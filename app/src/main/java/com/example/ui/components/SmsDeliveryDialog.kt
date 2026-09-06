package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SmsDeliveryNotification
import com.example.service.SmsDeliveryNotificationService
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen

@Composable
fun SmsDeliveryAlertDialog(
    sms: SmsDeliveryNotification,
    onDismiss: () -> Unit,
    onViewOrder: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("sms_delivery_alert_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = "SMS",
                            tint = VerifiedBadgeGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SMS de Livraison Reçu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Expéditeur: ${sms.sender} (Officiel)",
                            fontSize = 11.sp,
                            color = MedicalTealDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onDelete()
                        Toast.makeText(context, "SMS supprimé", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer ce SMS",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // SMS Notification Box with Android Message Style
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF1F8F6),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = MedicalTealPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Destinataire: ${sms.recipientPhone}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            }
                            Text(
                                text = sms.timestamp,
                                fontSize = 10.sp,
                                color = TextSecondaryMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFE0ECE8))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = sms.messageText,
                            fontSize = 12.sp,
                            color = TextPrimaryDark,
                            lineHeight = 18.sp,
                            fontFamily = FontFamily.SansSerif
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Operator info & Actions inside Dialog
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = VerifiedBadgeGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Réseau Mobile SN • Délivré",
                                    fontSize = 9.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("SMS PharmaDirect", sms.messageText)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Texte du SMS copié !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = SafeBlueSecondary, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = {
                                        SmsDeliveryNotificationService.openNativeSmsMessenger(context, sms.recipientPhone, sms.messageText)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = "App SMS", tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                modifier = Modifier.testTag("sms_alert_ok_button")
            ) {
                Text("Compris", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = {
                    onDelete()
                    Toast.makeText(context, "SMS supprimé", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                TextButton(onClick = {
                    SmsDeliveryNotificationService.openNativeSmsMessenger(context, sms.recipientPhone, sms.messageText)
                }) {
                    Text("Appli SMS", color = SafeBlueSecondary, fontSize = 12.sp)
                }
                TextButton(onClick = {
                    onDismiss()
                    onViewOrder()
                }) {
                    Text("Détails", color = MedicalTealDark, fontSize = 12.sp)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsInboxBottomSheet(
    smsList: List<SmsDeliveryNotification>,
    onDismiss: () -> Unit,
    onDeleteSms: (String) -> Unit = {},
    onDeleteBillingSms: () -> Unit = {},
    onClearAll: () -> Unit = {},
    onSimulateScenario: (SmsDeliveryNotificationService.DeliveryScenario) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var isSimulatorExpanded by remember { mutableStateOf(false) }

    val hasBillingSms = remember(smsList) {
        smsList.any { sms ->
            sms.sender.contains("PAY", ignoreCase = true) ||
            sms.orderNumber.startsWith("FACT-") ||
            sms.messageText.contains("Facture", ignoreCase = true) ||
            sms.messageText.contains("régler", ignoreCase = true) ||
            sms.messageText.contains("Paiement", ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("sms_inbox_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MedicalTealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = "SMS",
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Centre de Notifications SMS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "${smsList.size} notification(s) SMS enregistrée(s)",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            // Action row: Delete Billing SMS & Clear All
            if (smsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasBillingSms) {
                        OutlinedButton(
                            onClick = {
                                onDeleteBillingSms()
                                Toast.makeText(context, "SMS de facturation supprimés", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Supprimer SMS Facturation", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    TextButton(
                        onClick = {
                            onClearAll()
                            Toast.makeText(context, "Tous les SMS ont été effacés", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vider la boîte", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Simulator Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Simulateur de Notifications SMS Android",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MedicalTealDark
                            )
                        }
                        TextButton(
                            onClick = { isSimulatorExpanded = !isSimulatorExpanded },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(if (isSimulatorExpanded) "Masquer" else "Tester", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedicalTealPrimary)
                        }
                    }

                    if (isSimulatorExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Choisissez un scénario de notification SMS certifiée à simuler via le service Android :",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(SmsDeliveryNotificationService.DeliveryScenario.values()) { scenario ->
                                val (icon, label) = when (scenario) {
                                    SmsDeliveryNotificationService.DeliveryScenario.STANDARD_DELIVERED -> Icons.Default.Send to "Livraison Standard"
                                    SmsDeliveryNotificationService.DeliveryScenario.COLD_CHAIN_DELIVERED -> Icons.Default.AcUnit to "Chaîne du Froid 4.8°C"
                                    SmsDeliveryNotificationService.DeliveryScenario.NIGHT_DUTY_DELIVERED -> Icons.Default.Nightlight to "Garde de Nuit 24h"
                                    SmsDeliveryNotificationService.DeliveryScenario.PRESCRIPTION_VALIDATED -> Icons.Default.LocalPharmacy to "Ordonnance Validée"
                                    SmsDeliveryNotificationService.DeliveryScenario.INVOICE_AND_PAYMENT -> Icons.Default.ReceiptLong to "Facture & Paiement"
                                }
                                Button(
                                    onClick = {
                                        onSimulateScenario(scenario)
                                        Toast.makeText(context, "Notification SMS envoyée : ${scenario.title}", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFEFF4F2))
            Spacer(modifier = Modifier.height(14.dp))

            if (smsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = TextSecondaryMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucun SMS de livraison pour l'instant",
                            fontWeight = FontWeight.Medium,
                            color = TextSecondaryMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Utilisez le simulateur ci-dessus ou passez une commande pour recevoir des SMS.",
                            color = TextSecondaryMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(smsList, key = { it.id }) { sms ->
                        SmsItemCard(
                            sms = sms,
                            onDelete = {
                                onDeleteSms(sms.id)
                                Toast.makeText(context, "SMS supprimé", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmsItemCard(
    sms: SmsDeliveryNotification,
    onDelete: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBillingSms = sms.sender.contains("PAY", ignoreCase = true) ||
            sms.orderNumber.startsWith("FACT-") ||
            sms.messageText.contains("Facture", ignoreCase = true) ||
            sms.messageText.contains("régler", ignoreCase = true) ||
            sms.messageText.contains("Paiement", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBFB)),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isBillingSms) Color(0xFFFFCCBC) else Color(0xFFE0ECE8))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (isBillingSms) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isBillingSms) Icons.Default.ReceiptLong else Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (isBillingSms) Color(0xFFD32F2F) else VerifiedBadgeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = sms.sender,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isBillingSms) Color(0xFFC62828) else MedicalTealPrimary
                            )
                            if (isBillingSms) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFFEBEE),
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                ) {
                                    Text("FACTURE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }
                        Text(
                            text = "N° ${sms.orderNumber}",
                            fontSize = 10.sp,
                            color = MedicalTealDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sms.timestamp,
                        fontSize = 10.sp,
                        color = TextSecondaryMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Supprimer ce SMS",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = sms.messageText,
                fontSize = 12.sp,
                color = TextPrimaryDark,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF0F5F3))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Destinataire : ${sms.recipientPhone}",
                    fontSize = 10.sp,
                    color = TextSecondaryMuted
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("SMS PharmaDirect", sms.messageText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Texte du SMS copié !", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copier", fontSize = 10.sp)
                    }

                    Button(
                        onClick = {
                            SmsDeliveryNotificationService.openNativeSmsMessenger(context, sms.recipientPhone, sms.messageText)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Appli SMS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
