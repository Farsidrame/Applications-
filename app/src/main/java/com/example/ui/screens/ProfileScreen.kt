package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.InitialData
import com.example.data.model.DeliveryAddressEntity
import com.example.data.model.DeliveryCourierEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PharmacistRegistrationEntity
import com.example.data.model.UserProfileEntity
import com.example.ui.components.CourierManagementDialog
import com.example.ui.components.InvoiceDialog
import com.example.ui.components.PharmacistRegistrationDialog
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DutyPharmacyBg
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueLight
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.util.InvoicePrinterHelper
import com.example.ui.viewmodel.PharmaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: PharmaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val deliveryAddresses by viewModel.deliveryAddresses.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val prescriptions by viewModel.prescriptions.collectAsStateWithLifecycle()
    val pharmacists by viewModel.pharmacists.collectAsStateWithLifecycle()
    val couriers by viewModel.couriers.collectAsStateWithLifecycle()

    var activeMenuIndex by remember { mutableStateOf<Int?>(null) }

    data class ProfileMenuItem(
        val index: Int,
        val title: String,
        val subtitle: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val iconTint: Color,
        val iconBg: Color,
        val countBadge: String? = null,
        val testTag: String
    )

    val menuItems = listOf(
        ProfileMenuItem(
            index = 0,
            title = "Mon Profil & Contact d'Urgence",
            subtitle = "Identité, groupe sanguin, allergies & contact SOS",
            icon = Icons.Default.Person,
            iconTint = MedicalTealPrimary,
            iconBg = MedicalTealLight,
            countBadge = "Fiche santé",
            testTag = "profile_tab_0"
        ),
        ProfileMenuItem(
            index = 1,
            title = "Adresses de Livraison",
            subtitle = "Gérer vos points de repère et domiciles",
            icon = Icons.Default.LocationOn,
            iconTint = SafeBlueSecondary,
            iconBg = SafeBlueLight,
            countBadge = if (deliveryAddresses.isNotEmpty()) "${deliveryAddresses.size} enregistrée(s)" else "0 adresse",
            testTag = "profile_tab_1"
        ),
        ProfileMenuItem(
            index = 2,
            title = "Espace Pharmacien & Agréments",
            subtitle = "Inscriptions officines, licences & diplômes d'État",
            icon = Icons.Default.LocalPharmacy,
            iconTint = VerifiedBadgeGreen,
            iconBg = VerifiedBadgeBg,
            countBadge = if (pharmacists.isNotEmpty()) "${pharmacists.size} officine(s)" else "Inscription Pro",
            testTag = "profile_tab_2"
        ),
        ProfileMenuItem(
            index = 3,
            title = "Flotte de Livreurs",
            subtitle = "Gestion coursiers, véhicules isothermes & zones",
            icon = Icons.Default.TwoWheeler,
            iconTint = DutyPharmacyOrange,
            iconBg = DutyPharmacyBg,
            countBadge = if (couriers.isNotEmpty()) "${couriers.size} coursier(s)" else "0 livreur",
            testTag = "profile_tab_3"
        ),
        ProfileMenuItem(
            index = 4,
            title = "Factures Certifiées & Liens SMS",
            subtitle = "Reçus officiels conformes, impression & relance SMS",
            icon = Icons.Default.ReceiptLong,
            iconTint = MedicalTealDark,
            iconBg = MedicalTealLight,
            countBadge = if (orders.isNotEmpty()) "${orders.size} facture(s)" else "0 facture",
            testTag = "profile_tab_4"
        )
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Dialog state for Address Add/Edit
    var showAddressDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<DeliveryAddressEntity?>(null) }
    var addressToDelete by remember { mutableStateOf<DeliveryAddressEntity?>(null) }

    // Dialog state for Pharmacist Registration
    var showPharmacistDialog by remember { mutableStateOf(false) }

    // Dialog state for Courier Management
    var showCourierDialog by remember { mutableStateOf(false) }
    var editingCourier by remember { mutableStateOf<DeliveryCourierEntity?>(null) }
    var courierToDelete by remember { mutableStateOf<DeliveryCourierEntity?>(null) }

    // Invoice View Dialog
    var selectedOrderForInvoice by remember { mutableStateOf<OrderEntity?>(null) }

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
                            text = if (activeMenuIndex != null) {
                                menuItems.find { it.index == activeMenuIndex }?.title ?: "Espace Pro & Client"
                            } else "Espace Professionnel & Client",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Pharmaciens • Livreurs • Factures certifiées SN",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    if (activeMenuIndex != null) {
                        IconButton(onClick = { activeMenuIndex = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour aux rubriques",
                                tint = Color.White
                            )
                        }
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
            if (activeMenuIndex == null) {
                // Main Vertical Menu View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    // Profile Hero Header Card
                    ProfileHeroCard(
                        profile = userProfile,
                        ordersCount = orders.size,
                        prescriptionsCount = prescriptions.size,
                        addressesCount = deliveryAddresses.size,
                        pharmacistsCount = pharmacists.size,
                        couriersCount = couriers.size
                    )

                    // Vertical Menu Section Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "RUBRIQUES & GESTION DU COMPTE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalTealDark,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Sélectionnez une rubrique pour consulter ou modifier vos informations",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Vertical Menu Items List - Neatly Arranged & Aligned
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        menuItems.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag(item.testTag)
                                    .clickable { activeMenuIndex = item.index },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular Icon Container
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(item.iconBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = item.iconTint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    // Title and Description
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = item.subtitle,
                                            fontSize = 12.sp,
                                            color = TextSecondaryMuted,
                                            lineHeight = 16.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Badge Counter Pill
                                    item.countBadge?.let { badge ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(item.iconBg)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = badge,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = item.iconTint
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    // Chevron
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = "Ouvrir",
                                        tint = TextSecondaryMuted.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Section Detail Header (Quick return to menu)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeMenuIndex = null }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Retour à toutes les rubriques",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalTealPrimary
                        )
                    }
                }

                HorizontalDivider(color = BorderSoft)

                // Tab Content View
                when (activeMenuIndex) {
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
                                            snackbarHostState.showSnackbar("Coordonnées et profil enregistrés avec succès !")
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
                                    snackbarHostState.showSnackbar("Adresse principale : ${addr.title}")
                                }
                            }
                        )
                    }
                    2 -> {
                        PharmacistsTab(
                            pharmacists = pharmacists,
                            onRegisterNew = { showPharmacistDialog = true },
                            onDeletePharmacist = { id ->
                                viewModel.deletePharmacist(id)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Officine retirée de la liste")
                                }
                            }
                        )
                    }
                    3 -> {
                        CouriersTab(
                            couriers = couriers,
                            onAddNew = {
                                editingCourier = null
                                showCourierDialog = true
                            },
                            onEdit = { courier ->
                                editingCourier = courier
                                showCourierDialog = true
                            },
                            onDelete = { courier ->
                                courierToDelete = courier
                            },
                            onCall = { phone ->
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                context.startActivity(intent)
                            }
                        )
                    }
                    4 -> {
                        InvoicesTab(
                            orders = orders,
                            onViewInvoice = { order ->
                                selectedOrderForInvoice = order
                            },
                            onPrintInvoice = { order ->
                                InvoicePrinterHelper.printInvoice(context, order)
                            },
                            onSendSms = { order ->
                                viewModel.triggerInvoiceSms(order)
                                InvoicePrinterHelper.shareInvoiceSmsIntent(context, order)
                            }
                        )
                    }
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
                            snackbarHostState.showSnackbar("Adresse de livraison enregistrée !")
                        }
                    }
                )
            }
        )
    }

    // Pharmacist Registration Dialog
    if (showPharmacistDialog) {
        PharmacistRegistrationDialog(
            onDismiss = { showPharmacistDialog = false },
            onSubmit = { fullName, pharmacyName, region, city, district, phoneNumber, email, licenseNumber, orderRegistrationNumber, diplomaTitle ->
                viewModel.registerPharmacist(
                    fullName = fullName,
                    pharmacyName = pharmacyName,
                    region = region,
                    city = city,
                    district = district,
                    phoneNumber = phoneNumber,
                    email = email,
                    licenseNumber = licenseNumber,
                    orderRegistrationNumber = orderRegistrationNumber,
                    diplomaTitle = diplomaTitle,
                    onComplete = {
                        showPharmacistDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Inscription officinale validée ! SMS de confirmation transmis.")
                        }
                    }
                )
            }
        )
    }

    // Courier Management Dialog
    if (showCourierDialog) {
        CourierManagementDialog(
            courierToEdit = editingCourier,
            onDismiss = { showCourierDialog = false },
            onSave = { id, fullName, phoneNumber, nationalIdCardNumber, address, vehicleType, region, assignedZone, status ->
                viewModel.saveCourier(
                    id = id,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    nationalIdCardNumber = nationalIdCardNumber,
                    address = address,
                    vehicleType = vehicleType,
                    region = region,
                    assignedZone = assignedZone,
                    status = status,
                    onComplete = {
                        showCourierDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Fiche livreur mise à jour avec succès !")
                        }
                    }
                )
            }
        )
    }

    // Invoice Dialog Modal
    selectedOrderForInvoice?.let { order ->
        InvoiceDialog(
            order = order,
            onDismiss = { selectedOrderForInvoice = null },
            onSendSms = {
                viewModel.triggerInvoiceSms(order)
            }
        )
    }

    // Delete Address Confirmation Dialog
    addressToDelete?.let { addr ->
        AlertDialog(
            onDismissRequest = { addressToDelete = null },
            title = { Text(text = "Supprimer l'adresse ?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Êtes-vous sûr de vouloir supprimer l'adresse \"${addr.title}\" ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDeliveryAddress(addr.id)
                        addressToDelete = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Adresse supprimée")
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

    // Delete Courier Confirmation Dialog
    courierToDelete?.let { courier ->
        AlertDialog(
            onDismissRequest = { courierToDelete = null },
            title = { Text(text = "Supprimer le coursier ?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Voulez-vous retirer \"${courier.fullName}\" (${courier.phoneNumber}) de votre flotte ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCourier(courier.id)
                        courierToDelete = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Coursier retiré")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { courierToDelete = null }) {
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
    addressesCount: Int,
    pharmacistsCount: Int,
    couriersCount: Int
) {
    val name = if (profile != null && profile.fullName.isNotBlank()) profile.fullName else "Espace Utilisateur & Pro"
    val email = if (profile != null && profile.email.isNotBlank()) profile.email else "Profil non configuré"
    val phone = if (profile != null && profile.phoneNumber.isNotBlank()) profile.phoneNumber else "Ajoutez vos coordonnées ci-dessous"

    val initials = if (profile != null && profile.fullName.isNotBlank()) {
        profile.fullName.split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifEmpty { "PD" }
    } else "PD"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
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
                        fontSize = 20.sp,
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
                            contentDescription = "Certifié",
                            tint = VerifiedBadgeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = email, fontSize = 12.sp, color = TextSecondaryMuted)
                    Text(text = phone, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MedicalTealPrimary)
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
                ProfileStatBadge(count = addressesCount.toString(), label = "Adresses", color = SafeBlueSecondary)
                ProfileStatBadge(count = pharmacistsCount.toString(), label = "Officines", color = MedicalTealPrimary)
                ProfileStatBadge(count = couriersCount.toString(), label = "Livreurs", color = DutyPharmacyOrange)
                ProfileStatBadge(count = ordersCount.toString(), label = "Factures", color = Color(0xFF00875A))
            }
        }
    }
}

@Composable
private fun ProfileStatBadge(count: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 11.sp, color = TextSecondaryMuted)
    }
}

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
    var fullName by remember(profile) { mutableStateOf(profile?.fullName ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var phone by remember(profile) { mutableStateOf(profile?.phoneNumber ?: "") }
    var secondaryPhone by remember(profile) { mutableStateOf(profile?.secondaryPhone ?: "") }
    var emergencyName by remember(profile) { mutableStateOf(profile?.emergencyContactName ?: "") }
    var emergencyPhone by remember(profile) { mutableStateOf(profile?.emergencyContactPhone ?: "") }
    var bloodGroup by remember(profile) { mutableStateOf(profile?.bloodGroup ?: "") }
    var allergies by remember(profile) { mutableStateOf(profile?.knownAllergies ?: "") }
    var paymentMethod by remember(profile) { mutableStateOf(profile?.preferredPaymentMethod ?: "Wave Mobile Money") }
    var medicalNotes by remember(profile) { mutableStateOf(profile?.medicalNotes ?: "") }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Non déterminé")
    val paymentMethods = listOf("Wave Mobile Money", "Orange Money", "Free Money", "MTN MoMo", "Carte Bancaire (Visa/Mastercard)")

    var bloodDropdownExpanded by remember { mutableStateOf(false) }
    var paymentDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
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
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nom et prénom") },
                    placeholder = { Text("Ex: Dr. Mamadou Dramé") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MedicalTealPrimary) },
                    modifier = Modifier.fillMaxWidth().testTag("input_profile_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Adresse Email") },
                    placeholder = { Text("Ex: drame678mamadou@gmail.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MedicalTealPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("input_profile_email"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone principal (Wave / SMS)") },
                    placeholder = { Text("Ex: +221 77 000 00 00") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("input_profile_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = secondaryPhone,
                    onValueChange = { secondaryPhone = it },
                    label = { Text("Numéro secondaire / WhatsApp (Optionnel)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SafeBlueSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("input_profile_secondary_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Urgences & Profil Médical
        Text(
            text = "Contact d'Urgence & Profil Médical",
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
                OutlinedTextField(
                    value = emergencyName,
                    onValueChange = { emergencyName = it },
                    label = { Text("Personne à contacter en cas d'urgence") },
                    leadingIcon = { Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = DutyPharmacyOrange) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = emergencyPhone,
                    onValueChange = { emergencyPhone = it },
                    label = { Text("Téléphone du contact d'urgence") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = DutyPharmacyOrange) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Allergies connues (ex: Pénicilline, Aspirine...)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                onSaveContact(
                    fullName.trim(),
                    email.trim(),
                    phone.trim(),
                    secondaryPhone.trim(),
                    emergencyName.trim(),
                    emergencyPhone.trim(),
                    bloodGroup.trim(),
                    allergies.trim(),
                    paymentMethod.trim(),
                    medicalNotes.trim()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_save_profile_contact"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enregistrer mes informations", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lieux de Réception",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Button(
                    onClick = onAddNewAddress,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_new_address")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (addresses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            imageVector = Icons.Default.AddLocationAlt,
                            contentDescription = null,
                            tint = TextSecondaryMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aucune adresse enregistrée",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ajoutez votre domicile, bureau ou lieu de livraison pour recevoir vos commandes.",
                            fontSize = 12.sp,
                            color = TextSecondaryMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onAddNewAddress,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                        ) {
                            Text("Ajouter une adresse")
                        }
                    }
                }
            }
        } else {
            items(addresses) { address ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(address.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                                if (address.isDefault) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFE8F5E9))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("PAR DÉFAUT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = VerifiedBadgeGreen)
                                    }
                                }
                            }
                            Row {
                                IconButton(onClick = { onEditAddress(address) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = SafeBlueSecondary, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onDeleteAddress(address) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(address.fullAddress, fontSize = 12.sp, color = TextPrimaryDark)
                        Text("${address.neighborhood}, ${address.city} (${address.region})", fontSize = 11.sp, color = TextSecondaryMuted)

                        if (address.recipientName.isNotBlank() || address.contactPhone.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Contact: ${address.recipientName} • ${address.contactPhone}", fontSize = 11.sp, color = MedicalTealDark)
                        }

                        if (!address.isDefault) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { onSetDefault(address) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Définir comme adresse principale", fontSize = 11.sp, color = MedicalTealPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun PharmacistsTab(
    pharmacists: List<PharmacistRegistrationEntity>,
    onRegisterNew: () -> Unit,
    onDeletePharmacist: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Officines & Pharmaciens Titulaires",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Ordre National des Pharmaciens du Sénégal",
                        fontSize = 11.sp,
                        color = TextSecondaryMuted
                    )
                }
                Button(
                    onClick = onRegisterNew,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Inscrire", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (pharmacists.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            imageVector = Icons.Default.LocalPharmacy,
                            contentDescription = null,
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Espace Professionnel Officine", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vous êtes pharmacien titulaire au Sénégal ? Enregistrez votre officine avec votre N° de Licence pour délivrer des médicaments certifiés et recevoir des ordonnances scannées.",
                            fontSize = 12.sp,
                            color = TextSecondaryMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onRegisterNew,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Inscrire mon Officine")
                        }
                    }
                }
            }
        } else {
            items(pharmacists) { pharm ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFECFDF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBadgeGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(pharm.pharmacyName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                                    Text("Dr. ${pharm.fullName}", fontSize = 12.sp, color = MedicalTealPrimary, fontWeight = FontWeight.Medium)
                                }
                            }
                            IconButton(onClick = { onDeletePharmacist(pharm.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("N° Licence: ${pharm.licenseNumber}", fontSize = 11.sp, color = TextSecondaryMuted)
                                Text("N° Ordre: ${pharm.orderRegistrationNumber}", fontSize = 11.sp, color = TextSecondaryMuted)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${pharm.district}, ${pharm.city}", fontSize = 11.sp, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
                                Text(pharm.region, fontSize = 11.sp, color = TextSecondaryMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFECFDF5))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerifiedBadgeGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Statut : Agrément Validé par SMS • Prêt à valider ordonnances", fontSize = 11.sp, color = Color(0xFF065F46), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun CouriersTab(
    couriers: List<DeliveryCourierEntity>,
    onAddNew: () -> Unit,
    onEdit: (DeliveryCourierEntity) -> Unit,
    onDelete: (DeliveryCourierEntity) -> Unit,
    onCall: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Flotte de Livreurs Médicaux",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Motos isothermes & distribution certifiée",
                        fontSize = 11.sp,
                        color = TextSecondaryMuted
                    )
                }
                Button(
                    onClick = onAddNew,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (couriers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = null,
                            tint = DutyPharmacyOrange,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Gestion des Livreurs", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enregistrez vos coursiers avec leur CNI, coordonnées et type de véhicule pour leur assigner des livraisons de médicaments avec suivi temps réel.",
                            fontSize = 12.sp,
                            color = TextSecondaryMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onAddNew,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                        ) {
                            Text("Enregistrer un premier Livreur")
                        }
                    }
                }
            }
        } else {
            items(couriers) { courier ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MedicalTealLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(courier.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                                    Text(courier.vehicleType, fontSize = 11.sp, color = TextSecondaryMuted)
                                }
                            }

                            Row {
                                IconButton(onClick = { onCall(courier.phoneNumber) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Call, contentDescription = "Appeler", tint = VerifiedBadgeGreen, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onEdit(courier) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = SafeBlueSecondary, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onDelete(courier) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Tél: ${courier.phoneNumber}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                                Text("CNI / NIN: ${courier.nationalIdCardNumber}", fontSize = 11.sp, color = TextSecondaryMuted)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Zone: ${courier.assignedZone}", fontSize = 11.sp, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
                                Text(courier.region, fontSize = 11.sp, color = TextSecondaryMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val (statusText, statusBg, statusColor) = when (courier.status) {
                            "DISPONIBLE" -> Triple("🟢 DISPONIBLE POUR COURSES", Color(0xFFECFDF5), Color(0xFF065F46))
                            "EN_LIVRAISON" -> Triple("🟡 EN COURS DE LIVRAISON", Color(0xFFFEF3C7), Color(0xFF92400E))
                            else -> Triple("⚪ HORS LIGNE", Color(0xFFF1F5F9), Color(0xFF64748B))
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusBg)
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(statusText, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun InvoicesTab(
    orders: List<OrderEntity>,
    onViewInvoice: (OrderEntity) -> Unit,
    onPrintInvoice: (OrderEntity) -> Unit,
    onSendSms: (OrderEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Factures Électroniques Certifiées",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Impression A4 & Envoi direct par SMS sur téléphone",
                    fontSize = 11.sp,
                    color = TextSecondaryMuted
                )
            }
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Aucune facture pour le moment", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vos factures de commandes confirmées apparaîtront ici automatiquement avec possibilité d'impression et de réception SMS.",
                            fontSize = 12.sp,
                            color = TextSecondaryMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(orders) { order ->
                val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date(order.orderTimestamp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MedicalTealDark)
                                Text(dateStr, fontSize = 11.sp, color = TextSecondaryMuted)
                            }
                            Text(
                                "${order.totalFcfa} FCFA",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = MedicalTealPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pharmacie: ${order.pharmacyName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Articles: ${order.itemsSummary}",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onViewInvoice(order) },
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Voir Reçu", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onPrintInvoice(order) },
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalTealDark)
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Imprimer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onSendSms(order) },
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                            ) {
                                Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
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
    var recipient by remember { mutableStateOf(address?.recipientName ?: "") }
    var phone by remember { mutableStateOf(address?.contactPhone ?: "") }
    var fullAddress by remember { mutableStateOf(address?.fullAddress ?: "") }
    var neighborhood by remember { mutableStateOf(address?.neighborhood ?: "Sacré-Cœur") }
    var city by remember { mutableStateOf(address?.city ?: "Dakar") }
    var region by remember { mutableStateOf(address?.region ?: "Dakar") }
    var instructions by remember { mutableStateOf(address?.courierInstructions ?: "") }
    var isDefault by remember { mutableStateOf(address?.isDefault ?: false) }

    var regionExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("address_edit_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (address == null) "Ajouter une Adresse" else "Modifier l'Adresse",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nom du lieu (ex: Domicile, Bureau)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_title"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Nom du destinataire") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_recipient"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone pour le livreur") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_phone"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = regionExpanded,
                    onExpandedChange = { regionExpanded = !regionExpanded }
                ) {
                    OutlinedTextField(
                        value = region,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Région") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = regionExpanded,
                        onDismissRequest = { regionExpanded = false }
                    ) {
                        InitialData.senegalRegions.forEach { reg ->
                            DropdownMenuItem(
                                text = { Text(reg) },
                                onClick = {
                                    region = reg
                                    regionExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ville") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Quartier") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text("Adresse complète (Rue, Repère, N°)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_full"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions coursier") },
                    modifier = Modifier.fillMaxWidth().testTag("input_addr_instructions"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF9F9F9))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Adresse principale", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = MedicalTealPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val computed = if (fullAddress.isBlank()) "$neighborhood, $city ($region)" else fullAddress
                            onSave(title, recipient, phone, computed, neighborhood, city, region, instructions, isDefault)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_save_addr_dialog")
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
