package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PrescriptionEntity
import com.example.ui.components.CertifiedBadge
import com.example.ui.components.PrescriptionUploadDialog
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.viewmodel.PharmaViewModel

@Composable
fun PrescriptionsScreen(
    viewModel: PharmaViewModel,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prescriptions by viewModel.prescriptions.collectAsStateWithLifecycle()
    val allPharmacies by viewModel.allPharmacies.collectAsStateWithLifecycle()
    var showUploadDialog by remember { mutableStateOf(false) }
    var prescriptionToDelete by remember { mutableStateOf<PrescriptionEntity?>(null) }
    var successTransmissionBanner by remember { mutableStateOf<String?>(null) }

    if (showUploadDialog) {
        PrescriptionUploadDialog(
            initialPatientName = viewModel.userName.value,
            allPharmacies = allPharmacies,
            onDismiss = { showUploadDialog = false },
            onSubmit = { pName, dName, date, uri, notes, meds, pharmId, pharmName, pharmRegion ->
                viewModel.submitPrescription(
                    patientName = pName,
                    doctorName = dName,
                    prescriptionDate = date,
                    photoUri = uri,
                    notes = notes,
                    recognizedMedicines = meds,
                    pharmacyId = pharmId,
                    pharmacyName = pharmName,
                    pharmacyRegion = pharmRegion,
                    onSuccess = {
                        successTransmissionBanner = "Ordonnance scannée et envoyée avec succès à $pharmName !"
                    }
                )
            }
        )
    }

    if (prescriptionToDelete != null) {
        AlertDialog(
            onDismissRequest = { prescriptionToDelete = null },
            title = { Text("Supprimer l'ordonnance ?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Voulez-vous supprimer cette ordonnance de votre carnet de santé ?",
                    fontSize = 13.sp,
                    color = TextSecondaryMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        prescriptionToDelete?.let { viewModel.deletePrescription(it.id) }
                        prescriptionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Supprimer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { prescriptionToDelete = null }) {
                    Text("Annuler", color = TextSecondaryMuted)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("prescriptions_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Scanner & Envoyer Ordonnance",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Transmission sécurisée aux pharmacies du Sénégal",
                        fontSize = 12.sp,
                        color = TextSecondaryMuted
                    )
                }

                Button(
                    onClick = { showUploadDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("upload_new_prescription_button")
                ) {
                    Icon(Icons.Default.CropFree, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Scanner", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Success Transmission Banner
            if (successTransmissionBanner != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = VerifiedBadgeGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = successTransmissionBanner ?: "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VerifiedBadgeGreen
                                )
                            }
                            TextButton(onClick = { successTransmissionBanner = null }) {
                                Text("OK", color = VerifiedBadgeGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Legal / Safety Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F6))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Scannez votre ordonnance médicale pour la transmettre directement à la pharmacie de votre choix au Sénégal (Dakar, Thiès, Touba, Saint-Louis, etc.).",
                            fontSize = 11.sp,
                            color = MedicalTealDark,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (prescriptions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = null,
                                tint = TextSecondaryMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Aucune ordonnance numérisée",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Appuyez sur Scanner pour envoyer votre ordonnance",
                                fontSize = 12.sp,
                                color = TextSecondaryMuted
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showUploadDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scanner l'ordonnance")
                            }
                        }
                    }
                }
            } else {
                items(prescriptions) { prescription ->
                    PrescriptionCard(
                        prescription = prescription,
                        onDelete = { prescriptionToDelete = prescription },
                        onOrderDirectly = {
                            viewModel.orderDirectlyFromPrescription(prescription)
                            onNavigateToCart()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PrescriptionCard(
    prescription: PrescriptionEntity,
    onDelete: () -> Unit,
    onOrderDirectly: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prescription_card_${prescription.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MedicalTealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = prescription.doctorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Patient: ${prescription.patientName} • ${prescription.prescriptionDate}",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CertifiedBadge(text = "Transmise ✓")
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Supprimer",
                            tint = Color(0xFFB0BEC5),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pharmacy badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0F2F1))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalPharmacy,
                    contentDescription = null,
                    tint = MedicalTealPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Destinataire : ${prescription.pharmacyName} (${prescription.pharmacyRegion})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalTealDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFEFF4F2))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Médicaments prescrits reconnus :",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MedicalTealDark
            )
            Text(
                text = prescription.recognizedMedicines,
                fontSize = 12.sp,
                color = TextPrimaryDark,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Avis Pharmacien : ${prescription.pharmacistNotes}",
                    fontSize = 11.sp,
                    color = VerifiedBadgeGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onOrderDirectly,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Commander les médicaments auprès de cette pharmacie",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
