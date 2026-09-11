package com.example.ui.components

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.auth.AuthUser
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextOnWhiteMuted
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VerifiedBadgeGreen

@Composable
fun EditProfileDialog(
    profile: UserProfileEntity?,
    currentUser: AuthUser?,
    onDismiss: () -> Unit,
    onSave: (
        fullName: String,
        email: String,
        phoneNumber: String,
        secondaryPhone: String,
        emergencyContactName: String,
        emergencyContactPhone: String,
        bloodGroup: String,
        knownAllergies: String,
        preferredPaymentMethod: String,
        medicalNotes: String,
        userRole: String
    ) -> Unit
) {
    var fullName by remember {
        mutableStateOf(
            when {
                currentUser?.displayName?.isNotBlank() == true -> currentUser.displayName
                profile != null && profile.fullName.isNotBlank() -> profile.fullName
                else -> ""
            }
        )
    }

    var email by remember {
        mutableStateOf(
            when {
                currentUser?.email?.isNotBlank() == true -> currentUser.email
                profile != null && profile.email.isNotBlank() -> profile.email
                else -> ""
            }
        )
    }

    var phoneNumber by remember {
        mutableStateOf(
            when {
                currentUser?.phoneNumber?.isNotBlank() == true -> currentUser.phoneNumber
                profile != null && profile.phoneNumber.isNotBlank() -> profile.phoneNumber
                else -> ""
            }
        )
    }

    var secondaryPhone by remember { mutableStateOf(profile?.secondaryPhone ?: "") }
    var emergencyContactName by remember { mutableStateOf(profile?.emergencyContactName ?: "") }
    var emergencyContactPhone by remember { mutableStateOf(profile?.emergencyContactPhone ?: "") }
    var bloodGroup by remember { mutableStateOf(profile?.bloodGroup?.ifBlank { "O+" } ?: "O+") }
    var knownAllergies by remember { mutableStateOf(profile?.knownAllergies ?: "") }
    var preferredPaymentMethod by remember { mutableStateOf(profile?.preferredPaymentMethod?.ifBlank { "Wave Mobile Money" } ?: "Wave Mobile Money") }
    var medicalNotes by remember { mutableStateOf(profile?.medicalNotes ?: "") }
    var userRole by remember { mutableStateOf(currentUser?.role ?: profile?.userRole ?: "Patient / Client") }

    val bloodGroups = listOf("O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-")
    val paymentMethods = listOf("Wave Mobile Money", "Orange Money", "Free Money", "Espèces à la livraison")
    val rolesList = listOf("Patient / Client", "Pharmacien Diplômé", "Livreur Partenaire")

    var validationError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .widthIn(max = 580.dp)
                .padding(vertical = 14.dp)
                .testTag("dialog_edit_profile"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MedicalTealPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Modifier mon Profil",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnWhitePrimary
                            )
                            Text(
                                text = "Coordonnées personnelles & Fiche santé",
                                fontSize = 12.sp,
                                color = TextOnWhiteSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_edit_profile")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Rôle
                Text(
                    text = "Rôle de l'utilisateur",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnWhitePrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rolesList.forEach { role ->
                        val isSelected = userRole == role
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MedicalTealPrimary else Color(0xFFF1F5F9))
                                .border(
                                    1.dp,
                                    if (isSelected) MedicalTealPrimary else Color(0xFFE2E8F0),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { userRole = role }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (role) {
                                    "Patient / Client" -> "Patient"
                                    "Pharmacien Diplômé" -> "Pharmacien"
                                    else -> "Livreur"
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextOnWhitePrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Identité et Contact
                Text(
                    text = "Identité & Coordonnées",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnWhitePrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        validationError = null
                    },
                    label = { Text("Nom complet / Prénom", color = TextOnWhiteSecondary) },
                    placeholder = { Text("Ex: Amadou Diallo") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MedicalTealPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_fullname"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextOnWhitePrimary,
                        unfocusedTextColor = TextOnWhitePrimary,
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        validationError = null
                    },
                    label = { Text("Adresse Email", color = TextOnWhiteSecondary) },
                    placeholder = { Text("amadou@pharmaexpress.sn") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = MedicalTealPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_email"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextOnWhitePrimary,
                        unfocusedTextColor = TextOnWhitePrimary,
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it
                            validationError = null
                        },
                        label = { Text("Téléphone", color = TextOnWhiteSecondary) },
                        placeholder = { Text("+221 77 ...") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_edit_phone"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextOnWhitePrimary,
                            unfocusedTextColor = TextOnWhitePrimary,
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = secondaryPhone,
                        onValueChange = { secondaryPhone = it },
                        label = { Text("Tél 2 (Optionnel)", color = TextOnWhiteSecondary) },
                        placeholder = { Text("+221 ...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_edit_sec_phone"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextOnWhitePrimary,
                            unfocusedTextColor = TextOnWhitePrimary,
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Données Médicales
                Text(
                    text = "Dossier Médical & Urgence",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnWhitePrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Groupe Sanguin Chips
                Text(
                    text = "Groupe sanguin :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextOnWhiteSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bloodGroups.take(4).forEach { bg ->
                        val isSelected = bloodGroup == bg
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFFDC2626) else Color(0xFFF1F5F9))
                                .clickable { bloodGroup = bg }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bg,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextOnWhitePrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bloodGroups.takeLast(4).forEach { bg ->
                        val isSelected = bloodGroup == bg
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFFDC2626) else Color(0xFFF1F5F9))
                                .clickable { bloodGroup = bg }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bg,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextOnWhitePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = knownAllergies,
                    onValueChange = { knownAllergies = it },
                    label = { Text("Allergies connues déclarées", color = TextOnWhiteSecondary) },
                    placeholder = { Text("Ex: Pénicilline, Aspirine, Aucune") },
                    leadingIcon = {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = DutyPharmacyOrange)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_allergies"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextOnWhitePrimary,
                        unfocusedTextColor = TextOnWhitePrimary,
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Contact Urgence
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = emergencyContactName,
                        onValueChange = { emergencyContactName = it },
                        label = { Text("Contact SOS (Nom)", color = TextOnWhiteSecondary) },
                        placeholder = { Text("Parent / Proche") },
                        leadingIcon = {
                            Icon(Icons.Default.ContactPhone, contentDescription = null, tint = SafeBlueSecondary)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_edit_sos_name"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextOnWhitePrimary,
                            unfocusedTextColor = TextOnWhitePrimary,
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = emergencyContactPhone,
                        onValueChange = { emergencyContactPhone = it },
                        label = { Text("N° Tél SOS", color = TextOnWhiteSecondary) },
                        placeholder = { Text("+221 ...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_edit_sos_phone"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextOnWhitePrimary,
                            unfocusedTextColor = TextOnWhitePrimary,
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Moyen de paiement préféré
                Text(
                    text = "Moyen de paiement favori :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextOnWhiteSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    paymentMethods.forEach { method ->
                        val isSelected = preferredPaymentMethod == method
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MedicalTealPrimary.copy(alpha = 0.12f) else Color(0xFFF8FAFC))
                                .border(
                                    1.dp,
                                    if (isSelected) MedicalTealPrimary else Color(0xFFE2E8F0),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { preferredPaymentMethod = method }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null,
                                tint = if (isSelected) MedicalTealPrimary else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = method,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MedicalTealPrimary else TextOnWhitePrimary
                            )
                        }
                    }
                }

                validationError?.let { err ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = err, color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_cancel_edit_profile"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Text("Annuler", color = Color(0xFF475569), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            if (fullName.trim().isBlank()) {
                                validationError = "Veuillez renseigner votre nom complet."
                                return@Button
                            }
                            onSave(
                                fullName.trim(),
                                email.trim(),
                                phoneNumber.trim(),
                                secondaryPhone.trim(),
                                emergencyContactName.trim(),
                                emergencyContactPhone.trim(),
                                bloodGroup.trim(),
                                knownAllergies.trim(),
                                preferredPaymentMethod,
                                medicalNotes.trim(),
                                userRole
                            )
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("btn_save_edit_profile"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
