package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.example.ui.components.PrescriptionPharmacistValidationDialog
import com.example.ui.components.PrescriptionQrScannerDialog
import com.example.ui.components.PrescriptionUploadDialog
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
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
    var showQrScannerDialog by remember { mutableStateOf(false) }
    var prescriptionToDelete by remember { mutableStateOf<PrescriptionEntity?>(null) }
    var prescriptionForPharmacistValidation by remember { mutableStateOf<PrescriptionEntity?>(null) }
    var successTransmissionBanner by remember { mutableStateOf<String?>(null) }

    if (showQrScannerDialog) {
        PrescriptionQrScannerDialog(
            viewModel = viewModel,
            onDismiss = { showQrScannerDialog = false },
            onNavigateToCart = {
                showQrScannerDialog = false
                onNavigateToCart()
            }
        )
    }

    if (showUploadDialog) {
        PrescriptionUploadDialog(
            initialPatientName = "",
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
                    onSuccess = { rx ->
                        successTransmissionBanner = "Ordonnance scannée et envoyée à $pharmName ! Le pharmacien a validé les stocks disponibles."
                        prescriptionForPharmacistValidation = rx
                    }
                )
            }
        )
    }

    if (prescriptionForPharmacistValidation != null) {
        PrescriptionPharmacistValidationDialog(
            prescription = prescriptionForPharmacistValidation!!,
            onDismiss = { prescriptionForPharmacistValidation = null },
            onConfirmOrder = { selectedMeds ->
                viewModel.orderDirectlyFromPrescription(
                    prescription = prescriptionForPharmacistValidation!!,
                    selectedMedicineNames = selectedMeds,
                    onComplete = {
                        prescriptionForPharmacistValidation = null
                        onNavigateToCart()
                    }
                )
            }
        )
    }

    if (prescriptionToDelete != null) {
        AlertDialog(
            onDismissRequest = { prescriptionToDelete = null },
            containerColor = Color.White,
            titleContentColor = TextOnWhitePrimary,
            textContentColor = TextOnWhiteSecondary,
            title = { Text("Supprimer l'ordonnance ?", fontWeight = FontWeight.Bold, color = TextOnWhitePrimary) },
            text = {
                Text(
                    text = "Voulez-vous supprimer cette ordonnance de votre carnet de santé ?",
                    fontSize = 13.sp,
                    color = TextOnWhiteSecondary
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
                    Text("Annuler", color = Color(0xFF475569), fontWeight = FontWeight.SemiBold)
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showQrScannerDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("scan_prescription_qr_button")
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan QR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showUploadDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("upload_new_prescription_button")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = MedicalTealPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedicalTealPrimary)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Bannière Scan QR Code Ordonnance & Code-barres Boîtes Médicaments
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showQrScannerDialog = true }
                        .testTag("hero_banner_scan_qr_code"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Badge Jaune repositionné de manière proéminente en haut
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFDE047))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = Color(0xFF78350F),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "SCANNER UNIVERSEL • QR ORDONNANCE & CODE-BARRES BOÎTES",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF78350F)
                                    )
                                }
                            }

                            Text(
                                text = "Caméra ZXing HD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Scanner Ordonnance & Boîtes",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Numérisez le QR code d'une ordonnance ou le code-barres (EAN-13/CIP) présent sur vos boîtes de médicaments pour les ajouter automatiquement au panier.",
                                    fontSize = 11.5.sp,
                                    color = Color.White.copy(alpha = 0.92f),
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showQrScannerDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF0F766E)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Scanner", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
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
                            text = "Scannez votre ordonnance avec la caméra pour la transmettre directement à la pharmacie de votre région au Sénégal. Le pharmacien confirme les médicaments disponibles avant que vous ne validiez la commande.",
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
                                text = "Appuyez sur Scanner pour photographier et envoyer votre ordonnance",
                                fontSize = 12.sp,
                                color = TextSecondaryMuted
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { showQrScannerDialog = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                                ) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Scanner QR Code")
                                }

                                OutlinedButton(
                                    onClick = { showUploadDialog = true },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = MedicalTealPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Photo Ordonnance", color = MedicalTealPrimary)
                                }
                            }
                        }
                    }
                }
            } else {
                items(prescriptions) { prescription ->
                    PrescriptionCard(
                        prescription = prescription,
                        onDelete = { prescriptionToDelete = prescription },
                        onViewPharmacistValidation = {
                            prescriptionForPharmacistValidation = prescription
                        },
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
    onViewPharmacistValidation: () -> Unit,
    onOrderDirectly: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prescription_card_${prescription.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MedicalTealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = prescription.doctorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Patient: ${prescription.patientName} • ${prescription.prescriptionDate}",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondaryMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CertifiedBadge(text = "Transmise ✓")
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Supprimer",
                            tint = Color(0xFFB0BEC5),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pharmacy destination badge & Pharmacist in charge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE0F2F1))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalPharmacy,
                    contentDescription = null,
                    tint = MedicalTealPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${prescription.pharmacyName} (${prescription.pharmacyRegion})",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalTealDark
                    )
                    Text(
                        text = "Pharmacien responsable : ${prescription.pharmacistName}",
                        fontSize = 11.5.sp,
                        color = TextOnWhiteSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEFF4F2))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Médicaments prescrits détectés :",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MedicalTealDark
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = prescription.recognizedMedicines,
                fontSize = 13.sp,
                color = TextPrimaryDark,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Pharmacist response & validation badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = VerifiedBadgeGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Validation Pharmacie • Médicaments en stock confirmés",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerifiedBadgeGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = prescription.pharmacistNotes,
                        fontSize = 12.sp,
                        color = Color(0xFF1B5E20),
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: 1. Voir Validation Pharmacien / 2. Commander
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewPharmacistValidation,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(17.dp), tint = MedicalTealPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Vérifier Disponibilité",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalTealPrimary
                    )
                }

                Button(
                    onClick = onOrderDirectly,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Valider Commande",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
