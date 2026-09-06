package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.DeliveryAddressEntity
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryAddressDialog(
    address: DeliveryAddressEntity?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        recipient: String,
        phone: String,
        fullAddress: String,
        neighborhood: String,
        city: String,
        region: String,
        instructions: String,
        isDefault: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf(address?.title ?: "Domicile") }
    var recipient by remember { mutableStateOf(address?.recipientName ?: "") }
    var phone by remember { mutableStateOf(address?.contactPhone ?: "") }
    var fullAddress by remember { mutableStateOf(address?.fullAddress ?: "") }
    var neighborhood by remember { mutableStateOf(address?.neighborhood ?: "Sacré-Cœur") }
    var city by remember { mutableStateOf(address?.city ?: "Dakar") }
    var region by remember { mutableStateOf(address?.region ?: "Dakar") }
    var instructions by remember { mutableStateOf(address?.courierInstructions ?: "") }
    var isDefault by remember { mutableStateOf(address?.isDefault ?: false) }

    var regionExpanded by remember { mutableStateOf(false) }
    var isGpsLocating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val presetLabels = listOf(
        Pair("Domicile", Icons.Default.Home),
        Pair("Bureau", Icons.Default.Business),
        Pair("Hôpital / Clinique", Icons.Default.LocalHospital),
        Pair("Famille", Icons.Default.FamilyRestroom),
        Pair("Autre", Icons.Default.Place)
    )

    // Suggestion of neighborhoods based on region
    val currentRegionData = InitialData.senegalAdministrativeData[region]
    val popularNeighborhoods = currentRegionData?.popularNeighborhoods ?: listOf("Centre-ville", "Plateau", "Marché Central")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("delivery_address_dialog"),
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MedicalTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (address == null) "Ajouter un Lieu de Livraison" else "Modifier l'Adresse",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Géolocalisation & Instructions Coursier 🇸🇳",
                                fontSize = 11.sp,
                                color = TextSecondaryMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondaryMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // GPS Auto-detect Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MedicalTealLight.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MedicalTealPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                isGpsLocating = true
                                delay(800)
                                val gpsPreset = InitialData.locationPresets.first()
                                region = "Dakar"
                                city = "Dakar"
                                neighborhood = gpsPreset.district
                                fullAddress = "${gpsPreset.name}, près pharmacie de garde"
                                isGpsLocating = false
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isGpsLocating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MedicalTealPrimary
                                )
                            } else {
                                Icon(Icons.Default.MyLocation, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isGpsLocating) "Acquisition GPS en cours..." else "Utiliser ma position GPS actuelle",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalTealDark
                            )
                        }
                        Icon(Icons.Default.Place, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Label Preset Chips
                Text("Type de lieu :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetLabels) { (label, icon) ->
                        val isSelected = title == label
                        FilterChip(
                            selected = isSelected,
                            onClick = { title = label },
                            label = { Text(label, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedicalTealPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Recipient & Phone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = recipient,
                        onValueChange = { recipient = it },
                        label = { Text("Destinataire *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f).testTag("input_addr_recipient"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Téléphone *") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("input_addr_phone"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Region selector dropdown
                ExposedDropdownMenuBox(
                    expanded = regionExpanded,
                    onExpandedChange = { regionExpanded = !regionExpanded }
                ) {
                    OutlinedTextField(
                        value = region,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Région du Sénégal (14 Régions)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                    )
                    ExposedDropdownMenu(
                        expanded = regionExpanded,
                        onDismissRequest = { regionExpanded = false }
                    ) {
                        InitialData.senegalRegionsList.forEach { reg ->
                            DropdownMenuItem(
                                text = { Text(reg, fontSize = 13.sp) },
                                onClick = {
                                    region = reg
                                    val regInfo = InitialData.senegalAdministrativeData[reg]
                                    if (regInfo != null) {
                                        city = regInfo.capital
                                        neighborhood = regInfo.popularNeighborhoods.firstOrNull() ?: regInfo.capital
                                    }
                                    regionExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // City & Neighborhood
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
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                    )

                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Quartier *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                    )
                }

                // Popular neighborhood suggestion chips
                if (popularNeighborhoods.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Suggestions quartiers fréquents :", fontSize = 10.sp, color = TextSecondaryMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(popularNeighborhoods.take(4)) { q ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (neighborhood == q) MedicalTealPrimary else Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { neighborhood = q }
                            ) {
                                Text(
                                    text = q,
                                    fontSize = 10.sp,
                                    color = if (neighborhood == q) Color.White else TextPrimaryDark,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detailed address
                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text("Adresse précise (Rue, N° Villa, Immeuble, Repère)") },
                    placeholder = { Text("Ex: Villa 482, Rue 10 en face Pharmacie...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_full"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Instructions coursier
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions spéciales pour le livreur (Optionnel)") },
                    placeholder = { Text("Ex: Code interphone 1452, appeler en bas...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_instructions"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Default address toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAF9))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Définir comme adresse principale", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)
                        Text("Sélectionnée par défaut lors de vos commandes", fontSize = 10.sp, color = TextSecondaryMuted)
                    }
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = MedicalTealPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler", color = TextSecondaryMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val computed = if (fullAddress.isBlank()) "$neighborhood, $city ($region)" else fullAddress
                            onSave(
                                title.trim(),
                                recipient.trim(),
                                phone.trim(),
                                computed.trim(),
                                neighborhood.trim(),
                                city.trim(),
                                region.trim(),
                                instructions.trim(),
                                isDefault
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_save_addr_dialog")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enregistrer l'adresse", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
