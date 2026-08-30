package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.InitialData
import com.example.data.model.Medicine
import com.example.data.model.Pharmacy
import com.example.data.model.PharmacySortOption
import com.example.ui.components.MedicineCard
import com.example.ui.components.NearbyRadarMapCanvas
import com.example.ui.components.PharmacyCard
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.viewmodel.PharmaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: PharmaViewModel,
    onMedicineClick: (Medicine) -> Unit,
    onPharmacyClick: (Pharmacy) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Médicaments, 1: Pharmacies certifiées
    var pharmacyViewMode by remember { mutableStateOf("LIST") } // "LIST" or "RADAR"
    var showLocationSelectorDialog by remember { mutableStateOf(false) }
    var showSelectedPharmacySheet by remember { mutableStateOf<Pharmacy?>(null) }

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val dutyOnly by viewModel.dutyOnlyFilter.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val searchRadiusKm by viewModel.searchRadiusKm.collectAsStateWithLifecycle()
    val certifiedOnly by viewModel.certifiedOnlyFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.selectedSortOption.collectAsStateWithLifecycle()
    val preferredPharmacy by viewModel.selectedPreferredPharmacy.collectAsStateWithLifecycle()
    val isGpsDetecting by viewModel.isGpsDetecting.collectAsStateWithLifecycle()

    val filteredMedicines by viewModel.filteredMedicines.collectAsStateWithLifecycle()
    val filteredPharmacies by viewModel.filteredPharmacies.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("catalog_screen")
    ) {
        // Top Search & Location Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = {
                    Text(
                        if (selectedTab == 0) "Médicament, DCI, molécule..."
                        else "Pharmacie certifiée, quartier, garde..."
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MedicalTealPrimary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Effacer")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedicalTealPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Main Catalog Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MedicalTealPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MedicalTealPrimary
                    )
                }
            ) {
                Tab(
                    modifier = Modifier.testTag("tab_medicines"),
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Médicaments (${filteredMedicines.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    modifier = Modifier.testTag("tab_certified_pharmacies"),
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Pharmacies (${filteredPharmacies.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Certifiées",
                                tint = VerifiedBadgeGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // Category Filters Horizontal Bar
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onCategorySelected(cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MedicalTealPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Medicines List
            if (filteredMedicines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = TextSecondaryMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aucun médicament trouvé",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Essayez un autre mot-clé ou catégorie",
                            fontSize = 13.sp,
                            color = TextSecondaryMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredMedicines) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            onMedicineClick = { onMedicineClick(medicine) },
                            onAddToCart = { viewModel.addToCart(medicine) }
                        )
                    }
                }
            }
        } else {
            // Pharmacies Tab - Certified Pharmacy Search by Real-time Location
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("pharmacies_location_section")
            ) {
                // GPS Location Header Bar with Quick Switcher & GPS Detector
                Surface(
                    color = Color(0xFFF1F8F6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { showLocationSelectorDialog = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MedicalTealLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isGpsDetecting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = MedicalTealPrimary
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.MyLocation,
                                            contentDescription = "GPS",
                                            tint = MedicalTealPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Position GPS de recherche",
                                            fontSize = 10.sp,
                                            color = MedicalTealDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = MedicalTealDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = userLocation.addressName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // View Mode Toggle (Radar vs List)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                    .padding(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (pharmacyViewMode == "LIST") MedicalTealPrimary else Color.Transparent,
                                    modifier = Modifier
                                        .clickable { pharmacyViewMode = "LIST" }
                                        .testTag("mode_list_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "Vue Liste",
                                        tint = if (pharmacyViewMode == "LIST") Color.White else TextSecondaryMuted,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(18.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (pharmacyViewMode == "RADAR") MedicalTealPrimary else Color.Transparent,
                                    modifier = Modifier
                                        .clickable { pharmacyViewMode = "RADAR" }
                                        .testTag("mode_radar_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Radar,
                                        contentDescription = "Vue Radar Proximité",
                                        tint = if (pharmacyViewMode == "RADAR") Color.White else TextSecondaryMuted,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Filter & Sort Chips Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Certified Only Filter
                    item {
                        FilterChip(
                            modifier = Modifier.testTag("filter_certified_only"),
                            selected = certifiedOnly,
                            onClick = { viewModel.toggleCertifiedOnlyFilter() },
                            label = { Text("Certifiées Ordre SN", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = if (certifiedOnly) Color.White else VerifiedBadgeGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VerifiedBadgeGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    // Duty Pharmacy Filter
                    item {
                        FilterChip(
                            modifier = Modifier.testTag("filter_duty_pharmacies"),
                            selected = dutyOnly,
                            onClick = { viewModel.toggleDutyOnlyFilter() },
                            label = { Text("De Garde 24h", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Nightlight,
                                    contentDescription = null,
                                    tint = if (dutyOnly) Color.White else DutyPharmacyOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DutyPharmacyOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    // Radius filters
                    listOf(5.0 to "< 5 km", 10.0 to "< 10 km", 25.0 to "< 25 km", 0.0 to "Tout Sénégal").forEach { (radius, label) ->
                        item {
                            val isSelected = searchRadiusKm == radius
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSearchRadius(radius) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MedicalTealPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Sort Options
                    PharmacySortOption.values().forEach { option ->
                        item {
                            val isSelected = sortOption == option
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSortOption(option) },
                                label = { Text(option.label, fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Sort,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else MedicalTealPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MedicalTealDark,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Main Content View (Radar or List)
                if (pharmacyViewMode == "RADAR") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(6.dp))
                        NearbyRadarMapCanvas(
                            userLocation = userLocation,
                            pharmacies = filteredPharmacies,
                            selectedPharmacy = showSelectedPharmacySheet,
                            onSelectPharmacy = { selectedPharm ->
                                showSelectedPharmacySheet = selectedPharm
                            },
                            searchRadiusKm = searchRadiusKm,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Radar list summary below canvas
                        Text(
                            text = "Pharmacies détectées par radar (${filteredPharmacies.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredPharmacies) { pharmacy ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showSelectedPharmacySheet = pharmacy
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = pharmacy.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = TextPrimaryDark
                                                )
                                                if (pharmacy.isCertified) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        Icons.Default.Verified,
                                                        contentDescription = "Certifiée",
                                                        tint = VerifiedBadgeGreen,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "${pharmacy.district} • ${pharmacy.pharmacistInCharge}",
                                                fontSize = 11.sp,
                                                color = TextSecondaryMuted
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MedicalTealLight
                                            ) {
                                                Text(
                                                    text = "${pharmacy.distanceKm} km",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MedicalTealPrimary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "~${pharmacy.estimatedDeliveryMinutes} min",
                                                fontSize = 10.sp,
                                                color = TextSecondaryMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // List View
                    if (filteredPharmacies.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.LocalPharmacy,
                                    contentDescription = null,
                                    tint = TextSecondaryMuted,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Aucune pharmacie trouvée",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Augmentez le rayon de recherche ou modifiez les filtres",
                                    fontSize = 13.sp,
                                    color = TextSecondaryMuted,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        viewModel.setSearchRadius(0.0)
                                        viewModel.onSearchQueryChanged("")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                                ) {
                                    Text("Afficher toutes les pharmacies")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredPharmacies) { pharmacy ->
                                val isPreferred = preferredPharmacy?.id == pharmacy.id
                                Column {
                                    PharmacyCard(
                                        pharmacy = pharmacy,
                                        onPharmacyClick = { onPharmacyClick(pharmacy) }
                                    )
                                    // Quick select button below card
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp, end = 4.dp),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isPreferred) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = VerifiedBadgeGreen,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Pharmacie sélectionnée",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = VerifiedBadgeGreen
                                                )
                                            }
                                        } else {
                                            TextButton(
                                                onClick = {
                                                    viewModel.selectPreferredPharmacy(pharmacy)
                                                },
                                                modifier = Modifier.testTag("select_pharmacy_${pharmacy.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MedicalTealPrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Choisir cette officine",
                                                    fontSize = 11.sp,
                                                    color = MedicalTealPrimary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet: Selected Pharmacy from Radar Map
    showSelectedPharmacySheet?.let { pharm ->
        ModalBottomSheet(
            onDismissRequest = { showSelectedPharmacySheet = null },
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.testTag("radar_pharmacy_bottom_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 36.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = pharm.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            if (pharm.isCertified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Certifiée",
                                    tint = VerifiedBadgeGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = pharm.pharmacistInCharge,
                            fontSize = 12.sp,
                            color = TextSecondaryMuted
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF8E1)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${pharm.rating} (${pharm.reviewCount})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F8F6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Distance GPS", fontSize = 11.sp, color = TextSecondaryMuted)
                                Text("${pharm.distanceKm} km", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MedicalTealPrimary)
                            }
                            Column {
                                Text("Délai estimé", fontSize = 11.sp, color = TextSecondaryMuted)
                                Text("~${pharm.estimatedDeliveryMinutes} min", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
                            }
                            Column {
                                Text("Frais livraison", fontSize = 11.sp, color = TextSecondaryMuted)
                                Text("${pharm.deliveryFeeFcfa} FCFA", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MedicalTealDark)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Adresse : ${pharm.address}, ${pharm.district}, ${pharm.city}",
                            fontSize = 12.sp,
                            color = TextPrimaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.selectPreferredPharmacy(pharm)
                            showSelectedPharmacySheet = null
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sélectionner")
                    }

                    Button(
                        onClick = {
                            showSelectedPharmacySheet = null
                            onPharmacyClick(pharm)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                    ) {
                        Text("Voir l'officine")
                    }
                }
            }
        }
    }

    // Location Selector Dialog (Switch user coordinates / GPS preset)
    if (showLocationSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showLocationSelectorDialog = false },
            modifier = Modifier.testTag("location_presets_dialog"),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MedicalTealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choisir votre localisation", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Sélectionnez votre quartier ou ville pour recalculer en temps réel la distance de toutes les pharmacies certifiées :",
                        fontSize = 12.sp,
                        color = TextSecondaryMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated GPS Auto-detect Button
                    Button(
                        onClick = {
                            viewModel.simulateGpsLocationDetection()
                            showLocationSelectorDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("detect_gps_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GpsFixed, contentDescription = null, tint = VerifiedBadgeGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Détecter ma position GPS réelle", color = MedicalTealDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.height(220.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(viewModel.locationPresets) { preset ->
                            val isCurrent = userLocation.addressName == preset.name
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setLocationPreset(preset)
                                        showLocationSelectorDialog = false
                                    },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrent) MedicalTealLight else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, MedicalTealPrimary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = preset.name,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = TextPrimaryDark
                                        )
                                        Text(
                                            text = "${preset.district} (${preset.latitude}, ${preset.longitude})",
                                            fontSize = 10.sp,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                    if (isCurrent) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationSelectorDialog = false }) {
                    Text("Fermer", color = MedicalTealPrimary)
                }
            }
        )
    }
}
