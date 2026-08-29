package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.InitialData
import com.example.data.model.Pharmacy
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionUploadDialog(
    initialPatientName: String = "Mamadou Dramé",
    allPharmacies: List<Pharmacy> = InitialData.pharmacies,
    onDismiss: () -> Unit,
    onSubmit: (
        patientName: String,
        doctorName: String,
        date: String,
        photoUri: String,
        notes: String,
        recognizedMedicines: String,
        pharmacyId: String,
        pharmacyName: String,
        pharmacyRegion: String
    ) -> Unit
) {
    var patientName by remember { mutableStateOf(initialPatientName) }
    var doctorName by remember { mutableStateOf("Dr. Sokhna Ndao (Clinique Madeleine)") }
    var prescriptionDate by remember { mutableStateOf("29 Août 2026") }
    var medicinesText by remember { mutableStateOf("Amoxicilline 1g x 14 comprimés, Ventoline 100µg x 1") }
    var notes by remember { mutableStateOf("Traitement infection respiratoire & renouvellement crise") }
    var isScanningMode by remember { mutableStateOf(false) }
    var isScanned by remember { mutableStateOf(true) }

    // Region and Pharmacy selection
    val regions = listOf("Toutes les régions", "Dakar", "Thiès", "Saint-Louis", "Touba", "Kaolack", "Ziguinchor", "Mbour")
    var selectedRegion by remember { mutableStateOf("Toutes les régions") }

    val filteredPharmacies = remember(selectedRegion, allPharmacies) {
        if (selectedRegion == "Toutes les régions") allPharmacies
        else allPharmacies.filter { it.region.equals(selectedRegion, ignoreCase = true) || it.city.equals(selectedRegion, ignoreCase = true) }
    }

    var selectedPharmacy by remember {
        mutableStateOf(filteredPharmacies.firstOrNull() ?: InitialData.pharmacies.first())
    }
    var pharmacyDropdownExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val scanLaserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_laser_val"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("prescription_upload_dialog"),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MedicalTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = null,
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Scanner une Ordonnance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Transmission directe à la pharmacie",
                                fontSize = 11.sp,
                                color = VerifiedBadgeGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scanner Viewport simulation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isScanned) Color(0xFFE8F5E9) else Color(0xFF1E293B))
                        .border(
                            2.dp,
                            if (isScanned) VerifiedBadgeGreen else MedicalTealPrimary,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            isScanningMode = !isScanningMode
                            isScanned = true
                        }
                        .padding(14.dp)
                        .testTag("prescription_scanner_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isScanned) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = if (isScanned) VerifiedBadgeGreen else Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isScanned) "Ordonnance numérisée & analysée (OCR 100%)" else "Alignez l'ordonnance dans le cadre",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isScanned) VerifiedBadgeGreen else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isScanned) "Scan certifié • Détection automatique des molécules" else "Appuyez pour déclencher la capture haute résolution",
                            fontSize = 11.sp,
                            color = if (isScanned) TextSecondaryMuted else Color(0xFF94A3B8)
                        )

                        if (isScanned) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MedicalTealPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "2 Médicaments reconnus avec succès",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalTealDark
                                    )
                                }
                                Text(
                                    text = "Modifier",
                                    fontSize = 11.sp,
                                    color = MedicalTealPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Choisir la pharmacie destinataire au Sénégal
                Text(
                    text = "1. Pharmacie destinataire au Sénégal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MedicalTealDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Region filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(regions) { region ->
                        val isSelected = selectedRegion == region
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedRegion = region
                                val updated = if (region == "Toutes les régions") allPharmacies
                                else allPharmacies.filter { it.region.equals(region, ignoreCase = true) || it.city.equals(region, ignoreCase = true) }
                                selectedPharmacy = updated.firstOrNull() ?: selectedPharmacy
                            },
                            label = { Text(region, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedicalTealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pharmacy Dropdown
                ExposedDropdownMenuBox(
                    expanded = pharmacyDropdownExpanded,
                    onExpandedChange = { pharmacyDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedPharmacy.name} (${selectedPharmacy.city})",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = pharmacyDropdownExpanded) },
                        leadingIcon = {
                            Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = MedicalTealPrimary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("pharmacy_destination_selector"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = pharmacyDropdownExpanded,
                        onDismissRequest = { pharmacyDropdownExpanded = false }
                    ) {
                        filteredPharmacies.forEach { pharmacy ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(pharmacy.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${pharmacy.address} • ${pharmacy.city}", fontSize = 11.sp, color = TextSecondaryMuted)
                                    }
                                },
                                onClick = {
                                    selectedPharmacy = pharmacy
                                    pharmacyDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Informations ordonnance
                Text(
                    text = "2. Détails de l'ordonnance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MedicalTealDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Nom du patient") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rx_patient_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("Médecin prescripteur / Clinique") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = medicinesText,
                    onValueChange = { medicinesText = it },
                    label = { Text("Médicaments prescrits détectés") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rx_medicines_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes ou instructions pour le pharmacien") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Security notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F8F6))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MedicalTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Transmise directement sous secret médical au pharmacien de ${selectedPharmacy.name}.",
                        fontSize = 11.sp,
                        color = MedicalTealDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSubmit(
                            patientName,
                            doctorName,
                            prescriptionDate,
                            "prescription_scan_uri",
                            notes,
                            medicinesText,
                            selectedPharmacy.id,
                            selectedPharmacy.name,
                            selectedPharmacy.region
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_prescription_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Envoyer l'ordonnance à la pharmacie",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
