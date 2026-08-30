package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SmsDeliveryNotification
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen

@Composable
fun SmsDeliveryAlertDialog(
    sms: SmsDeliveryNotification,
    onDismiss: () -> Unit,
    onViewOrder: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("sms_delivery_alert_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sms,
                        contentDescription = "SMS",
                        tint = VerifiedBadgeGreen,
                        modifier = Modifier.size(20.dp)
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
                        text = "Expéditeur: ${sms.sender}",
                        fontSize = 11.sp,
                        color = MedicalTealDark,
                        fontWeight = FontWeight.Medium
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

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = VerifiedBadgeGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Confirmation transmise au réseau mobile sénégalais (Orange/Wave/Free)",
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Medium
                            )
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
            TextButton(onClick = {
                onDismiss()
                onViewOrder()
            }) {
                Text("Détails commande", color = MedicalTealDark)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsInboxBottomSheet(
    smsList: List<SmsDeliveryNotification>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                            text = "SMS de Confirmation de Livraison",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "${smsList.size} notification(s) SMS envoyée(s)",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
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
                            text = "Vous recevrez un SMS dès que votre commande sera livrée.",
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
                    items(smsList) { sms ->
                        SmsItemCard(sms = sms)
                    }
                }
            }
        }
    }
}

@Composable
fun SmsItemCard(sms: SmsDeliveryNotification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBFB)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0ECE8))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = VerifiedBadgeGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = sms.sender,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MedicalTealPrimary
                    )
                }

                Text(
                    text = sms.timestamp,
                    fontSize = 10.sp,
                    color = TextSecondaryMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sms.messageText,
                fontSize = 12.sp,
                color = TextPrimaryDark,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reçu sur ${sms.recipientPhone}",
                    fontSize = 10.sp,
                    color = TextSecondaryMuted
                )
                Text(
                    text = sms.orderNumber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalTealDark
                )
            }
        }
    }
}
