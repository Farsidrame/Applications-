package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Medicine
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.Pharmacy
import com.example.ui.components.CertifiedBadge
import com.example.ui.components.DutyBadge
import com.example.ui.components.MedicineCard
import com.example.ui.components.PharmacyCard
import com.example.ui.theme.DutyPharmacyBg
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueLight
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.viewmodel.PharmaViewModel

@Composable
fun HomeScreen(
    viewModel: PharmaViewModel,
    onNavigateToCatalog: () -> Unit,
    onNavigateToPrescriptions: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAdvice: () -> Unit = {},
    onNavigateToTracking: (OrderEntity) -> Unit = {},
    onMedicineClick: (Medicine) -> Unit,
    onPharmacyClick: (Pharmacy) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredMedicines by viewModel.filteredMedicines.collectAsStateWithLifecycle()
    val filteredPharmacies by viewModel.filteredPharmacies.collectAsStateWithLifecycle()
    val dutyOnly by viewModel.dutyOnlyFilter.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val currentDeliveryAddress by viewModel.userDeliveryAddress.collectAsStateWithLifecycle()
    val currentUserName by viewModel.userName.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val telemetry by viewModel.liveTelemetry.collectAsStateWithLifecycle()

    val activeOrder = orders.firstOrNull {
        it.status != OrderStatus.DELIVERED.name && it.status != OrderStatus.CANCELLED.name
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Location & Guarantee Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedicalTealPrimary)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToProfile() }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Localisation",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "LIVRAISON POUR ${currentUserName.uppercase()}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentDeliveryAddress,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_home_profile")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Mon Profil & Adresses",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Live Order Delivery Tracker Card (if active order exists)
        if (activeOrder != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                        .clickable { onNavigateToTracking(activeOrder) }
                        .testTag("home_live_order_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00332C)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(VerifiedBadgeGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "LIVRAISON EN COURS",
                                            color = Color(0xFF00E676),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF00E676))
                                        )
                                    }
                                    Text(
                                        text = activeOrder.pharmacyName,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Button(
                                onClick = { onNavigateToTracking(activeOrder) },
                                colors = ButtonDefaults.buttonColors(containerColor = VerifiedBadgeGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Suivre", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Livreur: ${telemetry.courierName} • ${telemetry.currentStreet}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            val mins = telemetry.etaSeconds / 60
                            Text(
                                text = if (telemetry.etaSeconds > 0) "~$mins min" else "Arrivé",
                                color = Color(0xFFFFD54F),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // Hero Banner Card with visual image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_pharmacy_hero),
                    contentDescription = "Pharmacie en ligne sécurisée",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC004D40), Color(0xF000332C))
                            )
                        )
                )

                // Text Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VerifiedBadgeGreen)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SANS SE DÉPLACER",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Livraison express 20-35 min",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Vos médicaments livrés directement chez vous",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Paiement en ligne sécurisé (Orange Money, Wave, CB)",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Quick Action Tiles (Upload Prescription, Duty Pharmacies, Fast Catalog)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Action 1: Upload Prescription
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_upload_prescription")
                        .clickable { onNavigateToPrescriptions() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SafeBlueLight)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SafeBlueSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = "Ordonnance",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Scanner l'Ordonnance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SafeBlueSecondary,
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "Envoyer à la pharmacie",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }
                }

                // Action 2: Duty Pharmacies 24h
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_duty_pharmacies")
                        .clickable {
                            viewModel.toggleDutyOnlyFilter()
                            onNavigateToCatalog()
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DutyPharmacyBg)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DutyPharmacyOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nightlight,
                                contentDescription = "Pharmacies de Garde",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pharmacies de Garde",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DutyPharmacyOrange,
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "Ouvertes 24h/24",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }

        // Search Bar with clear affordance
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Rechercher un médicament, DCI, marque...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Rechercher",
                            tint = MedicalTealPrimary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )
            }
        }

        // Categories Scroll
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Catégories de Santé",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    TextButton(onClick = onNavigateToCatalog) {
                        Text("Voir tout", color = MedicalTealPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.categories) { cat ->
                        val isSelected = cat == "Tous"
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    viewModel.onCategorySelected(cat)
                                    onNavigateToCatalog()
                                },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MedicalTealPrimary else MaterialTheme.colorScheme.surface,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0EAE7)) else null
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else TextPrimaryDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Duty Pharmacies Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(DutyPharmacyOrange)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pharmacies Partenaires Proches",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    filteredPharmacies.take(2).forEach { pharmacy ->
                        PharmacyCard(
                            pharmacy = pharmacy,
                            onPharmacyClick = { onPharmacyClick(pharmacy) }
                        )
                    }
                }
            }
        }

        // Popular & Essential Medications
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Médicaments Fréquents & Disponibles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                TextButton(onClick = onNavigateToCatalog) {
                    Text("Catalogue complet", color = MedicalTealPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(filteredMedicines.take(4)) { medicine ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                MedicineCard(
                    medicine = medicine,
                    onMedicineClick = { onMedicineClick(medicine) },
                    onAddToCart = { viewModel.addToCart(medicine) }
                )
            }
        }

        // Reassurance & Security Banner
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF8F5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Engagement Qualité & Fiabilité",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MedicalTealDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Médicaments 100% authentiques issus d'officines agréées par l'Ordre des Pharmaciens.\n" +
                               "• Respect de la chaîne du froid et emballage scellé inviolable.\n" +
                               "• Paiement en ligne sécurisé et traçabilité complète de votre commande.",
                        fontSize = 12.sp,
                        color = TextSecondaryMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
