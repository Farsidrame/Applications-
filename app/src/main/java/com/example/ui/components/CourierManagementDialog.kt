package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.DeliveryCourierEntity
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierManagementDialog(
    courierToEdit: DeliveryCourierEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        fullName: String,
        phoneNumber: String,
        nationalIdCardNumber: String,
        address: String,
        vehicleType: String,
        region: String,
        assignedZone: String,
        status: String
    ) -> Unit
) {
    var fullName by remember { mutableStateOf(courierToEdit?.fullName ?: "") }
    var phoneNumber by remember { mutableStateOf(courierToEdit?.phoneNumber ?: "") }
    var nationalIdCardNumber by remember { mutableStateOf(courierToEdit?.nationalIdCardNumber ?: "") }
    var address by remember { mutableStateOf(courierToEdit?.address ?: "") }
    var vehicleType by remember { mutableStateOf(courierToEdit?.vehicleType ?: "Moto Express (Isotherme)") }
    var selectedRegion by remember { mutableStateOf(courierToEdit?.region ?: "Dakar") }
    var assignedZone by remember { mutableStateOf(courierToEdit?.assignedZone ?: "") }
    var selectedStatus by remember { mutableStateOf(courierToEdit?.status ?: "DISPONIBLE") }

    var vehicleExpanded by remember { mutableStateOf(false) }
    var regionExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val vehicleTypes = listOf(
        "Moto Express (Isotherme)",
        "Scooter Électrique",
        "Vélo Cargo Médical",
        "Fourgonnette Frigorifique"
    )

    val statuses = listOf("DISPONIBLE", "EN_LIVRAISON", "HORS_LIGNE")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("courier_dialog"),
            shape = RoundedCornerShape(20.dp),
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
                                imageVector = Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (courierToEdit == null) "Ajouter un Livreur" else "Modifier le Livreur",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Flotte de distribution médicale",
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

                // Nom complet
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nom et prénom du coursier *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MedicalTealPrimary) },
                    modifier = Modifier.fillMaxWidth().testTag("courier_fullname_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Téléphone
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Téléphone mobile *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("courier_phone_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // CNI
                OutlinedTextField(
                    value = nationalIdCardNumber,
                    onValueChange = { nationalIdCardNumber = it },
                    label = { Text("N° CNI / NIN (Carte d'identité) *") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = MedicalTealPrimary) },
                    modifier = Modifier.fillMaxWidth().testTag("courier_cni_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Adresse de domicile
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse de domicile *") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = MedicalTealPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Type de véhicule
                ExposedDropdownMenuBox(
                    expanded = vehicleExpanded,
                    onExpandedChange = { vehicleExpanded = !vehicleExpanded }
                ) {
                    OutlinedTextField(
                        value = vehicleType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type de Véhicule *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleExpanded) },
                        leadingIcon = { Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = MedicalTealPrimary) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = vehicleExpanded,
                        onDismissRequest = { vehicleExpanded = false }
                    ) {
                        vehicleTypes.forEach { vt ->
                            DropdownMenuItem(
                                text = { Text(vt) },
                                onClick = {
                                    vehicleType = vt
                                    vehicleExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Région
                ExposedDropdownMenuBox(
                    expanded = regionExpanded,
                    onExpandedChange = { regionExpanded = !regionExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedRegion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Région d'affectation *") },
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

                // Zone assignée
                OutlinedTextField(
                    value = assignedZone,
                    onValueChange = { assignedZone = it },
                    label = { Text("Zone d'intervention (ex: Dakar-Plateau, Almadies...)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Statut
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = when (selectedStatus) {
                            "DISPONIBLE" -> "🟢 Disponible pour courses"
                            "EN_LIVRAISON" -> "🟡 En cours de livraison"
                            else -> "⚪ Hors ligne"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Statut opérationnel") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statuses.forEach { st ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (st) {
                                            "DISPONIBLE" -> "🟢 Disponible pour courses"
                                            "EN_LIVRAISON" -> "🟡 En cours de livraison"
                                            else -> "⚪ Hors ligne"
                                        }
                                    )
                                },
                                onClick = {
                                    selectedStatus = st
                                    statusExpanded = false
                                }
                            )
                        }
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

                // Submit button
                Button(
                    onClick = {
                        if (fullName.isBlank() || phoneNumber.isBlank() || nationalIdCardNumber.isBlank()) {
                            errorMessage = "Veuillez remplir les champs obligatoires (*)"
                            return@Button
                        }
                        onSave(
                            courierToEdit?.id,
                            fullName.trim(),
                            phoneNumber.trim(),
                            nationalIdCardNumber.trim(),
                            if (address.isNotBlank()) address.trim() else "Dakar",
                            vehicleType,
                            selectedRegion,
                            if (assignedZone.isNotBlank()) assignedZone.trim() else "Toute la région",
                            selectedStatus
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_courier_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (courierToEdit == null) "Enregistrer le Livreur" else "Mettre à jour la fiche",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
