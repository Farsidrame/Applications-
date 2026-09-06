package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.InitialData
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacistRegistrationDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        fullName: String,
        pharmacyName: String,
        region: String,
        city: String,
        district: String,
        phoneNumber: String,
        email: String,
        licenseNumber: String,
        orderRegistrationNumber: String,
        diplomaTitle: String
    ) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var pharmacyName by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("Dakar") }
    var city by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var orderRegistrationNumber by remember { mutableStateOf("") }
    var diplomaTitle by remember { mutableStateOf("Doctorat d'État en Pharmacie") }

    var regionExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var attachedDocumentName by remember { mutableStateOf<String?>(null) }
    var attachedDocumentUri by remember { mutableStateOf<Uri?>(null) }

    // Direct system document / file picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedDocumentUri = uri
            val extractedName = uri.lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':')
            attachedDocumentName = if (!extractedName.isNullOrBlank()) extractedName else "copie_diplome_licence.pdf"
            errorMessage = null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("pharmacist_registration_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with White Title Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MedicalTealPrimary
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalPharmacy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Inscription Pharmacien",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Agrément Officinal & Ordre SN",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Info banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MedicalTealPrimary)
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Validation instantanée par SMS officiel après transmission de vos identifiants d'ordre.",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Identité du Pharmacien
                Text("1. Identité & Diplôme", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nom et prénom du pharmacien titulaire *") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = MedicalTealPrimary) },
                    modifier = Modifier.fillMaxWidth().testTag("pharmacist_fullname_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = diplomaTitle,
                    onValueChange = { diplomaTitle = it },
                    label = { Text("Titre du Diplôme Universitaire *") },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = MedicalTealPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Officine & Localisation
                Text("2. Officine & Localisation", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = pharmacyName,
                    onValueChange = { pharmacyName = it },
                    label = { Text("Nom de la pharmacie / Officine *") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = MedicalTealPrimary) },
                    modifier = Modifier.fillMaxWidth().testTag("pharmacist_pharmacy_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Region dropdown
                ExposedDropdownMenuBox(
                    expanded = regionExpanded,
                    onExpandedChange = { regionExpanded = !regionExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedRegion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Région (Sénégal) *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MedicalTealPrimary) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = regionExpanded,
                        onDismissRequest = { regionExpanded = false }
                    ) {
                        InitialData.senegalRegions.forEach { reg ->
                            DropdownMenuItem(
                                text = { Text(reg) },
                                onClick = {
                                    selectedRegion = reg
                                    regionExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ville / Commune *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("Quartier / Rue *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Contacts & Agréments
                Text("3. Agréments & Contact", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = licenseNumber,
                        onValueChange = { licenseNumber = it },
                        label = { Text("N° Licence *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = orderRegistrationNumber,
                        onValueChange = { orderRegistrationNumber = it },
                        label = { Text("N° Ordre *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Numéro Mobile / WhatsApp Officiel *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("pharmacist_phone_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email professionnel") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MedicalTealPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Direct File & Document Attachment Section
                Text("4. Pièce Justificative (Licence / Diplôme)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(6.dp))

                if (attachedDocumentName != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = VerifiedBadgeGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = attachedDocumentName ?: "Document joint",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextPrimaryDark,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Fichier sélectionné avec succès ✓",
                                        fontSize = 11.sp,
                                        color = VerifiedBadgeGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Row {
                                OutlinedButton(
                                    onClick = { filePickerLauncher.launch("*/*") },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Changer", fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        attachedDocumentName = null
                                        attachedDocumentUri = null
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            // Direct opening of the native document/file picker
                            filePickerLauncher.launch("*/*")
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedicalTealPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_attach_pharmacist_diploma")
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = "Joindre document",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Joindre copie Licence / Diplôme (PDF/Photo)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (fullName.isBlank() || pharmacyName.isBlank() || phoneNumber.isBlank() || licenseNumber.isBlank() || orderRegistrationNumber.isBlank()) {
                            errorMessage = "Veuillez remplir tous les champs obligatoires (*)"
                            return@Button
                        }
                        onSubmit(
                            fullName.trim(),
                            pharmacyName.trim(),
                            selectedRegion,
                            if (city.isNotBlank()) city.trim() else "Dakar",
                            if (district.isNotBlank()) district.trim() else "Centre-ville",
                            phoneNumber.trim(),
                            email.trim(),
                            licenseNumber.trim(),
                            orderRegistrationNumber.trim(),
                            diplomaTitle.trim()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_pharmacist_registration_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalTealPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Valider mon Agrément & Activer l'Officine", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
