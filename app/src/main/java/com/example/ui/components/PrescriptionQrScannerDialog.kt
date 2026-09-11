package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.local.InitialData
import com.example.data.model.Pharmacy
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.util.DemoPrescriptionPreset
import com.example.ui.util.PrescriptionQrParser
import com.example.ui.util.QrCodeScannerUtil
import com.example.ui.util.ScannedPrescriptionData
import com.example.ui.viewmodel.PharmaViewModel
import java.util.Locale
import java.util.concurrent.Executors

enum class ScannerPosition(val title: String, val badge: String) {
    HIGH("Haut", "Position Haute"),
    CENTER("Centré", "Position Centrée"),
    LOW("Bas", "Position Basse")
}

enum class ScannerFrameFormat(val title: String, val subtitle: String) {
    SQUARE("QR Ordonnance (1:1)", "Idéal pour codes QR d'ordonnance"),
    BARCODE_BOX("Boîte Médicament (Horizontal)", "Spécial code-barres (EAN-13 / CIP) sur boîte"),
    DOCUMENT("Ordonnance A4 (4:3)", "Optimal pour feuilles d'ordonnance & codes larges")
}

enum class ScannerScanMode(val title: String, val badge: String) {
    QR_OFFICIAL("QR Code Officiel", "SEN-PHARMA"),
    ALL_CODES("Tous types de codes", "QR + Barcode"),
    HIGH_CONTRAST("Haute Sensibilité", "Contraste max")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionQrScannerDialog(
    viewModel: PharmaViewModel,
    onDismiss: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // États du scanner & paramètres de capture
    var isTorchOn by remember { mutableStateOf(false) }
    var useFrontCamera by remember { mutableStateOf(false) }
    var activeCamera by remember { mutableStateOf<Camera?>(null) }
    var isAnalyzing by remember { mutableStateOf(true) }

    // Paramètres avancés du scanner
    var showSettingsSheet by remember { mutableStateOf(false) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var isBeepEnabled by remember { mutableStateOf(true) }
    var isVibrationEnabled by remember { mutableStateOf(true) }
    var showAlignmentGrid by remember { mutableStateOf(true) }
    var showLaser by remember { mutableStateOf(true) }
    var scannerPosition by remember { mutableStateOf(ScannerPosition.CENTER) }
    var frameFormat by remember { mutableStateOf(ScannerFrameFormat.SQUARE) }
    var scanMode by remember { mutableStateOf(ScannerScanMode.ALL_CODES) }
    var autoAddToCartDirectly by remember { mutableStateOf(false) }

    var scannedResult by remember { mutableStateOf<ScannedPrescriptionData?>(null) }
    var isAddingToCart by remember { mutableStateOf(false) }
    var showPresetSelector by remember { mutableStateOf(false) }
    var selectedPharmacy by remember { mutableStateOf(InitialData.pharmacies.first()) }
    var statusFeedbackMessage by remember { mutableStateOf<String?>(null) }

    // Feedback haptique & auditif médical
    fun triggerVibration() {
        if (!isVibrationEnabled) return
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    fun triggerBeep() {
        if (!isBeepEnabled) return
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 130)
        } catch (_: Exception) {}
    }

    fun applyZoom(ratio: Float) {
        zoomLevel = ratio
        try {
            activeCamera?.cameraControl?.setZoomRatio(ratio)
        } catch (_: Exception) {
            try {
                val linear = ((ratio - 1.0f) / 2.0f).coerceIn(0f, 1f)
                activeCamera?.cameraControl?.setLinearZoom(linear)
            } catch (_: Exception) {}
        }
    }

    fun handleQrDecoded(rawText: String) {
        if (!isAnalyzing || rawText.isBlank()) return
        isAnalyzing = false
        triggerVibration()
        triggerBeep()
        val parsed = PrescriptionQrParser.parse(rawText)
        if (parsed.pharmacyId != null) {
            val matchedPharm = InitialData.pharmacies.find { it.id == parsed.pharmacyId }
            if (matchedPharm != null) selectedPharmacy = matchedPharm
        } else if (parsed.pharmacyName != null) {
            val matchedPharm = InitialData.pharmacies.find { it.name.contains(parsed.pharmacyName, ignoreCase = true) }
            if (matchedPharm != null) selectedPharmacy = matchedPharm
        }
        scannedResult = parsed

        // Option d'ajout direct au panier selon les paramètres du scanner
        if (autoAddToCartDirectly) {
            viewModel.addPrescriptionQrToCart(
                scannedData = parsed,
                customPharmacy = selectedPharmacy,
                onComplete = { count, pharmName ->
                    Toast.makeText(
                        context,
                        "✓ $count médicaments ajoutés au panier ($pharmName) !",
                        Toast.LENGTH_LONG
                    ).show()
                    onDismiss()
                    onNavigateToCart()
                }
            )
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, "Permission caméra requise pour scanner en direct.", Toast.LENGTH_SHORT).show()
        }
    }

    // Photo capture launcher (system camera app fallback)
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val decoded = QrCodeScannerUtil.decodeBitmap(bitmap)
            if (decoded != null) {
                handleQrDecoded(decoded)
            } else {
                Toast.makeText(context, "Aucun QR Code valide détecté sur la photo. Essayez d'ajuster la lumière.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Gallery image picker launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    val decoded = QrCodeScannerUtil.decodeBitmap(bitmap)
                    if (decoded != null) {
                        handleQrDecoded(decoded)
                    } else {
                        Toast.makeText(context, "Aucun QR code d'ordonnance trouvé dans l'image sélectionnée.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erreur de lecture de l'image: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("prescription_qr_scanner_dialog"),
            color = Color(0xFF0F172A)
        ) {
            if (scannedResult != null) {
                // Écran de résultat et validation d'ajout au panier
                ScannedPrescriptionResultView(
                    data = scannedResult!!,
                    selectedPharmacy = selectedPharmacy,
                    onSelectPharmacy = { selectedPharmacy = it },
                    isAddingToCart = isAddingToCart,
                    onConfirmAddToCart = {
                        isAddingToCart = true
                        viewModel.addPrescriptionQrToCart(
                            scannedData = scannedResult!!,
                            customPharmacy = selectedPharmacy,
                            onComplete = { count, pharmName ->
                                isAddingToCart = false
                                Toast.makeText(
                                    context,
                                    "✓ $count médicaments ajoutés au panier auprès de $pharmName !",
                                    Toast.LENGTH_LONG
                                ).show()
                                onDismiss()
                                onNavigateToCart()
                            }
                        )
                    },
                    onRescan = {
                        scannedResult = null
                        isAnalyzing = true
                    },
                    onClose = onDismiss
                )
            } else {
                // Écran Caméra Scanner en direct
                Box(modifier = Modifier.fillMaxSize()) {
                    if (hasCameraPermission) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                val previewView = PreviewView(ctx).apply {
                                    scaleType = PreviewView.ScaleType.FILL_CENTER
                                }
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                val cameraExecutor = Executors.newSingleThreadExecutor()

                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }

                                    val imageAnalysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()

                                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                        if (isAnalyzing) {
                                            val decoded = QrCodeScannerUtil.decodeImageProxy(imageProxy)
                                            if (!decoded.isNullOrBlank()) {
                                                previewView.post {
                                                    handleQrDecoded(decoded)
                                                }
                                            }
                                        }
                                        imageProxy.close()
                                    }

                                    val cameraSelector = if (useFrontCamera) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }

                                    try {
                                        cameraProvider.unbindAll()
                                        val cam = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            imageAnalysis
                                        )
                                         activeCamera = cam
                                        cam.cameraControl.enableTorch(isTorchOn)
                                        applyZoom(zoomLevel)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(ctx))

                                previewView
                            },
                            update = {
                                activeCamera?.cameraControl?.enableTorch(isTorchOn)
                                applyZoom(zoomLevel)
                            }
                        )
                    } else {
                        // Pas de permission caméra
                        CameraPermissionFallbackCard(
                            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            onOpenGallery = { pickImageLauncher.launch("image/*") },
                            onOpenPreset = { showPresetSelector = true }
                        )
                    }

                    // Viseur & Laser Scanner avec Paramètres Alignés
                    CameraScannerOverlay(
                        isAnalyzing = isAnalyzing,
                        isTorchOn = isTorchOn,
                        useFrontCamera = useFrontCamera,
                        zoomLevel = zoomLevel,
                        isBeepEnabled = isBeepEnabled,
                        isVibrationEnabled = isVibrationEnabled,
                        showAlignmentGrid = showAlignmentGrid,
                        showLaser = showLaser,
                        frameFormat = frameFormat,
                        scanMode = scanMode,
                        scannerPosition = scannerPosition,
                        onTorchToggle = {
                            isTorchOn = !isTorchOn
                            activeCamera?.cameraControl?.enableTorch(isTorchOn)
                        },
                        onFlipCamera = {
                            useFrontCamera = !useFrontCamera
                        },
                        onZoomChange = { newZoom ->
                            applyZoom(newZoom)
                        },
                        onToggleGrid = {
                            showAlignmentGrid = !showAlignmentGrid
                        },
                        onToggleBeep = {
                            isBeepEnabled = !isBeepEnabled
                        },
                        onToggleVibration = {
                            isVibrationEnabled = !isVibrationEnabled
                        },
                        onToggleFormat = {
                            frameFormat = when (frameFormat) {
                                ScannerFrameFormat.SQUARE -> ScannerFrameFormat.BARCODE_BOX
                                ScannerFrameFormat.BARCODE_BOX -> ScannerFrameFormat.DOCUMENT
                                ScannerFrameFormat.DOCUMENT -> ScannerFrameFormat.SQUARE
                            }
                        },
                        onSelectFormat = { newFormat ->
                            frameFormat = newFormat
                        },
                        onPositionChange = { newPos ->
                            scannerPosition = newPos
                        },
                        onOpenSettings = {
                            showSettingsSheet = true
                        },
                        onClose = onDismiss,
                        onTakePicture = {
                            takePictureLauncher.launch(null)
                        },
                        onPickGallery = {
                            pickImageLauncher.launch("image/*")
                        },
                        onOpenPresets = {
                            showPresetSelector = true
                        }
                    )
                }
            }

            // Bottom sheet pour choisir une ordonnance ou boîte test (démonstration instantanée)
            if (showPresetSelector) {
                DemoPrescriptionsBottomSheet(
                    onDismiss = { showPresetSelector = false },
                    onSelectPreset = { preset ->
                        showPresetSelector = false
                        handleQrDecoded(preset.jsonPayload)
                    }
                )
            }

            // Volet modal des Paramètres du Scanner (Optique, Alignement, Retours)
            if (showSettingsSheet) {
                ScannerSettingsBottomSheet(
                    zoomLevel = zoomLevel,
                    onZoomChange = { applyZoom(it) },
                    isTorchOn = isTorchOn,
                    onTorchToggle = {
                        isTorchOn = !isTorchOn
                        activeCamera?.cameraControl?.enableTorch(isTorchOn)
                    },
                    useFrontCamera = useFrontCamera,
                    onFlipCamera = { useFrontCamera = !useFrontCamera },
                    isBeepEnabled = isBeepEnabled,
                    onToggleBeep = { isBeepEnabled = !isBeepEnabled },
                    isVibrationEnabled = isVibrationEnabled,
                    onToggleVibration = { isVibrationEnabled = !isVibrationEnabled },
                    showAlignmentGrid = showAlignmentGrid,
                    onToggleGrid = { showAlignmentGrid = !showAlignmentGrid },
                    showLaser = showLaser,
                    onToggleLaser = { showLaser = !showLaser },
                    scannerPosition = scannerPosition,
                    onSelectPosition = { scannerPosition = it },
                    frameFormat = frameFormat,
                    onSelectFrameFormat = { frameFormat = it },
                    scanMode = scanMode,
                    onSelectScanMode = { scanMode = it },
                    autoAddToCart = autoAddToCartDirectly,
                    onToggleAutoAddToCart = { autoAddToCartDirectly = !autoAddToCartDirectly },
                    onResetDefaults = {
                        zoomLevel = 1.0f
                        applyZoom(1.0f)
                        isTorchOn = false
                        activeCamera?.cameraControl?.enableTorch(false)
                        useFrontCamera = false
                        isBeepEnabled = true
                        isVibrationEnabled = true
                        showAlignmentGrid = true
                        showLaser = true
                        scannerPosition = ScannerPosition.CENTER
                        frameFormat = ScannerFrameFormat.SQUARE
                        scanMode = ScannerScanMode.ALL_CODES
                        autoAddToCartDirectly = false
                        Toast.makeText(context, "Paramètres réinitialisés par défaut", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { showSettingsSheet = false }
                )
            }
        }
    }
}

@Composable
private fun CameraScannerOverlay(
    isAnalyzing: Boolean,
    isTorchOn: Boolean,
    useFrontCamera: Boolean,
    zoomLevel: Float,
    isBeepEnabled: Boolean,
    isVibrationEnabled: Boolean,
    showAlignmentGrid: Boolean,
    showLaser: Boolean,
    frameFormat: ScannerFrameFormat,
    scanMode: ScannerScanMode,
    scannerPosition: ScannerPosition,
    onTorchToggle: () -> Unit,
    onFlipCamera: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onToggleGrid: () -> Unit,
    onToggleBeep: () -> Unit,
    onToggleVibration: () -> Unit,
    onToggleFormat: () -> Unit,
    onSelectFormat: (ScannerFrameFormat) -> Unit,
    onPositionChange: (ScannerPosition) -> Unit,
    onOpenSettings: () -> Unit,
    onClose: () -> Unit,
    onTakePicture: () -> Unit,
    onPickGallery: () -> Unit,
    onOpenPresets: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_animation")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_offset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Barre supérieure (Hautement arrangée et parfaitement alignée)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bouton Fermer
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("close_qr_scanner_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White, modifier = Modifier.size(20.dp))
            }

            // Badge central titre + état live
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, Color(0xFFFDE047).copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isAnalyzing) Color(0xFF22C55E) else Color(0xFFEAB308))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    if (frameFormat == ScannerFrameFormat.BARCODE_BOX) Icons.Default.Medication else Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (frameFormat == ScannerFrameFormat.BARCODE_BOX) "Scan Code-barres Boîte" else "Scan QR Ordonnance",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                )
            }

            // Actions rapides supérieures (Torche, Caméra, Paramètres)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Torche / Flash
                IconButton(
                    onClick = onTorchToggle,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isTorchOn) Color(0xFFFDE047) else Color.Black.copy(alpha = 0.55f))
                        .border(1.dp, if (isTorchOn) Color(0xFFFDE047) else Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash Torche",
                        tint = if (isTorchOn) Color(0xFF78350F) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Inverser Caméra
                IconButton(
                    onClick = onFlipCamera,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.FlipCameraAndroid,
                        contentDescription = "Changer Caméra",
                        tint = if (useFrontCamera) Color(0xFFFDE047) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Bouton Paramètres du Scanner
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.5.dp, Color(0xFFFDE047), CircleShape)
                        .testTag("scanner_settings_button")
                ) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Paramètres du Scanner",
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. Zone centrale : Sélecteurs + Viseur Cache Jaune repositionnable
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Barre supérieure des commandes : Mode de visée (QR vs Boîte) & Position du cache
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Onglets Mode de Visée : QR Ordonnance vs Boîte Médicament
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isQr = frameFormat == ScannerFrameFormat.SQUARE
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isQr) MedicalTealPrimary else Color.Transparent)
                            .clickable { onSelectFormat(ScannerFrameFormat.SQUARE) }
                            .padding(horizontal = 11.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "QR Ordonnance",
                                fontSize = 11.sp,
                                fontWeight = if (isQr) FontWeight.Bold else FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }

                    val isBox = frameFormat == ScannerFrameFormat.BARCODE_BOX
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isBox) Color(0xFFFDE047) else Color.Transparent)
                            .clickable { onSelectFormat(ScannerFrameFormat.BARCODE_BOX) }
                            .padding(horizontal = 11.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Medication,
                                contentDescription = null,
                                tint = if (isBox) Color(0xFF78350F) else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Boîte Médicament",
                                fontSize = 11.sp,
                                fontWeight = if (isBox) FontWeight.Black else FontWeight.Medium,
                                color = if (isBox) Color(0xFF78350F) else Color.White
                            )
                        }
                    }
                }

                // Sélecteur dynamique de Position du Cache Jaune (Haut / Centré / Bas)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(1.dp, Color(0xFFFDE047).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Position Cache :",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFDE047)
                    )
                    ScannerPosition.entries.forEach { pos ->
                        val isSelected = scannerPosition == pos
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFFFDE047) else Color.White.copy(alpha = 0.08f))
                                .clickable { onPositionChange(pos) }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = pos.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF78350F) else Color.White
                            )
                        }
                    }
                }
            }

            // Dimensions adaptées au format ciblé
            val (reticleWidth, reticleHeight) = when (frameFormat) {
                ScannerFrameFormat.SQUARE -> 265.dp to 265.dp
                ScannerFrameFormat.BARCODE_BOX -> 315.dp to 155.dp
                ScannerFrameFormat.DOCUMENT -> 295.dp to 220.dp
            }

            // Décalage vertical du Cache Jaune selon la position choisie
            val verticalOffset = when (scannerPosition) {
                ScannerPosition.HIGH -> (-38).dp
                ScannerPosition.CENTER -> 0.dp
                ScannerPosition.LOW -> 38.dp
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = verticalOffset),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Badge du Cache Jaune Actif
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFDE047))
                        .padding(horizontal = 10.dp, vertical = 3.5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (frameFormat == ScannerFrameFormat.BARCODE_BOX) Icons.Default.Medication else Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color(0xFF78350F),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (frameFormat == ScannerFrameFormat.BARCODE_BOX) "CACHE JAUNE • CODE-BARRES BOÎTE" else "CACHE JAUNE • QR ORDONNANCE",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Le Viseur Cache Jaune
                Box(
                    modifier = Modifier
                        .size(width = reticleWidth, height = reticleHeight)
                        .clip(RoundedCornerShape(22.dp))
                        .border(2.5.dp, Color(0xFFFDE047), RoundedCornerShape(22.dp))
                        .background(Color.Black.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    // 4 Coins visuels accentués en or/jaune vif
                    ReticleCorners(color = Color(0xFFFDE047))

                    // Grille d'aide à l'alignement
                    if (showAlignmentGrid) {
                        AlignmentGrid(color = Color.White.copy(alpha = 0.22f))
                    }

                    // Laser animé lumineux
                    if (isAnalyzing && showLaser) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.94f)
                                .height(3.dp)
                                .align(Alignment.TopCenter)
                                .padding(top = (laserOffset * (reticleHeight.value - 20)).dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0xFFFDE047),
                                            MedicalTealPrimary,
                                            Color.White,
                                            MedicalTealPrimary,
                                            Color(0xFFFDE047),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Icône centrale indicative
                    Icon(
                        if (frameFormat == ScannerFrameFormat.BARCODE_BOX) Icons.Default.Medication else Icons.Default.QrCode,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.18f),
                        modifier = Modifier.size(if (frameFormat == ScannerFrameFormat.BARCODE_BOX) 65.dp else 85.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bandeau d'instructions & d'état du scanner
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, Color(0xFFFDE047).copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CenterFocusStrong,
                            contentDescription = null,
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (frameFormat == ScannerFrameFormat.BARCODE_BOX) {
                                "Placez le code-barres (EAN-13/CIP) de la boîte dans le cadre jaune (${String.format(Locale.US, "%.1f", zoomLevel)}x)"
                            } else {
                                "Placez le QR Code de l'ordonnance dans le cadre jaune (${String.format(Locale.US, "%.1f", zoomLevel)}x)"
                            },
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 3. Commandes inférieures (Démo Presets, Photo, Galerie)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bouton de démonstration / presets pour tester sans imprimer
            Button(
                onClick = onOpenPresets,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F766E),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("open_test_prescriptions_button")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFFDE047))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tester QR Ordonnance ou Code-barres Boîte",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onTakePicture,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Prendre photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onPickGallery,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Depuis Galerie", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun AlignmentGrid(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.22f)
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        val w = size.width
        val h = size.height

        // 2 lignes verticales (règle des tiers)
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w / 3f, 0f),
            end = androidx.compose.ui.geometry.Offset(w / 3f, h),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(2f * w / 3f, 0f),
            end = androidx.compose.ui.geometry.Offset(2f * w / 3f, h),
            strokeWidth = 1.dp.toPx()
        )

        // 2 lignes horizontales (règle des tiers)
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, h / 3f),
            end = androidx.compose.ui.geometry.Offset(w, h / 3f),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, 2f * h / 3f),
            end = androidx.compose.ui.geometry.Offset(w, 2f * h / 3f),
            strokeWidth = 1.dp.toPx()
        )

        // Réticule central d'alignement fin
        val cx = w / 2f
        val cy = h / 2f
        val crossLen = 14.dp.toPx()
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = androidx.compose.ui.geometry.Offset(cx - crossLen, cy),
            end = androidx.compose.ui.geometry.Offset(cx + crossLen, cy),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = androidx.compose.ui.geometry.Offset(cx, cy - crossLen),
            end = androidx.compose.ui.geometry.Offset(cx, cy + crossLen),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScannerSettingsBottomSheet(
    zoomLevel: Float,
    onZoomChange: (Float) -> Unit,
    isTorchOn: Boolean,
    onTorchToggle: () -> Unit,
    useFrontCamera: Boolean,
    onFlipCamera: () -> Unit,
    isBeepEnabled: Boolean,
    onToggleBeep: () -> Unit,
    isVibrationEnabled: Boolean,
    onToggleVibration: () -> Unit,
    showAlignmentGrid: Boolean,
    onToggleGrid: () -> Unit,
    showLaser: Boolean,
    onToggleLaser: () -> Unit,
    scannerPosition: ScannerPosition,
    onSelectPosition: (ScannerPosition) -> Unit,
    frameFormat: ScannerFrameFormat,
    onSelectFrameFormat: (ScannerFrameFormat) -> Unit,
    scanMode: ScannerScanMode,
    onSelectScanMode: (ScannerScanMode) -> Unit,
    autoAddToCart: Boolean,
    onToggleAutoAddToCart: () -> Unit,
    onResetDefaults: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F172A),
        contentColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // En-tête des paramètres
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MedicalTealPrimary.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MedicalEmeraldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Paramètres du Scanner",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Alignement, optique & détection médicale",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1 : CADRAGE & REPÈRES D'ALIGNEMENT
            Text(
                text = "1. CADRAGE & POSITION DU CACHE JAUNE",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MedicalEmeraldAccent,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Format du viseur optique",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Adapte les proportions du cadre au document ou boîte",
                        fontSize = 11.5.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScannerFrameFormat.entries.forEach { format ->
                            val isSelected = frameFormat == format
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectFrameFormat(format) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MedicalTealPrimary.copy(alpha = 0.25f) else Color(0xFF0F172A)
                                ),
                                border = BorderStroke(
                                    1.2.dp,
                                    if (isSelected) MedicalTealPrimary else Color(0xFF334155)
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = format.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isSelected) MedicalEmeraldAccent else Color.White
                                        )
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isSelected) MedicalEmeraldAccent else Color(0xFF64748B),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = format.subtitle,
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF94A3B8),
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Position du Cache Jaune",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Ajuste l'emplacement vertical du viseur (Haut, Centré, Bas)",
                        fontSize = 11.5.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScannerPosition.entries.forEach { pos ->
                            val isSelected = scannerPosition == pos
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectPosition(pos) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFFDE047).copy(alpha = 0.2f) else Color(0xFF0F172A)
                                ),
                                border = BorderStroke(
                                    1.2.dp,
                                    if (isSelected) Color(0xFFFDE047) else Color(0xFF334155)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = pos.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color(0xFFFDE047) else Color.White
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = pos.badge,
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF94A3B8),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Switch Grille
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.GridOn,
                                contentDescription = null,
                                tint = MedicalEmeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Grille de repère d'alignement",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Quadrillage 3x3 pour positionner le QR bien droit",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Switch(
                            checked = showAlignmentGrid,
                            onCheckedChange = { onToggleGrid() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MedicalTealPrimary,
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Switch Laser
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Sensors,
                                contentDescription = null,
                                tint = MedicalEmeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Faisceau laser lumineux animé",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Balayage visuel en continu dans le cadre",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Switch(
                            checked = showLaser,
                            onCheckedChange = { onToggleLaser() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MedicalTealPrimary,
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 2 : OPTIQUE & OBJECTIF CAMÉRA
            Text(
                text = "2. OPTIQUE & OBJECTIF CAMÉRA",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MedicalEmeraldAccent,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Zoom
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Niveau de zoom numérique",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.1f", zoomLevel)}x",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = MedicalEmeraldAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Boutons de zoom prédéfinis
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1.0f to "1.0x (Normal)", 1.5f to "1.5x (Optimal)", 2.0f to "2.0x", 3.0f to "3.0x (Macro)").forEach { (ratio, label) ->
                            val isSelected = kotlin.math.abs(zoomLevel - ratio) < 0.12f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MedicalTealPrimary else Color(0xFF0F172A))
                                    .border(1.dp, if (isSelected) MedicalEmeraldAccent else Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .clickable { onZoomChange(ratio) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = zoomLevel,
                        onValueChange = onZoomChange,
                        valueRange = 1.0f..3.0f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = MedicalEmeraldAccent,
                            activeTrackColor = MedicalTealPrimary,
                            inactiveTrackColor = Color(0xFF334155)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Switch Torche
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = null,
                                tint = if (isTorchOn) Color(0xFFFDE047) else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Torche / Éclairage d'appoint",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Facilite la détection dans les environnements sombres",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Switch(
                            checked = isTorchOn,
                            onCheckedChange = { onTorchToggle() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFDE047),
                                checkedTrackColor = MedicalTealPrimary,
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Switch Caméra dorsale / frontale
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.FlipCameraAndroid,
                                contentDescription = null,
                                tint = MedicalEmeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Capteur actif",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (useFrontCamera) "Caméra Frontale active" else "Caméra Arrière HD (Recommandée)",
                                    fontSize = 11.sp,
                                    color = if (useFrontCamera) Color(0xFFFDE047) else Color(0xFF94A3B8)
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = onFlipCamera,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MedicalTealPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (useFrontCamera) "Passer à l'arrière" else "Passer à l'avant",
                                fontSize = 11.sp,
                                color = MedicalEmeraldAccent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 3 : SIGNAUX & RETOURS SENSORIELS
            Text(
                text = "3. SIGNAUX & RETOURS SENSORIELS",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MedicalEmeraldAccent,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Bip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isBeepEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = null,
                                tint = MedicalEmeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Bip sonore de confirmation",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Tonalité audio médicale dès détection du QR code",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Switch(
                            checked = isBeepEnabled,
                            onCheckedChange = { onToggleBeep() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MedicalTealPrimary,
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Vibration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Vibration,
                                contentDescription = null,
                                tint = MedicalEmeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Vibration tactile (Haptique)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Impulsion vibration lors de la lecture réussie",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Switch(
                            checked = isVibrationEnabled,
                            onCheckedChange = { onToggleVibration() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MedicalTealPrimary,
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 4 : MODES DE DÉTECTION & AJOUT PANIER
            Text(
                text = "4. PROTOCOLE & AUTOMATISATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MedicalEmeraldAccent,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Mode de décodage optique",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ScannerScanMode.entries.forEach { mode ->
                        val isSelected = scanMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MedicalTealPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { onSelectScanMode(mode) }
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) MedicalEmeraldAccent else Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = mode.title,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) MedicalTealPrimary else Color(0xFF334155))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = mode.badge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Switch Ajout Direct au panier
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = MedicalEmeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Ajout direct au panier",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Transférer immédiatement les médicaments sans écran de validation",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Switch(
                            checked = autoAddToCart,
                            onCheckedChange = { onToggleAutoAddToCart() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MedicalTealPrimary,
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions inférieures du volet paramètres
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onResetDefaults,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF475569)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCBD5E1))
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rétablir", fontSize = 12.sp)
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalTealPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Appliquer & Fermer", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReticleCorners(color: Color) {
    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Top Left
        Box(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopStart)
                .border(3.dp, color, RoundedCornerShape(topStart = 8.dp))
        )
        // Top Right
        Box(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd)
                .border(3.dp, color, RoundedCornerShape(topEnd = 8.dp))
        )
        // Bottom Left
        Box(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomStart)
                .border(3.dp, color, RoundedCornerShape(bottomStart = 8.dp))
        )
        // Bottom Right
        Box(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomEnd)
                .border(3.dp, color, RoundedCornerShape(bottomEnd = 8.dp))
        )
    }
}

@Composable
private fun CameraPermissionFallbackCard(
    onRequestPermission: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenPreset: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, BorderSoft)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = MedicalTealPrimary,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Accès Caméra Requis",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pour numériser le QR code de votre ordonnance médicale, autorisez l'utilisation de la caméra de votre appareil.",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRequestPermission,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Autoriser la Caméra", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenPreset,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tester sans caméra (Ordonnances Types)")
                }
            }
        }
    }
}

@Composable
private fun ScannedPrescriptionResultView(
    data: ScannedPrescriptionData,
    selectedPharmacy: Pharmacy,
    onSelectPharmacy: (Pharmacy) -> Unit,
    isAddingToCart: Boolean,
    onConfirmAddToCart: () -> Unit,
    onRescan: () -> Unit,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scanned_prescription_result_view"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = VerifiedBadgeGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Ordonnance QR Détectée",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Authentification réussie • ${data.verificationCode}",
                            fontSize = 11.5.sp,
                            color = VerifiedBadgeGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondaryMuted)
                }
            }

            HorizontalDivider(color = BorderSoft)

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Doctor & Patient Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderSoft)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PRESCRIPTION MÉDICALE CERTIFIÉE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalTealDark
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Médecin Prescripteur",
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                                Text(
                                    text = data.doctorName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = data.doctorSpecialty,
                                    fontSize = 12.sp,
                                    color = TextOnWhiteSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Patient",
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                                Text(
                                    text = data.patientName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Date : ${data.prescriptionDate}",
                                    fontSize = 11.5.sp,
                                    color = TextSecondaryMuted
                                )
                            }
                        }
                    }
                }

                // Destination Pharmacy
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F6)),
                    border = BorderStroke(1.dp, MedicalTealPrimary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MedicalTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Délivrance & Préparation :",
                                fontSize = 11.sp,
                                color = MedicalTealDark
                            )
                            Text(
                                text = selectedPharmacy.name,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "${selectedPharmacy.address} • ${selectedPharmacy.region}",
                                fontSize = 11.5.sp,
                                color = TextOnWhiteSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Prescribed Medicines list header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Médicaments prescrits (${data.items.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "${data.totalItemsCount} unités au total",
                        fontSize = 12.sp,
                        color = TextSecondaryMuted
                    )
                }

                // Medicines cards
                data.items.forEach { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, BorderSoft)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MedicalTealLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Medication,
                                        contentDescription = null,
                                        tint = MedicalTealPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.medicineName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = TextPrimaryDark
                                    )
                                    Text(
                                        text = item.posology,
                                        fontSize = 11.5.sp,
                                        color = TextSecondaryMuted,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MedicalTealLight)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Qté: ${item.quantity}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalTealDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${item.estimatedTotalPrice} FCFA",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalTealPrimary
                                )
                            }
                        }
                    }
                }

                // Posology / Doctor Notes
                if (data.notes.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Instructions du médecin :",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.notes,
                                fontSize = 12.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Total Summary Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderSoft)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total estimé des médicaments :",
                                fontSize = 12.sp,
                                color = TextSecondaryMuted
                            )
                            Text(
                                text = "Délivrance certifiée sur ordonnance",
                                fontSize = 11.sp,
                                color = VerifiedBadgeGreen
                            )
                        }
                        Text(
                            text = "${data.totalPrescriptionCost} FCFA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalTealPrimary
                        )
                    }
                }
            }

            // Bottom Actions: Add to Cart & Rescan
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, BorderSoft)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onConfirmAddToCart,
                        enabled = !isAddingToCart,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("add_prescription_medicines_to_cart_button")
                    ) {
                        if (isAddingToCart) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ajout au panier en cours...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ajouter ${data.totalItemsCount} médicaments au Panier",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = onRescan,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scanner une autre ordonnance", fontSize = 12.sp, color = MedicalTealPrimary)
                        }

                        TextButton(
                            onClick = onClose,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Fermer", fontSize = 12.sp, color = TextSecondaryMuted)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DemoPrescriptionsBottomSheet(
    onDismiss: () -> Unit,
    onSelectPreset: (DemoPrescriptionPreset) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ordonnances & Boîtes Types (Sénégal)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Sélectionnez un modèle pour simuler un scan QR ou Code-barres",
                        fontSize = 12.sp,
                        color = TextSecondaryMuted
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrescriptionQrParser.demoPresets.forEach { preset ->
                val isBarcode = preset.isBarcode
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onSelectPreset(preset) }
                        .testTag("demo_preset_${preset.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isBarcode) Color(0xFFFEFCE8) else Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, if (isBarcode) Color(0xFFFDE047) else BorderSoft)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isBarcode) Color(0xFFFEF08A) else MedicalTealLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (isBarcode) Icons.Default.Medication else Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = if (isBarcode) Color(0xFF854D0E) else MedicalTealPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = preset.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = TextPrimaryDark
                                    )
                                    if (isBarcode) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFDE047))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text("CODE-BARRES", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color(0xFF78350F))
                                        }
                                    }
                                }
                                Text(
                                    text = preset.subtitle,
                                    fontSize = 12.sp,
                                    color = TextOnWhiteSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = preset.doctor,
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                            }
                        }

                        Button(
                            onClick = { onSelectPreset(preset) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBarcode) Color(0xFFD97706) else MedicalTealPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(if (isBarcode) "Scanner Boîte" else "Scanner QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
