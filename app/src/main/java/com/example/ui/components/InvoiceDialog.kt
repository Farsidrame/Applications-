package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.OrderEntity
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceDialog(
    order: OrderEntity,
    onDismiss: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date(order.orderTimestamp))

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("invoice_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MedicalTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "Facture",
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Facture & Reçu Officiel",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Paiement en ligne acquitté",
                                style = MaterialTheme.typography.bodySmall,
                                color = VerifiedBadgeGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondaryMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Invoice paper container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFAFCFB))
                        .border(1.dp, Color(0xFFE0EAE7), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Pharmacy Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = order.pharmacyName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MedicalTealDark
                                )
                                Text(
                                    text = order.pharmacyAddress,
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                            }
                            CertifiedBadge(text = "Agréée Santé")
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFE5ECE9))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Metadata Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("N° COMMANDE", fontSize = 10.sp, color = TextSecondaryMuted, fontWeight = FontWeight.Bold)
                                Text(order.orderNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("DATE DU PAIEMENT", fontSize = 10.sp, color = TextSecondaryMuted, fontWeight = FontWeight.Bold)
                                Text(dateStr, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimaryDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("PATIENT / CLIENT", fontSize = 10.sp, color = TextSecondaryMuted, fontWeight = FontWeight.Bold)
                                Text("${order.patientName} (${order.patientPhone})", fontSize = 12.sp, color = TextPrimaryDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("ADRESSE DE LIVRAISON", fontSize = 10.sp, color = TextSecondaryMuted, fontWeight = FontWeight.Bold)
                        Text(order.deliveryAddress, fontSize = 12.sp, color = TextPrimaryDark)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFE5ECE9))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Items
                        Text("DÉTAIL DES MÉDICAMENTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedicalTealDark)
                        Spacer(modifier = Modifier.height(6.dp))

                        val itemList = order.itemsSummary.split(" | ")
                        itemList.forEach { itemText ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "• $itemText",
                                    fontSize = 12.sp,
                                    color = TextPrimaryDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFE5ECE9))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Payment Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sous-total médicaments:", fontSize = 12.sp, color = TextSecondaryMuted)
                            Text("${order.subtotalFcfa} FCFA", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Livraison express sécurisée:", fontSize = 12.sp, color = TextSecondaryMuted)
                            Text("${order.deliveryFeeFcfa} FCFA", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL PAYÉ EN LIGNE:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MedicalTealDark)
                            Text(
                                "${order.totalFcfa} FCFA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MedicalTealPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1F8F6))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Mode de règlement:", fontSize = 11.sp, color = TextSecondaryMuted)
                            Text(order.paymentMethod, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedicalTealDark)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Réf transaction: ${order.paymentTransactionId}",
                            fontSize = 10.sp,
                            color = TextSecondaryMuted,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // QR Code & Verification Stamp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QR Code Facture",
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Traçabilité Pharmaceutique",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalTealDark
                                )
                                Text(
                                    text = "Code PIN réception: ${order.deliveryPinCode}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFD84315)
                                )
                                Text(
                                    text = "À donner au livreur à l'arrivée",
                                    fontSize = 10.sp,
                                    color = TextSecondaryMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Conserver mon reçu", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
