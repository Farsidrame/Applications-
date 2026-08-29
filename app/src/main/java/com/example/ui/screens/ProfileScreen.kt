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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DeliveryAddressEntity
import com.example.data.model.UserProfileEntity
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: PharmaViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val deliveryAddresses by viewModel.deliveryAddresses.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val prescriptions by viewModel.prescriptions.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Coordonnées & Contact", "Adresses de Livraison (${deliveryAddresses.size})")

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Dialog state for Address Add/Edit
    var showAddressDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<DeliveryAddressEntity?>(null) }

    // Dialog state for delete confirmation
    var addressToDelete by remember { mutableStateOf<DeliveryAddressEntity?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Gestion de Profil",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Coordonnées & Adresses au Sénégal",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MedicalTealPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Profile Hero Header Card
            ProfileHeroCard(
                profile = userProfile,
                ordersCount = orders.size,
                prescriptionsCount = prescriptions.size,
                addressesCount = deliveryAddresses.size
            )

            // Tabs Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MedicalTealPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MedicalTealPrimary,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier.testTag("profile_tab_$index"),
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) MedicalTealPrimary else TextSecondaryMuted
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    ContactInformationTab(
                        profile = userProfile,
                        onSaveContact = { fullName, email, phone, secPhone, emerName, emerPhone, blood, allergies, paymentMethod, medNotes ->
                            viewModel.updateContactDetails(
                                fullName = fullName,
                                email = email,
                                phoneNumber = phone,
                                secondaryPhone = secPhone,
                                emergencyContactName = emerName,
                                emergencyContactPhone = emerPhone,
                                bloodGroup = blood,
                                knownAllergies = allergies,
                                preferredPaymentMethod = paymentMethod,
                                medicalNotes = medNotes,
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Coordonnées et informations de contact enregistrées avec succès !")
                                    }
                                }
                            )
                        }
                    )
                }
                1 -> {
                    DeliveryAddressesTab(
                        addresses = deliveryAddresses,
                        onAddNewAddress = {
                            editingAddress = null
                            showAddressDialog = true
                        },
                        onEditAddress = { addr ->
                            editingAddress = addr
                            showAddressDialog = true
                        },
                        onDeleteAddress = { addr ->
                            addressToDelete = addr
                        },
                        onSetDefault = { addr ->
                            viewModel.setDefaultDeliveryAddress(addr.id)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Adresse principale mise à jour : ${addr.title}")
                            }
                        }
                    )
                }
            }
        }
    }

    // Address Add / Edit Dialog
    if (showAddressDialog) {
        AddressEditDialog(
            address = editingAddress,
            onDismiss = { showAddressDialog = false },
            onSave = { title, recipient, phone, fullAddress, neighborhood, city, region, instructions, isDefault ->
                viewModel.saveDeliveryAddress(
                    id = editingAddress?.id,
                    title = title,
                    recipientName = recipient,
                    contactPhone = phone,
                    fullAddress = fullAddress,
                    neighborhood = neighborhood,
                    city = city,
                    region = region,
                    courierInstructions = instructions,
                    isDefault = isDefault,
                    onSuccess = {
                        showAddressDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                if (editingAddress == null) "Nouvelle adresse de livraison ajoutée !"
                                else "Adresse de livraison mise à jour !"
                            )
                        }
                    }
                )
            }
        )
    }

    // Delete Address Confirmation Dialog
    addressToDelete?.let { addr ->
        AlertDialog(
            onDismissRequest = { addressToDelete = null },
            title = {
                Text(text = "Supprimer l'adresse ?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "Êtes-vous sûr de vouloir supprimer l'adresse \"${addr.title}\" (${addr.neighborhood}, ${addr.city}) ?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDeliveryAddress(addr.id)
                        addressToDelete = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Adresse supprimée avec succès")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { addressToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun ProfileHeroCard(
    profile: UserProfileEntity?,
    ordersCount: Int,
    prescriptionsCount: Int,
    addressesCount: Int
) {
    val name = profile?.fullName ?: "Mamadou Dramé"
    val email = profile?.email ?: "drame678mamadou@gmail.com"
    val phone = profile?.phoneNumber ?: "+221 77 654 32 10"

    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifEmpty { "MD" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circle with initials
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MedicalTealPrimary, MedicalTealDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Vérifié",
                            tint = VerifiedBadgeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = email,
                        fontSize = 12.sp,
                        color = TextSecondaryMuted
                    )
                    Text(
                        text = phone,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MedicalTealPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(12.dp))

            // Quick Stats summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileStatBadge(
                    count = addressesCount.toString(),
                    label = "Adresses",
                    color = SafeBlueSecondary
                )
                ProfileStatBadge(
                    count = ordersCount.toString(),
                    label = "Commandes",
                    color = MedicalTealPrimary
                )
                ProfileStatBadge(
                    count = prescriptionsCount.toString(),
                    label = "Ordonnances",
                    color = DutyPharmacyOrange
                )
            }
        }
    }
}

@Composable
private fun ProfileStatBadge(
    count: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondaryMuted
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactInformationTab(
    profile: UserProfileEntity?,
    onSaveContact: (
        fullName: String,
        email: String,
        phone: String,
        secondaryPhone: String,
        emergencyName: String,
        emergencyPhone: String,
        bloodGroup: String,
        allergies: String,
        paymentMethod: String,
        medicalNotes: String
    ) -> Unit
) {
    var fullName by remember(profile) { mutableStateOf(profile?.fullName ?: "Mamadou Dramé") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "drame678mamadou@gmail.com") }
    var phone by remember(profile) { mutableStateOf(profile?.phoneNumber ?: "+221 77 654 32 10") }
    var secondaryPhone by remember(profile) { mutableStateOf(profile?.secondaryPhone ?: "+221 78 123 45 67") }
    var emergencyName by remember(profile) { mutableStateOf(profile?.emergencyContactName ?: "Fatou Dramé (Épouse)") }
    var emergencyPhone by remember(profile) { mutableStateOf(profile?.emergencyContactPhone ?: "+221 76 987 65 43") }
    var bloodGroup by remember(profile) { mutableStateOf(profile?.bloodGroup ?: "O+") }
    var allergies by remember(profile) { mutableStateOf(profile?.knownAllergies ?: "Pénicilline (Légère)") }
    var paymentMethod by remember(profile) { mutableStateOf(profile?.preferredPaymentMethod ?: "Wave Mobile Money") }
    var medicalNotes by remember(profile) { mutableStateOf(profile?.medicalNotes ?: "Suivi régulier tension artérielle") }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Non déterminé")
    val paymentMethods = listOf("Wave Mobile Money", "Orange Money", "MTN MoMo", "Carte Bancaire (Visa / Mastercard)", "Garantie Sécurisée")

    var bloodDropdownExpanded by remember { mutableStateOf(false) }
    var paymentDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Section 1: Informations Personnelles & Coordonnées
        Text(
            text = "Informations Personnelles & Contact",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MedicalTealPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Nom & Prénom
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nom complet du patient") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MedicalTealPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Adresse Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MedicalTealPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_email"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Téléphone Principal
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Numéro de téléphone principal (Wave / SMS)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Téléphone Secondaire / WhatsApp
                OutlinedTextField(
                    value = secondaryPhone,
                    onValueChange = { secondaryPhone = it },
                    label = { Text("Numéro secondaire / WhatsApp (Optionnel)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SafeBlueSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_secondary_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Contact d'Urgence & Données Médicales
        Text(
            text = "Contact d'Urgence & Profil Santé",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MedicalTealPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Contact d'urgence Nom
                OutlinedTextField(
                    value = emergencyName,
                    onValueChange = { emergencyName = it },
                    label = { Text("Contact d'urgence (Nom & Lien de parenté)") },
                    leadingIcon = { Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = DutyPharmacyOrange) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_emergency_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Téléphone d'urgence
                OutlinedTextField(
                    value = emergencyPhone,
                    onValueChange = { emergencyPhone = it },
                    label = { Text("Téléphone d'urgence") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = DutyPharmacyOrange) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_emergency_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Groupe Sanguin Dropdown
                ExposedDropdownMenuBox(
                    expanded = bloodDropdownExpanded,
                    onExpandedChange = { bloodDropdownExpanded = !bloodDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Groupe Sanguin") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = bloodDropdownExpanded,
                        onDismissRequest = { bloodDropdownExpanded = false }
                    ) {
                        bloodGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group) },
                                onClick = {
                                    bloodGroup = group
                                    bloodDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Allergies connues
                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Allergies médicamenteuses connues") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = MedicalEmeraldAccent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_allergies"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mode de paiement préféré
                ExposedDropdownMenuBox(
                    expanded = paymentDropdownExpanded,
                    onExpandedChange = { paymentDropdownExpanded = !paymentDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mode de Paiement Préféré") },
                        leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null, tint = MedicalTealPrimary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = paymentDropdownExpanded,
                        onDismissRequest = { paymentDropdownExpanded = false }
                    ) {
                        paymentMethods.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method) },
                                onClick = {
                                    paymentMethod = method
                                    paymentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes médicales
                OutlinedTextField(
                    value = medicalNotes,
                    onValueChange = { medicalNotes = it },
                    label = { Text("Notes médicales & Antécédents") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_medical_notes"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                onSaveContact(
                    fullName,
                    email,
                    phone,
                    secondaryPhone,
                    emergencyName,
                    emergencyPhone,
                    bloodGroup,
                    allergies,
                    paymentMethod,
                    medicalNotes
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_save_profile"),
            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Enregistrer mes Coordonnées",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun DeliveryAddressesTab(
    addresses: List<DeliveryAddressEntity>,
    onAddNewAddress: () -> Unit,
    onEditAddress: (DeliveryAddressEntity) -> Unit,
    onDeleteAddress: (DeliveryAddressEntity) -> Unit,
    onSetDefault: (DeliveryAddressEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("delivery_addresses_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalTealLight.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MedicalTealPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MedicalTealPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Livraison Express au Sénégal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MedicalTealDark
                        )
                        Text(
                            text = "Définissez vos adresses à Dakar, Thiès, Touba et régions pour une livraison rapide par nos coursiers certifiés.",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }

        // Add Address Action Button
        item {
            Button(
                onClick = onAddNewAddress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_add_new_address"),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ajouter une nouvelle adresse de livraison",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Addresses list
        if (addresses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = TextSecondaryMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aucune adresse enregistrée",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ajoutez une adresse pour commander vos médicaments en 1 clic.",
                            fontSize = 12.sp,
                            color = TextSecondaryMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(addresses, key = { it.id }) { address ->
                AddressItemCard(
                    address = address,
                    onEdit = { onEditAddress(address) },
                    onDelete = { onDeleteAddress(address) },
                    onSetDefault = { onSetDefault(address) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun AddressItemCard(
    address: DeliveryAddressEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    val isHome = address.title.contains("Domicile", ignoreCase = true) || address.title.contains("Maison", ignoreCase = true)
    val isWork = address.title.contains("Bureau", ignoreCase = true) || address.title.contains("Travail", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("address_card_${address.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (address.isDefault) androidx.compose.foundation.BorderStroke(2.dp, MedicalTealPrimary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (address.isDefault) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Title + Default badge + Edit/Delete actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            isHome -> Icons.Default.Home
                            isWork -> Icons.Default.Work
                            else -> Icons.Default.LocationOn
                        },
                        contentDescription = null,
                        tint = if (address.isDefault) MedicalTealPrimary else SafeBlueSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = address.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimaryDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (address.isDefault) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MedicalTealPrimary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MedicalTealPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Par Défaut",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalTealPrimary
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp).testTag("btn_edit_addr_${address.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = SafeBlueSecondary, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp).testTag("btn_delete_addr_${address.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details
            Text(
                text = address.fullAddress,
                fontSize = 13.sp,
                color = TextPrimaryDark,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "${address.neighborhood}, ${address.city} (${address.region})",
                fontSize = 12.sp,
                color = TextSecondaryMuted
            )

            if (address.recipientName.isNotBlank() || address.contactPhone.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondaryMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Contact : ${address.recipientName} • ${address.contactPhone}",
                        fontSize = 11.sp,
                        color = TextSecondaryMuted
                    )
                }
            }

            if (address.courierInstructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SafeBlueSecondary,
                            modifier = Modifier.size(14.dp).padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Instructions coursier : ${address.courierInstructions}",
                            fontSize = 11.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }

            // Set as default action if not currently default
            if (!address.isDefault) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onSetDefault,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("btn_set_default_addr_${address.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalTealPrimary)
                ) {
                    Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Définir comme adresse principale de livraison", fontSize = 11.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressEditDialog(
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
    var recipient by remember { mutableStateOf(address?.recipientName ?: "Mamadou Dramé") }
    var phone by remember { mutableStateOf(address?.contactPhone ?: "+221 77 654 32 10") }
    var fullAddress by remember { mutableStateOf(address?.fullAddress ?: "") }
    var neighborhood by remember { mutableStateOf(address?.neighborhood ?: "Sacré-Cœur / Keur Gorgui") }
    var city by remember { mutableStateOf(address?.city ?: "Dakar") }
    var region by remember { mutableStateOf(address?.region ?: "Dakar") }
    var instructions by remember { mutableStateOf(address?.courierInstructions ?: "") }
    var isDefault by remember { mutableStateOf(address?.isDefault ?: false) }

    val popularNeighborhoods = listOf(
        "Sacré-Cœur / Keur Gorgui",
        "Plateau / Centre-ville",
        "Mermoz",
        "Almadies",
        "Ngor",
        "Ouakam",
        "Fann Résidence / Point E",
        "Maristes / Hann",
        "Yoff",
        "Liberté 6 / SICAP",
        "Grand Dakar",
        "Pikine",
        "Guédiawaye",
        "Rufisque",
        "Thiès Centre",
        "Touba Mosquée",
        "Saint-Louis Île"
    )

    val popularCities = listOf("Dakar", "Thiès", "Saint-Louis", "Touba", "Mbour", "Kaolack", "Ziguinchor")

    var neighborhoodDropdownExpanded by remember { mutableStateOf(false) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("dialog_address_form")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (address == null) "Nouvelle Adresse" else "Modifier l'Adresse",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title label
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nom de l'adresse (ex: Domicile, Bureau, Famille)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Recipient
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Nom du destinataire") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_recipient"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone pour le coursier") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Full Address
                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text("Adresse détaillée (Rue, Résidence, Bâtiment, Porte)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_full"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Neighborhood Dropdown
                ExposedDropdownMenuBox(
                    expanded = neighborhoodDropdownExpanded,
                    onExpandedChange = { neighborhoodDropdownExpanded = !neighborhoodDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Quartier") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = neighborhoodDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("input_addr_neighborhood"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = neighborhoodDropdownExpanded,
                        onDismissRequest = { neighborhoodDropdownExpanded = false }
                    ) {
                        popularNeighborhoods.forEach { neigh ->
                            DropdownMenuItem(
                                text = { Text(neigh) },
                                onClick = {
                                    neighborhood = neigh
                                    neighborhoodDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // City Dropdown
                ExposedDropdownMenuBox(
                    expanded = cityDropdownExpanded,
                    onExpandedChange = { cityDropdownExpanded = !cityDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = {
                            city = it
                            region = it
                        },
                        label = { Text("Ville / Région") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("input_addr_city"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = cityDropdownExpanded,
                        onDismissRequest = { cityDropdownExpanded = false }
                    ) {
                        popularCities.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    city = c
                                    region = c
                                    cityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Instructions for courier
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions coursier (ex: 2ème étage, interphone 42)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_instructions"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Set as Default switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF9F9F9))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Adresse principale",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Utilisée par défaut lors du paiement",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MedicalTealPrimary
                        ),
                        modifier = Modifier.testTag("switch_addr_default")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler", color = TextSecondaryMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fullAddress.isBlank()) {
                                fullAddress = "$neighborhood, $city"
                            }
                            onSave(
                                title,
                                recipient,
                                phone,
                                fullAddress,
                                neighborhood,
                                city,
                                region,
                                instructions,
                                isDefault
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_save_addr_dialog")
                    ) {
                        Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
