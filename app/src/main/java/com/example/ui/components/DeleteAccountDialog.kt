package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.auth.AuthUser
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextOnWhiteMuted
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun DeleteAccountDialog(
    profile: UserProfileEntity?,
    currentUser: AuthUser?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirmDelete: (clearAllData: Boolean) -> Unit
) {
    var confirmedCheckbox by remember { mutableStateOf(false) }
    var clearAllAddresses by remember { mutableStateOf(true) }

    val userIdentifier = when {
        currentUser?.email?.isNotBlank() == true -> currentUser.email
        profile?.email?.isNotBlank() == true -> profile.email
        currentUser?.displayName?.isNotBlank() == true -> currentUser.displayName
        profile?.fullName?.isNotBlank() == true -> profile.fullName
        else -> "Compte Utilisateur"
    }

    val userUid = currentUser?.uid ?: profile?.firebaseUid ?: ""

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_delete_account"),
            color = Color(0xFF181010), // Deep warm dark slate for danger warning
            border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f)),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Warning Icon and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF450A0A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Suppression",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF7F1D1D).copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ACTION IRRÉVERSIBLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFCA5A5),
                            letterSpacing = 0.5.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier.testTag("btn_close_delete_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title and Subtitle
                Text(
                    text = "Supprimer mon compte & identifiant",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Conforme à la politique de confidentialité Google Play et au RGPD",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Target Account Identity Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF271515)),
                    border = BorderStroke(1.dp, Color(0xFF7F1D1D).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3F1515)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonRemove,
                                contentDescription = null,
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Identifiant cible :",
                                fontSize = 11.sp,
                                color = Color(0xFFFCA5A5)
                            )
                            Text(
                                text = userIdentifier,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (userUid.isNotBlank()) {
                                Text(
                                    text = "UID: ${userUid.take(16)}...",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // What will be erased explanation list
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1414)),
                    border = BorderStroke(1.dp, Color(0xFF3D2323))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Les éléments suivants seront définitivement effacés :",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF87171)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DeletionDetailRow("Identifiant de connexion & compte Firebase Auth")
                        DeletionDetailRow("Profil santé (groupe sanguin, allergies, contact d'urgence)")
                        DeletionDetailRow("Données biométriques et code PIN de sécurité matériel")
                        DeletionDetailRow("Panier d'achat en cours et session active")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Option Checkbox for delivery addresses
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF221616))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = clearAllAddresses,
                        onCheckedChange = { clearAllAddresses = it },
                        enabled = !isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFEF4444),
                            uncheckedColor = Color.White.copy(alpha = 0.6f),
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Supprimer également le carnet d'adresses de livraison",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mandatory Safety Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (confirmedCheckbox) Color(0xFF3B1616) else Color(0xFF241515))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = confirmedCheckbox,
                        onCheckedChange = { confirmedCheckbox = it },
                        enabled = !isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFDC2626),
                            uncheckedColor = Color(0xFFF87171),
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.testTag("checkbox_confirm_account_deletion")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Je confirme vouloir supprimer définitivement mon compte et mon identifiant.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (confirmedCheckbox) Color(0xFFFCA5A5) else Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = { onConfirmDelete(clearAllAddresses) },
                    enabled = confirmedCheckbox && !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626),
                        disabledContainerColor = Color(0xFF552222)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_confirm_delete_account")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Suppression en cours...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Supprimer définitivement mon compte",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_cancel_delete_account")
                ) {
                    Text(
                        text = "Annuler et conserver mon compte",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DeletionDetailRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}
