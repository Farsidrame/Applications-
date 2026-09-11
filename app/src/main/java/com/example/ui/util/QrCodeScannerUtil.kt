package com.example.ui.util

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Utilitaire de décodage et d'encodage de QR Code et Code-barres (boîtes médicaments) utilisant ZXing Core.
 */
object QrCodeScannerUtil {

    // Formats supportés : QR Code d'ordonnance + Codes-barres 1D des boîtes de médicaments (EAN-13, EAN-8, Code 128, UPC...)
    val supportedFormats = listOf(
        BarcodeFormat.QR_CODE,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_39,
        BarcodeFormat.UPC_A,
        BarcodeFormat.UPC_E,
        BarcodeFormat.DATA_MATRIX
    )

    private val decodeHints = mapOf(
        DecodeHintType.POSSIBLE_FORMATS to supportedFormats,
        DecodeHintType.CHARACTER_SET to "UTF-8",
        DecodeHintType.TRY_HARDER to true
    )

    /**
     * Décode un QR code à partir d'un ImageProxy fourni par CameraX ImageAnalysis.
     */
    fun decodeImageProxy(imageProxy: ImageProxy): String? {
        return try {
            val plane = imageProxy.planes.getOrNull(0) ?: return null
            val buffer = plane.buffer
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride
            val width = imageProxy.width
            val height = imageProxy.height
            val rotation = imageProxy.imageInfo.rotationDegrees

            val cleanData = ByteArray(width * height)
            var destOffset = 0
            val rowData = ByteArray(rowStride)

            for (row in 0 until height) {
                buffer.position(row * rowStride)
                if (pixelStride == 1) {
                    buffer.get(cleanData, destOffset, width)
                    destOffset += width
                } else {
                    buffer.get(rowData, 0, minOf(rowStride, buffer.remaining()))
                    for (col in 0 until width) {
                        cleanData[destOffset++] = rowData[col * pixelStride]
                    }
                }
            }

            // Essayer avec orientation selon la rotation de la caméra
            val (orientedData, orientedW, orientedH) = rotateYData(cleanData, width, height, rotation)
            decodeByteArray(orientedData, orientedW, orientedH)
                ?: decodeByteArray(cleanData, width, height)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Décode un QR code ou code-barres (médicaments) à partir d'un Bitmap (capture photo ou galerie).
     */
    fun decodeBitmap(bitmap: Bitmap): String? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap, decodeHints)
            result.text
        } catch (e: Exception) {
            null
        }
    }

    private fun decodeByteArray(data: ByteArray, width: Int, height: Int): String? {
        return try {
            val source = PlanarYUVLuminanceSource(
                data, width, height, 0, 0, width, height, false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap, decodeHints)
            result.text
        } catch (e: Exception) {
            null
        }
    }

    private fun rotateYData(
        data: ByteArray,
        width: Int,
        height: Int,
        rotation: Int
    ): Triple<ByteArray, Int, Int> {
        return when (rotation) {
            90 -> {
                val rotated = ByteArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        rotated[x * height + height - y - 1] = data[x + y * width]
                    }
                }
                Triple(rotated, height, width)
            }
            180 -> {
                val rotated = ByteArray(width * height)
                val total = width * height
                for (i in 0 until total) {
                    rotated[total - 1 - i] = data[i]
                }
                Triple(rotated, width, height)
            }
            270 -> {
                val rotated = ByteArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        rotated[(width - x - 1) * height + y] = data[x + y * width]
                    }
                }
                Triple(rotated, height, width)
            }
            else -> Triple(data, width, height)
        }
    }

    /**
     * Génère un Bitmap QR code avec ZXing QRCodeWriter (pour démonstrations ou partage).
     */
    fun generateQrCodeBitmap(content: String, size: Int = 512): Bitmap {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1
        )
        val bitMatrix = QRCodeWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        )
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        return bitmap
    }
}
