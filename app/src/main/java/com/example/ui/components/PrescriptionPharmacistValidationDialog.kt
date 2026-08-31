package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.InitialData
import com.example.data.model.PrescriptionEntity
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen

data class AvailableMedItem(
    val name: String,
    val dosage: String,
    val priceFcfa: Int,
    val isAvailable: Boolean,
    val posologyAdvice: String
)

@Composable
fun PrescriptionPharmacistValidationDialog(
    prescription: PrescriptionEntity,
    onDismiss: () -> Unit,
    onConfirmOrder: (selectedMedicines: List<String>) -> Unit
) {
    // Parse medicines from prescription
    val rawList = remember(prescription.recognizedMedicines) {
        prescription.recognizedMedicines.split(",").map { it.trim() }.filter { it.isNotBlank() }
    }

    val parsedMeds = remember(rawList) {
        rawList.mapIndexed { index, medStr ->
            val match = InitialData.medicines.find {
                it.name.contains(medStr, ignoreCase = true) || medStr.contains(it.name, ignoreCase = true)
            }
            val price = match?.priceFcfa ?: when (index % 3) {
                0 -> 3200
                1 -> 4500
                else -> 1800
            }
            val dosage = match?.dosageStrength ?: "Dosage standard"
            val advice = when {
                medStr.contains("Amox", ignoreCase = true) -> "1 gélule matin et soir pendant 7 jours après le repas"
                medStr.contains("Ventoline", ignoreCase = true) -> "1 à 2 bouffées par voie inhalée en cas de crise d'asthme"
                medStr.contains("Para", ignoreCase = true) || medStr.contains("Doli", ignoreCase = true) -> "1 comprimé toutes les 6 heures si douleur/fièvre (max 3g/jour)"
                else -> "Prendre selon la posologie stricte de l'ordonnance médicale"
            }
            AvailableMedItem(
                name = match?.name ?: medStr,
                dosage = dosage,
                priceFcfa = price,
                isAvailable = true,
                posologyAdvice = advice
            )
        }
    }

    // Selected state map for checkboxes
    val selectedMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            parsedMeds.forEach { put(it.name, true) }
        }
    }

    val selectedCount = selectedMap.values.count { it }
    val totalSelectedPrice = parsedMeds.filter { selectedMap[it.name] == true }.sumOf { it.priceFcfa }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .testTag("pharmacist_validation_dialog"),
            shape = RoundedCornerShape(24.dp),
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
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = VerifiedBadgeGreen,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Validation Pharmacien",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Disponibilité & Préparation de commande",
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

                // Pharmacist in charge & Pharmacy Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F6))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = prescription.pharmacyName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MedicalTealDark
                                    )
                                    Text(
                                        text = "Région: ${prescription.pharmacyRegion} • Pharmacie Agréée",
                                        fontSize = 11.sp,
                                        color = TextSecondaryMuted
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Vérifié ✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VerifiedBadgeGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFDCEFEA))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MedicalEmeraldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pharmacien réviseur : ${prescription.pharmacistName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prescription du ${prescription.prescriptionDate} • ${prescription.doctorName}",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section Médicaments disponibles confirmés
                Text(
                    text = "Médicaments prescrits & Stocks vérifiés :",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MedicalTealDark
                )
                Text(
                    text = "Sélectionnez les molécules que vous souhaitez commander immédiatement :",
                    fontSize = 11.sp,
                    color = TextSecondaryMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    parsedMeds.forEach { item ->
                        val isChecked = selectedMap[item.name] ?: true
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (isChecked) MedicalTealPrimary.copy(alpha = 0.5f) else Color(0xFFE2E8F0),
                                    RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChecked) Color(0xFFFAFDFA) else Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { selectedMap[item.name] = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MedicalTealPrimary,
                                        checkmarkColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimaryDark,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${item.priceFcfa} FCFA",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MedicalTealPrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFE8F5E9))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "● En Stock Officine",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = VerifiedBadgeGreen
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = item.dosage,
                                            fontSize = 11.sp,
                                            color = TextSecondaryMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Conseil prise : ${item.posologyAdvice}",
                                        fontSize = 10.sp,
                                        color = TextSecondaryMuted,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pharmacist advice box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F6))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Avis Officine : ${prescription.pharmacistNotes}",
                            fontSize = 11.sp,
                            color = MedicalTealDark,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total Summary
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$selectedCount médicament(s) sélectionné(s)",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                        Text(
                            text = "Total Médicaments Disponibles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimaryDark
                        )
                    }
                    Text(
                        text = "$totalSelectedPrice FCFA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MedicalTealPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val selectedList = parsedMeds.filter { selectedMap[it.name] == true }.map { it.name }
                        onConfirmOrder(selectedList)
                        onDismiss()
                    },
                    enabled = selectedCount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_prescription_order_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Valider la commande ($totalSelectedPrice FCFA)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
