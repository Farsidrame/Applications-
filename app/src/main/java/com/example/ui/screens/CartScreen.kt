package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.local.InitialData
import com.example.data.model.CartItemEntity
import com.example.ui.components.PrescriptionRequiredBadge
import com.example.ui.components.PrescriptionUploadDialog
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.PrescriptionAlertBg
import com.example.ui.theme.PrescriptionAlertRed
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.viewmodel.PharmaViewModel

@Composable
fun CartScreen(
    viewModel: PharmaViewModel,
    onNavigateToCheckout: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val safetyAlerts by viewModel.safetyAlerts.collectAsStateWithLifecycle()
    val isPrescriptionAttached by viewModel.isPrescriptionAttachedToCart.collectAsStateWithLifecycle()
    val attachedPrescriptionSummary by viewModel.attachedPrescriptionSummary.collectAsStateWithLifecycle()
    val userAddress by viewModel.userDeliveryAddress.collectAsStateWithLifecycle()
    val savedAddresses by viewModel.deliveryAddresses.collectAsStateWithLifecycle()

    var showPrescriptionDialog by remember { mutableStateOf(false) }
    var isEditingAddress by remember { mutableStateOf(false) }
    var addressInput by remember { mutableStateOf(userAddress) }
    var selectedRegion by remember { mutableStateOf("Dakar") }
    var selectedCity by remember { mutableStateOf("Dakar") }
    var selectedNeighborhood by remember { mutableStateOf("Sacré-Cœur / Keur Gorgui") }
    var regionDropdownExpanded by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.priceFcfa * it.quantity }
    val defaultPharmacy = InitialData.pharmacies.first()
    val deliveryFee = if (cartItems.isEmpty()) 0 else defaultPharmacy.deliveryFeeFcfa
    val total = subtotal + deliveryFee

    val hasPrescriptionItems = cartItems.any { it.requiresPrescription }

    if (showPrescriptionDialog) {
        PrescriptionUploadDialog(
            initialPatientName = viewModel.userName.value,
            onDismiss = { showPrescriptionDialog = false },
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
                    pharmacyRegion = pharmRegion
                )
            }
        )
    }

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp)
                .testTag("empty_cart_view"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MedicalTealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MedicalTealPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Votre panier est vide",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Commandez vos médicaments et faites-vous livrer en 20 min sans vous déplacer.",
                    fontSize = 13.sp,
                    color = TextSecondaryMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToCatalog,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                    modifier = Modifier.testTag("explore_medicines_button")
                ) {
                    Text("Explorer les médicaments", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("cart_screen")
        ) {
            // Cart List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pharmacy fulfillment header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MedicalTealLight.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
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
                                    text = "Préparation par : ${defaultPharmacy.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MedicalTealDark
                                )
                                Text(
                                    text = "Livraison express estimée : ~${defaultPharmacy.estimatedDeliveryMinutes} minutes",
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                            }
                        }
                    }
                }

                // Safety Alerts (Drug interactions or Prescription requirement)
                items(safetyAlerts) { alert ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PrescriptionAlertBg)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = PrescriptionAlertRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = alert,
                                fontSize = 11.sp,
                                color = PrescriptionAlertRed,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Prescription status or upload CTA
                if (hasPrescriptionItems) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPrescriptionAttached) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isPrescriptionAttached) Icons.Default.CheckCircle else Icons.Default.Description,
                                            contentDescription = null,
                                            tint = if (isPrescriptionAttached) VerifiedBadgeGreen else Color(0xFFE65100),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isPrescriptionAttached) "Ordonnance Médicale Validée ✓" else "Ordonnance Requise",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isPrescriptionAttached) VerifiedBadgeGreen else Color(0xFFE65100)
                                            )
                                            Text(
                                                text = attachedPrescriptionSummary ?: "Obligatoire pour les antibiotiques / asthme",
                                                fontSize = 11.sp,
                                                color = TextSecondaryMuted
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { showPrescriptionDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isPrescriptionAttached) MedicalTealPrimary else Color(0xFFE65100)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(
                                            text = if (isPrescriptionAttached) "Modifier" else "Joindre photo",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Cart Items
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { viewModel.updateCartItemQuantity(item, 1) },
                        onDecrease = { viewModel.updateCartItemQuantity(item, -1) },
                        onRemove = { viewModel.removeCartItem(item.id) }
                    )
                }

                // Delivery Address Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🇸🇳", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Adresse de livraison (Sénégal)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimaryDark
                                    )
                                }

                                TextButton(
                                    onClick = { isEditingAddress = !isEditingAddress },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        if (isEditingAddress) Icons.Default.Close else Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = MedicalTealPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEditingAddress) "Fermer" else "Changer / Région",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalTealPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Display current address
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF7FAF9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MedicalTealPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = userAddress,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimaryDark
                                        )
                                        Text(
                                            text = "Livraison express disponible sur l'ensemble des 14 régions du Sénégal",
                                            fontSize = 10.sp,
                                            color = MedicalTealDark
                                        )
                                    }
                                }
                            }

                            // Quick saved addresses chips
                            if (savedAddresses.size > 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Adresses enregistrées :",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondaryMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(savedAddresses) { addr ->
                                        val isCurrent = userAddress.contains(addr.neighborhood) || userAddress == addr.fullAddress
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isCurrent) MedicalTealLight else Color(0xFFF1F1F1),
                                            border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, MedicalTealPrimary) else null,
                                            modifier = Modifier.clickable {
                                                viewModel.userDeliveryAddress.value = "${addr.fullAddress}, ${addr.neighborhood}, ${addr.city} (${addr.region})"
                                                addressInput = "${addr.fullAddress}, ${addr.neighborhood}, ${addr.city} (${addr.region})"
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    if (isCurrent) Icons.Default.CheckCircle else Icons.Default.Home,
                                                    contentDescription = null,
                                                    tint = if (isCurrent) MedicalTealPrimary else TextSecondaryMuted,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${addr.title} (${addr.region})",
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isCurrent) MedicalTealDark else TextPrimaryDark
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Expandable Editor with 14 Senegal regions
                            AnimatedVisibility(visible = isEditingAddress) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    HorizontalDivider(color = Color(0xFFEEEEEE))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "1. Sélectionner une région du Sénégal :",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    // 14 regions chips
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(InitialData.senegalRegionsList) { reg ->
                                            val isSel = reg == selectedRegion
                                            FilterChip(
                                                selected = isSel,
                                                onClick = {
                                                    selectedRegion = reg
                                                    val info = InitialData.senegalAdministrativeData[reg]
                                                    if (info != null) {
                                                        selectedCity = info.capital
                                                        selectedNeighborhood = info.popularNeighborhoods.firstOrNull() ?: info.capital
                                                        addressInput = "$selectedNeighborhood, $selectedCity ($selectedRegion)"
                                                    }
                                                },
                                                label = { Text(reg, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = MedicalTealPrimary,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Pre-set City & Neighborhood pills for chosen region
                                    val regionInfo = InitialData.senegalAdministrativeData[selectedRegion]
                                    if (regionInfo != null) {
                                        Text(
                                            text = "Villes & Quartiers de $selectedRegion :",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextSecondaryMuted
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(regionInfo.popularCities) { c ->
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (c == selectedCity) MedicalTealLight else Color(0xFFF3F3F3),
                                                    modifier = Modifier.clickable {
                                                        selectedCity = c
                                                        addressInput = "$selectedNeighborhood, $selectedCity ($selectedRegion)"
                                                    }
                                                ) {
                                                    Text(
                                                        text = c,
                                                        fontSize = 10.sp,
                                                        fontWeight = if (c == selectedCity) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (c == selectedCity) MedicalTealDark else TextPrimaryDark,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(regionInfo.popularNeighborhoods) { neigh ->
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (neigh == selectedNeighborhood) MedicalTealLight else Color(0xFFF3F3F3),
                                                    modifier = Modifier.clickable {
                                                        selectedNeighborhood = neigh
                                                        addressInput = "$selectedNeighborhood, $selectedCity ($selectedRegion)"
                                                    }
                                                ) {
                                                    Text(
                                                        text = neigh,
                                                        fontSize = 10.sp,
                                                        fontWeight = if (neigh == selectedNeighborhood) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (neigh == selectedNeighborhood) MedicalTealDark else TextPrimaryDark,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Full address input
                                    OutlinedTextField(
                                        value = addressInput,
                                        onValueChange = { addressInput = it },
                                        label = { Text("Adresse complète de livraison") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            if (addressInput.isNotBlank()) {
                                                viewModel.userDeliveryAddress.value = addressInput
                                            }
                                            isEditingAddress = false
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Appliquer cette adresse de livraison", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Summary & Checkout Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sous-total médicaments:", fontSize = 12.sp, color = TextSecondaryMuted)
                        Text("$subtotal FCFA", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Livraison à domicile express:", fontSize = 12.sp, color = TextSecondaryMuted)
                        Text("$deliveryFee FCFA", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFEFF4F2))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TOTAL À PAYER:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text(
                                text = "$total FCFA",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MedicalTealPrimary
                            )
                        }

                        Button(
                            onClick = onNavigateToCheckout,
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("proceed_to_payment_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Payer en ligne",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemEntity,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cart_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MedicalTealLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = MedicalTealPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.medicineName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "${item.dosageStrength} • ${item.priceFcfa} FCFA / unité",
                    fontSize = 11.sp,
                    color = TextSecondaryMuted
                )
                if (item.requiresPrescription) {
                    PrescriptionRequiredBadge(modifier = Modifier.padding(top = 2.dp))
                }
            }

            // Quantity buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F8F6))
                    .padding(2.dp)
            ) {
                IconButton(
                    onClick = onDecrease,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                        contentDescription = "Diminuer",
                        modifier = Modifier.size(14.dp),
                        tint = if (item.quantity == 1) Color.Red else TextPrimaryDark
                    )
                }

                Text(
                    text = "${item.quantity}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Augmenter", modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
