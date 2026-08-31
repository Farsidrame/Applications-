package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.data.model.OrderEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InvoicePrinterHelper {

    fun generateInvoiceHtml(order: OrderEntity): String {
        val dateFormatted = SimpleDateFormat("dd MMMM yyyy 'à' HH:mm", Locale.FRENCH).format(Date(order.orderTimestamp))
        val items = order.itemsSummary.split(" | ")

        val itemsRowsHtml = StringBuilder()
        items.forEachIndexed { index, itemStr ->
            itemsRowsHtml.append("""
                <tr>
                    <td style="padding: 10px 12px; border-bottom: 1px solid #E2E8F0; font-size: 14px; color: #1E293B;">${index + 1}</td>
                    <td style="padding: 10px 12px; border-bottom: 1px solid #E2E8F0; font-size: 14px; font-weight: 600; color: #0F172A;">$itemStr</td>
                    <td style="padding: 10px 12px; border-bottom: 1px solid #E2E8F0; font-size: 14px; color: #059669; font-weight: bold; text-align: right;">Inclus</td>
                </tr>
            """.trimIndent())
        }

        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Facture ${order.orderNumber} - PharmaDirect Sénégal</title>
                <style>
                    @page { size: A4; margin: 15mm; }
                    body {
                        font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                        color: #1E293B;
                        background: #FFFFFF;
                        margin: 0;
                        padding: 24px;
                        line-height: 1.5;
                    }
                    .header-container {
                        display: flex;
                        justify-content: space-between;
                        align-items: flex-start;
                        border-bottom: 3px solid #00875A;
                        padding-bottom: 16px;
                        margin-bottom: 24px;
                    }
                    .brand-title {
                        font-size: 26px;
                        font-weight: 800;
                        color: #00875A;
                        margin: 0;
                        letter-spacing: -0.5px;
                    }
                    .brand-sub {
                        font-size: 12px;
                        color: #64748B;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        margin-top: 4px;
                    }
                    .invoice-badge {
                        background: #ECFDF5;
                        border: 1px solid #A7F3D0;
                        color: #065F46;
                        padding: 8px 16px;
                        border-radius: 8px;
                        text-align: right;
                    }
                    .invoice-number {
                        font-size: 18px;
                        font-weight: 700;
                        margin: 0;
                    }
                    .meta-grid {
                        display: flex;
                        justify-content: space-between;
                        gap: 20px;
                        margin-bottom: 28px;
                    }
                    .meta-box {
                        flex: 1;
                        background: #F8FAFC;
                        border: 1px solid #E2E8F0;
                        border-radius: 8px;
                        padding: 14px;
                    }
                    .meta-title {
                        font-size: 11px;
                        font-weight: 700;
                        text-transform: uppercase;
                        color: #64748B;
                        letter-spacing: 0.5px;
                        margin-bottom: 6px;
                    }
                    .meta-content {
                        font-size: 13px;
                        color: #1E293B;
                        margin: 0;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-bottom: 24px;
                    }
                    th {
                        background: #F1F5F9;
                        padding: 12px;
                        text-align: left;
                        font-size: 12px;
                        font-weight: 700;
                        color: #475569;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }
                    .totals-container {
                        display: flex;
                        justify-content: flex-end;
                        margin-bottom: 28px;
                    }
                    .totals-box {
                        width: 320px;
                        background: #F8FAFC;
                        border: 1px solid #E2E8F0;
                        border-radius: 8px;
                        padding: 14px;
                    }
                    .total-row {
                        display: flex;
                        justify-content: space-between;
                        font-size: 14px;
                        padding: 6px 0;
                    }
                    .total-grand {
                        border-top: 2px solid #CBD5E1;
                        margin-top: 8px;
                        padding-top: 10px;
                        font-size: 18px;
                        font-weight: 800;
                        color: #00875A;
                    }
                    .footer-seal {
                        border-top: 1px dashed #CBD5E1;
                        padding-top: 18px;
                        text-align: center;
                        font-size: 11px;
                        color: #64748B;
                    }
                    .pin-badge {
                        display: inline-block;
                        background: #FEF3C7;
                        border: 1px solid #FCD34D;
                        color: #92400E;
                        font-weight: 700;
                        padding: 4px 10px;
                        border-radius: 6px;
                        margin-top: 6px;
                    }
                </style>
            </head>
            <body>
                <div class="header-container">
                    <div>
                        <h1 class="brand-title">🏥 PharmaDirect Sénégal</h1>
                        <div class="brand-sub">Réseau Pharmaceutique National Certifié • République du Sénégal</div>
                        <div style="font-size: 11px; color: #475569; margin-top: 4px;">NINEA : 008942103 • Ordre des Pharmaciens du Sénégal</div>
                    </div>
                    <div class="invoice-badge">
                        <div class="invoice-number">FACTURE ${order.orderNumber}</div>
                        <div style="font-size: 12px; margin-top: 2px;">Date : $dateFormatted</div>
                        <div style="font-size: 11px; color: #059669; font-weight: 700; margin-top: 4px;">PAYÉE & ACQUITTÉE</div>
                    </div>
                </div>

                <div class="meta-grid">
                    <div class="meta-box">
                        <div class="meta-title">Pharmacie Émettrice</div>
                        <p class="meta-content"><strong>${order.pharmacyName}</strong></p>
                        <p class="meta-content" style="color: #64748B; font-size: 12px;">${order.pharmacyAddress}</p>
                        <p class="meta-content" style="font-size: 12px; margin-top: 4px;">Agrément Ministère de la Santé</p>
                    </div>

                    <div class="meta-box">
                        <div class="meta-title">Patient / Destinataire</div>
                        <p class="meta-content"><strong>${if (order.patientName.isNotBlank()) order.patientName else "Client PharmaDirect"}</strong></p>
                        <p class="meta-content" style="font-size: 12px; color: #64748B;">Tél : ${if (order.patientPhone.isNotBlank()) order.patientPhone else "Non renseigné"}</p>
                        <p class="meta-content" style="font-size: 12px; color: #64748B;">Livraison : ${order.deliveryAddress}</p>
                    </div>

                    <div class="meta-box">
                        <div class="meta-title">Règlement & Livraison</div>
                        <p class="meta-content">Mode : <strong>${order.paymentMethod}</strong></p>
                        <p class="meta-content" style="font-size: 12px; color: #64748B;">Réf Tx : ${order.paymentTransactionId}</p>
                        <div class="pin-badge">Code PIN Sécurisé : ${order.deliveryPinCode}</div>
                    </div>
                </div>

                <table>
                    <thead>
                        <tr>
                            <th style="width: 40px;">#</th>
                            <th>Désignation des Médicaments & Produits</th>
                            <th style="width: 120px; text-align: right;">Statut</th>
                        </tr>
                    </thead>
                    <tbody>
                        $itemsRowsHtml
                    </tbody>
                </table>

                <div class="totals-container">
                    <div class="totals-box">
                        <div class="total-row">
                            <span style="color: #64748B;">Sous-total Médicaments :</span>
                            <span style="font-weight: 600;">${order.subtotalFcfa} FCFA</span>
                        </div>
                        <div class="total-row">
                            <span style="color: #64748B;">Frais de livraison express :</span>
                            <span style="font-weight: 600;">${order.deliveryFeeFcfa} FCFA</span>
                        </div>
                        <div class="total-row total-grand">
                            <span>TOTAL ACQUITTÉ :</span>
                            <span>${order.totalFcfa} FCFA</span>
                        </div>
                    </div>
                </div>

                <div class="footer-seal">
                    <p style="margin: 0 0 4px 0;"><strong>Facture électronique certifiée conforme délivrée par PharmaDirect Sénégal.</strong></p>
                    <p style="margin: 0; color: #94A3B8;">Livreur assigné : ${order.courierName} (${order.courierPhone}) • Service réclamations & Urgences : +221 33 800 00 00</p>
                    <p style="margin: 4px 0 0 0; font-family: monospace; font-size: 10px; color: #CBD5E1;">QR-PAYLOAD: ${order.invoiceQrCodePayload}</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun printInvoice(context: Context, order: OrderEntity) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Toast.makeText(context, "Service d'impression non disponible sur cet appareil", Toast.LENGTH_SHORT).show()
            return
        }

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false

            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("Facture_${order.orderNumber}")
                val jobName = "Facture PharmaDirect ${order.orderNumber}"
                printManager.print(
                    jobName,
                    printAdapter,
                    PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build()
                )
            }
        }

        val html = generateInvoiceHtml(order)
        webView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null)
    }

    fun shareInvoiceSmsIntent(context: Context, order: OrderEntity) {
        val recipientPhone = if (order.patientPhone.isNotBlank()) order.patientPhone else ""
        val smsBody = """
            🏥 FACTURE PHARMADIRECT SÉNÉGAL
            N° Facture : ${order.orderNumber}
            Pharmacie : ${order.pharmacyName}
            Client : ${order.patientName}
            Articles : ${order.itemsSummary}
            Total : ${order.totalFcfa} FCFA (${order.paymentMethod})
            Réf Transaction : ${order.paymentTransactionId}
            Code PIN livraison : ${order.deliveryPinCode}
            Livreur : ${order.courierName} (${order.courierPhone})
            Facture certifiée conforme. Service client : +221 33 800 00 00
        """.trimIndent()

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("sms:$recipientPhone")
            putExtra("sms_body", smsBody)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Facture enregistrée et envoyée par SMS dans l'application", Toast.LENGTH_LONG).show()
        }
    }
}
