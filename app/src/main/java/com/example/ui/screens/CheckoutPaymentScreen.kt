package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.InitialData
import com.example.data.model.DeliveryAddressEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PaymentMethod
import com.example.ui.components.DeliveryAddressDialog
import com.example.ui.components.PaymentMethodSelector
import com.example.ui.theme.EscrowGreenColor
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.MtnMomoYellow
import com.example.ui.theme.OrangeMoneyColor
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.TextOnWhiteMuted
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.theme.VisaBlueColor
import com.example.ui.theme.WaveBlueColor
import com.example.ui.viewmodel.PaymentProcessState
import com.example.ui.viewmodel.PharmaViewModel
import com.example.util.MobilePaymentLauncher
import com.example.util.PaymentLaunchResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class DeliveryOptionType(val title: String, val subtitle: String, val feeFcfa: Int, val isPickup: Boolean) {
    EXPRESS_DELIVERY("Livraison Express Santé (30-45 min)", "Coursier dédié en sac isotherme certifié 2-8°C", 1500, false),
    SCHEDULED_DELIVERY("Livraison Programmée", "Choisissez un créneau de livraison adapté", 1500, false),
    CLICK_AND_COLLECT("Retrait en Pharmacie (Click & Collect)", "Disponible en 15 min à l'officine • 0 FCFA", 0, true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutPaymentScreen(
    viewModel: PharmaViewModel,
    onBack: () -> Unit,
    onPaymentSuccess: (OrderEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()
    val userAddress by viewModel.userDeliveryAddress.collectAsStateWithLifecycle()
    val savedAddresses by viewModel.deliveryAddresses.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userPhone by viewModel.userPhone.collectAsStateWithLifecycle()

    // Pharmacy selection
    val defaultPharmacy = InitialData.pharmacies.first()

    // Delivery selection
    var selectedDeliveryOption by remember { mutableStateOf(DeliveryOptionType.EXPRESS_DELIVERY) }
    var selectedTimeSlot by remember { mutableStateOf("Aujourd'hui • Dans 30-45 min") }

    // Address Management State
    var showAddressDialog by remember { mutableStateOf(false) }
    var addressToEdit by remember { mutableStateOf<DeliveryAddressEntity?>(null) }
    var selectedAddressEntity by remember(savedAddresses, userAddress) {
        mutableStateOf(
            savedAddresses.find { userAddress.contains(it.neighborhood) || userAddress == it.fullAddress }
                ?: savedAddresses.find { it.isDefault }
                ?: savedAddresses.firstOrNull()
        )
    }

    // Payment Selection & Form Details
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.WAVE) }
    var mobileOrCardNumber by remember(userPhone) { mutableStateOf(userPhone) }

    // Operator App Direct Open & Code Validation States
    var mobileValidationMode by remember { mutableStateOf("DIRECT_APP") } // "DIRECT_APP" ou "BY_CODE"
    var authCodeInput by remember { mutableStateOf("") }
    var autoLaunchAppOnSelect by remember { mutableStateOf(true) }
    var hasAppBeenLaunched by remember { mutableStateOf(false) }
    var lastLaunchedAppName by remember { mutableStateOf("Wave") }
    var launchStatusMessage by remember { mutableStateOf<String?>(null) }
    
    // Credit Card Form Specifics (Empty fields for user manual input)
    var cardHolderName by remember { mutableStateOf("") }
    var cardNumberFormatted by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var isCvvVisible by remember { mutableStateOf(false) }

    // Promo Code State
    var promoCodeInput by remember { mutableStateOf("") }
    var appliedDiscountPercent by remember { mutableIntStateOf(0) }
    var promoMessage by remember { mutableStateOf<String?>(null) }
    var isOrderSummaryExpanded by remember { mutableStateOf(true) }

    // Calculation
    val subtotal = cartItems.sumOf { it.priceFcfa * it.quantity }
    val deliveryFee = if (selectedDeliveryOption.isPickup) 0 else selectedDeliveryOption.feeFcfa
    val discountAmount = if (appliedDiscountPercent > 0) (subtotal * appliedDiscountPercent / 100) else 0
    val total = (subtotal + deliveryFee - discountAmount).coerceAtLeast(0)

    // Current delivery address string
    val currentDeliveryAddressString = when {
        selectedDeliveryOption.isPickup -> "Retrait au comptoir : ${defaultPharmacy.name}, ${defaultPharmacy.address}"
        selectedAddressEntity != null -> "${selectedAddressEntity?.fullAddress}, ${selectedAddressEntity?.neighborhood}, ${selectedAddressEntity?.city} (${selectedAddressEntity?.region})"
        userAddress.isNotBlank() -> userAddress
        else -> "Résidence Keur Gorgui, Sacré-Cœur, Dakar (Dakar)"
    }

    val currentRecipientName = selectedAddressEntity?.recipientName?.takeIf { it.isNotBlank() } ?: userName.ifBlank { "Client Destinataire" }
    val currentRecipientPhone = selectedAddressEntity?.contactPhone?.takeIf { it.isNotBlank() } ?: userPhone

    // Payment Processing Modal
    if (paymentState is PaymentProcessState.Processing) {
        val state = paymentState as PaymentProcessState.Processing
        Dialog(onDismissRequest = { /* non-dismissible during payment */ }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("payment_processing_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(MedicalTealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MedicalTealPrimary,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Sécurisation du Paiement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.stepMessage,
                        fontSize = 12.sp,
                        color = TextSecondaryMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = VerifiedBadgeGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Protocole bancaire SSL 256-bit crypté • 0% Fraude", fontSize = 11.sp, color = VerifiedBadgeGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Delivery Address Add/Edit Dialog
    if (showAddressDialog) {
        DeliveryAddressDialog(
            address = addressToEdit,
            onDismiss = {
                showAddressDialog = false
                addressToEdit = null
            },
            onSave = { title, recipient, phone, fullAddress, neighborhood, city, region, instructions, isDefault ->
                viewModel.saveDeliveryAddress(
                    id = addressToEdit?.id,
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
                        val computed = "$fullAddress, $neighborhood, $city ($region)"
                        viewModel.userDeliveryAddress.value = computed
                        showAddressDialog = false
                        addressToEdit = null
                    }
                )
            }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("checkout_payment_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Paiement Sécurisé & Livraison", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Pharmacie Certifiée • 14 Régions du Sénégal", fontSize = 11.sp, color = TextSecondaryMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MedicalTealLight)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SSL 256-Bit", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MedicalTealDark)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = TextPrimaryDark
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Total net débité :", fontSize = 11.sp, color = TextSecondaryMuted)
                                if (appliedDiscountPercent > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFDC2626))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("-$appliedDiscountPercent%", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = "$total FCFA",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MedicalTealPrimary
                            )
                        }

                        Button(
                            onClick = {
                                val paymentParam = if (selectedPaymentMethod == PaymentMethod.CREDIT_CARD) {
                                    "CARTE: $cardNumberFormatted"
                                } else {
                                    mobileOrCardNumber
                                }

                                // Si mode application directe et l'app n'a pas encore été lancée, tenter de la lancer
                                if (mobileValidationMode == "DIRECT_APP" && 
                                    (selectedPaymentMethod == PaymentMethod.WAVE || 
                                     selectedPaymentMethod == PaymentMethod.ORANGE_MONEY || 
                                     selectedPaymentMethod == PaymentMethod.MTN_MOMO)) {
                                    if (!hasAppBeenLaunched && MobilePaymentLauncher.isOperatorAppInstalled(context, selectedPaymentMethod)) {
                                        MobilePaymentLauncher.launchOperatorApp(
                                            context = context,
                                            method = selectedPaymentMethod,
                                            amountFcfa = total,
                                            orderRef = "CMD-${System.currentTimeMillis() % 100000}",
                                            clientPhone = mobileOrCardNumber,
                                            notifyUser = true
                                        )
                                    }
                                }

                                viewModel.processOnlinePayment(
                                    items = cartItems,
                                    pharmacy = defaultPharmacy,
                                    deliveryAddress = currentDeliveryAddressString,
                                    patientName = currentRecipientName,
                                    patientPhone = currentRecipientPhone,
                                    paymentMethod = selectedPaymentMethod,
                                    mobileNumberOrCard = paymentParam,
                                    validationMode = mobileValidationMode,
                                    authCode = authCodeInput,
                                    onSuccess = { newOrder ->
                                        val paymentLinkUrl = when (selectedPaymentMethod) {
                                            PaymentMethod.WAVE -> "https://pay.wave.com/m/pharmadirect_sn?amount=$total&ref=${newOrder.orderNumber}"
                                            PaymentMethod.ORANGE_MONEY -> "https://pay.orange-money.sn/checkout?id=PHARMADIRECT&amt=$total&order=${newOrder.orderNumber}"
                                            PaymentMethod.MTN_MOMO -> "https://pay.mtn.com/momo/checkout?recipient=PHARMADIRECT&amt=$total&ref=${newOrder.orderNumber}"
                                            PaymentMethod.CREDIT_CARD -> "https://pay.pharmadirect.sn/card/checkout?amt=$total&ref=${newOrder.orderNumber}"
                                            else -> "https://pay.pharmadirect.sn/checkout?amt=$total&order=${newOrder.orderNumber}"
                                        }
                                        viewModel.triggerPaymentLinkSms(newOrder, paymentLinkUrl)
                                        viewModel.triggerInvoiceSms(newOrder)
                                        onPaymentSuccess(newOrder)
                                    }
                                )
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("confirm_and_pay_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                            enabled = cartItems.isNotEmpty() && paymentState !is PaymentProcessState.Processing
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (mobileValidationMode == "DIRECT_APP" && hasAppBeenLaunched) "Valider le Paiement" else "Confirmer & Payer",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // STEP 1: Delivery Mode & Address Management
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MedicalTealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mode & Lieu de Livraison",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Delivery Option Tabs (Express, Scheduled, Pickup)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DeliveryOptionType.values().forEach { option ->
                    val isSelected = selectedDeliveryOption == option
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedDeliveryOption = option },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MedicalTealLight.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MedicalTealPrimary else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MedicalTealPrimary else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (option) {
                                        DeliveryOptionType.EXPRESS_DELIVERY -> Icons.Default.LocalShipping
                                        DeliveryOptionType.SCHEDULED_DELIVERY -> Icons.Default.AccessTime
                                        DeliveryOptionType.CLICK_AND_COLLECT -> Icons.Default.Store
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MedicalTealPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = option.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MedicalTealDark else TextPrimaryDark
                                    )
                                    Text(
                                        text = if (option.feeFcfa == 0) "GRATUIT" else "${option.feeFcfa} FCFA",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (option.feeFcfa == 0) VerifiedBadgeGreen else MedicalTealPrimary
                                    )
                                }
                                Text(
                                    text = option.subtitle,
                                    fontSize = 11.sp,
                                    color = if (isSelected) TextOnWhiteSecondary else TextSecondaryMuted
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedDeliveryOption = option },
                                colors = RadioButtonDefaults.colors(selectedColor = MedicalTealPrimary)
                            )
                        }
                    }
                }
            }

            // Scheduled slot selector
            if (selectedDeliveryOption == DeliveryOptionType.SCHEDULED_DELIVERY) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Créneaux disponibles :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextOnWhitePrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val slots = listOf(
                                "Aujourd'hui • 14h - 16h",
                                "Aujourd'hui • 18h - 20h",
                                "Demain • 09h - 11h",
                                "Demain • 14h - 16h"
                            )
                            items(slots) { slot ->
                                val isSel = selectedTimeSlot == slot
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedTimeSlot = slot },
                                    label = { Text(slot, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MedicalTealPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // DELIVERY ADDRESS MANAGEMENT CARD (Hidden if Click & Collect)
            if (!selectedDeliveryOption.isPickup) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Adresse de Réception",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            }

                            Button(
                                onClick = {
                                    addressToEdit = null
                                    showAddressDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Nouvelle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selected Address Detailed Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAF9),
                            border = BorderStroke(1.dp, MedicalTealPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = selectedAddressEntity?.title ?: "Domicile",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextOnWhitePrimary
                                        )
                                        if (selectedAddressEntity?.isDefault == true) {
                                            Spacer(modifier = Modifier.width(6.dp))
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
                                        TextButton(
                                            onClick = {
                                                addressToEdit = selectedAddressEntity
                                                showAddressDialog = true
                                            },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = MedicalTealPrimary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Modifier", fontSize = 11.sp, color = MedicalTealPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = currentDeliveryAddressString,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextOnWhitePrimary,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = TextOnWhiteSecondary, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$currentRecipientName • $currentRecipientPhone",
                                        fontSize = 11.sp,
                                        color = TextOnWhiteSecondary
                                    )
                                }

                                if (!selectedAddressEntity?.courierInstructions.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = SafeBlueSecondary, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Note : ${selectedAddressEntity?.courierInstructions}",
                                            fontSize = 10.5.sp,
                                            color = SafeBlueSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Quick Switcher among Saved Addresses
                        if (savedAddresses.size > 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Changer rapidement de lieu :", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(savedAddresses) { addr ->
                                    val isCurrent = selectedAddressEntity?.id == addr.id
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCurrent) MedicalTealLight else Color(0xFFF1F5F9),
                                        border = if (isCurrent) BorderStroke(1.dp, MedicalTealPrimary) else null,
                                        modifier = Modifier.clickable {
                                            selectedAddressEntity = addr
                                            val full = "${addr.fullAddress}, ${addr.neighborhood}, ${addr.city} (${addr.region})"
                                            viewModel.userDeliveryAddress.value = full
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isCurrent) Icons.Default.CheckCircle else Icons.Default.Home,
                                                contentDescription = null,
                                                tint = if (isCurrent) MedicalTealPrimary else TextOnWhiteMuted,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${addr.title} • ${addr.neighborhood}",
                                                fontSize = 11.sp,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isCurrent) MedicalTealDark else TextOnWhitePrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 2: Payment Method Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MedicalTealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("2", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Moyen de Paiement Sécurisé",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFECFDF5))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("0% Frais", color = VerifiedBadgeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            PaymentMethodSelector(
                selectedMethod = selectedPaymentMethod,
                onMethodSelected = { method ->
                    selectedPaymentMethod = method
                    authCodeInput = ""
                    // Lorsqu'un utilisateur choisit Wave ou Orange Money, le paiement s'ouvre directement dans l'application installée
                    if (method == PaymentMethod.WAVE || method == PaymentMethod.ORANGE_MONEY || method == PaymentMethod.MTN_MOMO) {
                        val isInstalled = MobilePaymentLauncher.isOperatorAppInstalled(context, method)
                        if (autoLaunchAppOnSelect && isInstalled) {
                            val launchRes = MobilePaymentLauncher.launchOperatorApp(
                                context = context,
                                method = method,
                                amountFcfa = total,
                                orderRef = "CMD-${System.currentTimeMillis() % 100000}",
                                clientPhone = mobileOrCardNumber,
                                notifyUser = true
                            )
                            if (launchRes is PaymentLaunchResult.Success) {
                                hasAppBeenLaunched = true
                                lastLaunchedAppName = launchRes.appName
                                launchStatusMessage = launchRes.message
                            }
                        }
                    }
                }
            )

            // Bannière interactive lorsque l'application de l'opérateur a été ouverte
            if (hasAppBeenLaunched && (selectedPaymentMethod == PaymentMethod.WAVE || selectedPaymentMethod == PaymentMethod.ORANGE_MONEY || selectedPaymentMethod == PaymentMethod.MTN_MOMO)) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("app_launched_status_banner"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPaymentMethod == PaymentMethod.WAVE) WaveBlueColor.copy(alpha = 0.12f)
                        else OrangeMoneyColor.copy(alpha = 0.12f)
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (selectedPaymentMethod == PaymentMethod.WAVE) WaveBlueColor else OrangeMoneyColor
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = if (selectedPaymentMethod == PaymentMethod.WAVE) WaveBlueColor else OrangeMoneyColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Application $lastLaunchedAppName ouverte",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = TextPrimaryDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "La transaction de $total FCFA a été transmise à $lastLaunchedAppName. Confirmez directement dans l'application ou saisissez votre code d'autorisation.",
                            fontSize = 11.5.sp,
                            color = TextSecondaryMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.processOnlinePayment(
                                        items = cartItems,
                                        pharmacy = defaultPharmacy,
                                        deliveryAddress = currentDeliveryAddressString,
                                        patientName = currentRecipientName,
                                        patientPhone = currentRecipientPhone,
                                        paymentMethod = selectedPaymentMethod,
                                        mobileNumberOrCard = mobileOrCardNumber,
                                        validationMode = "DIRECT_APP",
                                        authCode = authCodeInput,
                                        onSuccess = { newOrder ->
                                            val paymentLinkUrl = when (selectedPaymentMethod) {
                                                PaymentMethod.WAVE -> "https://pay.wave.com/m/pharmadirect_sn?amount=$total&ref=${newOrder.orderNumber}"
                                                PaymentMethod.ORANGE_MONEY -> "https://pay.orange-money.sn/checkout?id=PHARMADIRECT&amt=$total&order=${newOrder.orderNumber}"
                                                else -> "https://pay.pharmadirect.sn/checkout?amt=$total&order=${newOrder.orderNumber}"
                                            }
                                            viewModel.triggerPaymentLinkSms(newOrder, paymentLinkUrl)
                                            viewModel.triggerInvoiceSms(newOrder)
                                            onPaymentSuccess(newOrder)
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f).height(40.dp).testTag("direct_app_validated_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("J'ai validé dans l'app", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    MobilePaymentLauncher.launchOperatorApp(
                                        context = context,
                                        method = selectedPaymentMethod,
                                        amountFcfa = total,
                                        orderRef = "CMD-${System.currentTimeMillis() % 100000}",
                                        clientPhone = mobileOrCardNumber,
                                        notifyUser = true
                                    )
                                },
                                modifier = Modifier.height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp), tint = TextPrimaryDark)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Relancer", fontSize = 11.5.sp, color = TextPrimaryDark)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Payment Form Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (selectedPaymentMethod) {
                        PaymentMethod.WAVE -> {
                            val isWaveInstalled = remember(context) {
                                MobilePaymentLauncher.isOperatorAppInstalled(context, PaymentMethod.WAVE)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(WaveBlueColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("W", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Wave Mobile Money", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextPrimaryDark)
                                        Text(
                                            if (isWaveInstalled) "Application Wave prête sur votre smartphone" else "Validation rapide 0% de frais",
                                            fontSize = 11.sp,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                }

                                if (isWaveInstalled) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFE0F2FE),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WaveBlueColor, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("App Installée", color = WaveBlueColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Onglets de choix du mode de validation : Dans l'App vs Par Code
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { mobileValidationMode = "DIRECT_APP" }
                                            .testTag("mode_direct_app_wave"),
                                        color = if (mobileValidationMode == "DIRECT_APP") Color.White else Color.Transparent,
                                        shadowElevation = if (mobileValidationMode == "DIRECT_APP") 2.dp else 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.PhoneAndroid,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (mobileValidationMode == "DIRECT_APP") WaveBlueColor else TextSecondaryMuted
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Dans l'App Wave",
                                                fontWeight = if (mobileValidationMode == "DIRECT_APP") FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (mobileValidationMode == "DIRECT_APP") TextPrimaryDark else TextSecondaryMuted
                                            )
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { mobileValidationMode = "BY_CODE" }
                                            .testTag("mode_by_code_wave"),
                                        color = if (mobileValidationMode == "BY_CODE") Color.White else Color.Transparent,
                                        shadowElevation = if (mobileValidationMode == "BY_CODE") 2.dp else 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Key,
                                                contentDescription = null,
                                                modifier = Modifier.size(15.dp),
                                                tint = if (mobileValidationMode == "BY_CODE") WaveBlueColor else TextSecondaryMuted
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Saisir un code",
                                                fontWeight = if (mobileValidationMode == "BY_CODE") FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (mobileValidationMode == "BY_CODE") TextPrimaryDark else TextSecondaryMuted
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (mobileValidationMode == "DIRECT_APP") {
                                // MODE 1 : Validation directe dans l'application Wave
                                if (isWaveInstalled) {
                                    Button(
                                        onClick = {
                                            val res = MobilePaymentLauncher.launchOperatorApp(
                                                context = context,
                                                method = PaymentMethod.WAVE,
                                                amountFcfa = total,
                                                orderRef = "CMD-${System.currentTimeMillis() % 100000}",
                                                clientPhone = mobileOrCardNumber,
                                                notifyUser = true
                                            )
                                            if (res is PaymentLaunchResult.Success) {
                                                hasAppBeenLaunched = true
                                                lastLaunchedAppName = res.appName
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("open_wave_app_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = WaveBlueColor)
                                    ) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Ouvrir l'application Wave ($total FCFA)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(WaveBlueColor.copy(alpha = 0.08f))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = WaveBlueColor, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "1. L'application Wave s'ouvre directement.\n2. Validez le débit avec votre empreinte ou code.\n3. Revenez ici : votre commande est confirmée !",
                                            fontSize = 11.sp,
                                            color = TextPrimaryDark,
                                            lineHeight = 16.sp
                                        )
                                    }
                                } else {
                                    // Application non installée
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFFFBEB))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = "L'application Wave n'est pas installée sur ce téléphone.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF92400E)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(
                                                onClick = { MobilePaymentLauncher.openPlayStore(context, "com.wave.personal") },
                                                modifier = Modifier.weight(1f).height(36.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp)
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Installer Wave", fontSize = 11.sp)
                                            }
                                            Button(
                                                onClick = { mobileValidationMode = "BY_CODE" },
                                                modifier = Modifier.weight(1f).height(36.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = WaveBlueColor),
                                                contentPadding = PaddingValues(horizontal = 6.dp)
                                            ) {
                                                Text("Payer par code", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // MODE 2 : Validation par code secret / OTP
                                OutlinedTextField(
                                    value = mobileOrCardNumber,
                                    onValueChange = { mobileOrCardNumber = it },
                                    label = { Text("Numéro Wave") },
                                    placeholder = { Text("Ex: 77 123 45 67") },
                                    modifier = Modifier.fillMaxWidth().testTag("payment_account_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WaveBlueColor),
                                    leadingIcon = {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = WaveBlueColor)
                                    }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = authCodeInput,
                                    onValueChange = { if (it.length <= 6) authCodeInput = it },
                                    label = { Text("Code secret / Code de confirmation Wave") },
                                    placeholder = { Text("Code à 4 ou 6 chiffres") },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_code_wave_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WaveBlueColor),
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = WaveBlueColor)
                                    }
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Saisissez votre code secret Wave pour valider instantanément la transaction.",
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                            }
                        }

                        PaymentMethod.ORANGE_MONEY -> {
                            val isOmInstalled = remember(context) {
                                MobilePaymentLauncher.isOperatorAppInstalled(context, PaymentMethod.ORANGE_MONEY)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(OrangeMoneyColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("OM", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Orange Money Sénégal", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextPrimaryDark)
                                        Text(
                                            if (isOmInstalled) "Application Orange Money prête sur votre smartphone" else "Validation via App ou code #144#391#",
                                            fontSize = 11.sp,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                }

                                if (isOmInstalled) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFFEDD5),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OrangeMoneyColor, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("App Installée", color = OrangeMoneyColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Onglets de choix : Dans l'App vs Par Code
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { mobileValidationMode = "DIRECT_APP" }
                                            .testTag("mode_direct_app_om"),
                                        color = if (mobileValidationMode == "DIRECT_APP") Color.White else Color.Transparent,
                                        shadowElevation = if (mobileValidationMode == "DIRECT_APP") 2.dp else 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.PhoneAndroid,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (mobileValidationMode == "DIRECT_APP") OrangeMoneyColor else TextSecondaryMuted
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Dans l'App OM",
                                                fontWeight = if (mobileValidationMode == "DIRECT_APP") FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (mobileValidationMode == "DIRECT_APP") TextPrimaryDark else TextSecondaryMuted
                                            )
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { mobileValidationMode = "BY_CODE" }
                                            .testTag("mode_by_code_om"),
                                        color = if (mobileValidationMode == "BY_CODE") Color.White else Color.Transparent,
                                        shadowElevation = if (mobileValidationMode == "BY_CODE") 2.dp else 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Dialpad,
                                                contentDescription = null,
                                                modifier = Modifier.size(15.dp),
                                                tint = if (mobileValidationMode == "BY_CODE") OrangeMoneyColor else TextSecondaryMuted
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Code #144#391#",
                                                fontWeight = if (mobileValidationMode == "BY_CODE") FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (mobileValidationMode == "BY_CODE") TextPrimaryDark else TextSecondaryMuted
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (mobileValidationMode == "DIRECT_APP") {
                                // MODE 1 : Validation directe dans Orange Money
                                if (isOmInstalled) {
                                    Button(
                                        onClick = {
                                            val res = MobilePaymentLauncher.launchOperatorApp(
                                                context = context,
                                                method = PaymentMethod.ORANGE_MONEY,
                                                amountFcfa = total,
                                                orderRef = "CMD-${System.currentTimeMillis() % 100000}",
                                                clientPhone = mobileOrCardNumber,
                                                notifyUser = true
                                            )
                                            if (res is PaymentLaunchResult.Success) {
                                                hasAppBeenLaunched = true
                                                lastLaunchedAppName = res.appName
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("open_om_app_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = OrangeMoneyColor)
                                    ) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Ouvrir Orange Money ($total FCFA)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(OrangeMoneyColor.copy(alpha = 0.08f))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = OrangeMoneyColor, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "1. L'application Orange Money s'ouvre automatiquement.\n2. Autorisez la transaction avec votre code secret.\n3. Revenez ici pour finaliser la commande.",
                                            fontSize = 11.sp,
                                            color = TextPrimaryDark,
                                            lineHeight = 16.sp
                                        )
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFFFBEB))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = "L'application Orange Money n'est pas installée sur ce téléphone.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF92400E)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(
                                                onClick = { MobilePaymentLauncher.openPlayStore(context, "com.orange.orangemoneysenegal") },
                                                modifier = Modifier.weight(1f).height(36.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp)
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Installer OM", fontSize = 11.sp)
                                            }
                                            Button(
                                                onClick = { mobileValidationMode = "BY_CODE" },
                                                modifier = Modifier.weight(1f).height(36.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = OrangeMoneyColor),
                                                contentPadding = PaddingValues(horizontal = 6.dp)
                                            ) {
                                                Text("Utiliser le code", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // MODE 2 : Validation par code d'autorisation #144#391#
                                OutlinedTextField(
                                    value = mobileOrCardNumber,
                                    onValueChange = { mobileOrCardNumber = it },
                                    label = { Text("Numéro Orange Money") },
                                    placeholder = { Text("Ex: 77 000 00 00") },
                                    modifier = Modifier.fillMaxWidth().testTag("payment_account_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangeMoneyColor),
                                    leadingIcon = {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = OrangeMoneyColor)
                                    }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Bouton 1-clic pour composer directement le code USSD #144#391#
                                OutlinedButton(
                                    onClick = {
                                        MobilePaymentLauncher.openUssdDialer(context, "#144#391#")
                                    },
                                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("dial_orange_ussd_button"),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, OrangeMoneyColor),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangeMoneyColor)
                                ) {
                                    Icon(Icons.Default.Dialpad, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Composer #144#391# pour générer le code", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = authCodeInput,
                                    onValueChange = { if (it.length <= 6) authCodeInput = it },
                                    label = { Text("Code d'autorisation temporaire (4 chiffres)") },
                                    placeholder = { Text("Ex: 1234") },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_code_om_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangeMoneyColor),
                                    leadingIcon = {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = OrangeMoneyColor)
                                    }
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ce code temporaire à usage unique garantit que votre code secret principal reste confidentiel.",
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                            }
                        }

                        PaymentMethod.MTN_MOMO -> {
                            val isMomoInstalled = remember(context) {
                                MobilePaymentLauncher.isOperatorAppInstalled(context, PaymentMethod.MTN_MOMO)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(MtnMomoYellow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("M", color = TextPrimaryDark, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("MTN Mobile Money", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextPrimaryDark)
                                        Text(
                                            if (isMomoInstalled) "App MTN MoMo disponible" else "Validation sécurisée par code secret OTP",
                                            fontSize = 11.sp,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                }

                                if (isMomoInstalled) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFEF3C7),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Text("App Installée", color = Color(0xFFB45309), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (isMomoInstalled) {
                                Button(
                                    onClick = {
                                        val res = MobilePaymentLauncher.launchOperatorApp(
                                            context = context,
                                            method = PaymentMethod.MTN_MOMO,
                                            amountFcfa = total,
                                            orderRef = "CMD-${System.currentTimeMillis() % 100000}",
                                            clientPhone = mobileOrCardNumber,
                                            notifyUser = true
                                        )
                                        if (res is PaymentLaunchResult.Success) {
                                            hasAppBeenLaunched = true
                                            lastLaunchedAppName = res.appName
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(46.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MtnMomoYellow)
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = TextPrimaryDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ouvrir l'application MoMo ($total FCFA)", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            OutlinedTextField(
                                value = mobileOrCardNumber,
                                onValueChange = { mobileOrCardNumber = it },
                                label = { Text("Numéro MTN MoMo") },
                                modifier = Modifier.fillMaxWidth().testTag("payment_account_input"),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MtnMomoYellow),
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = MtnMomoYellow)
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = authCodeInput,
                                onValueChange = { authCodeInput = it },
                                label = { Text("Code secret OTP MoMo") },
                                modifier = Modifier.fillMaxWidth().testTag("auth_code_momo_input"),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                visualTransformation = PasswordVisualTransformation(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MtnMomoYellow),
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MtnMomoYellow)
                                }
                            )
                        }

                        PaymentMethod.CREDIT_CARD -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = VisaBlueColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Carte Visa / Mastercard Sécurisée", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                                        Text("Protocole 3D-Secure & Chiffrement AES-256", fontSize = 10.5.sp, color = TextSecondaryMuted)
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = VisaBlueColor) {
                                        Text("VISA", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEB001B)) {
                                        Text("MC", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = cardHolderName,
                                onValueChange = { cardHolderName = it },
                                label = { Text("Nom sur la carte") },
                                placeholder = { Text("ex: Mamadou Diop", fontSize = 12.sp, color = TextOnWhiteMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VisaBlueColor)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = cardNumberFormatted,
                                onValueChange = { cardNumberFormatted = it },
                                label = { Text("Numéro de carte (16 chiffres)") },
                                placeholder = { Text("4532 0000 0000 0000", fontSize = 12.sp, color = TextOnWhiteMuted) },
                                modifier = Modifier.fillMaxWidth().testTag("payment_account_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VisaBlueColor),
                                leadingIcon = {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = VisaBlueColor)
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { cardExpiry = it },
                                    label = { Text("Expiration (MM/AA)") },
                                    placeholder = { Text("MM/AA", fontSize = 12.sp, color = TextOnWhiteMuted) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VisaBlueColor)
                                )

                                OutlinedTextField(
                                    value = cardCvv,
                                    onValueChange = { cardCvv = it },
                                    label = { Text("CVV (3 chiffres)") },
                                    placeholder = { Text("123", fontSize = 12.sp, color = TextOnWhiteMuted) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    visualTransformation = if (isCvvVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VisaBlueColor),
                                    trailingIcon = {
                                        IconButton(onClick = { isCvvVisible = !isCvvVisible }, modifier = Modifier.size(24.dp)) {
                                            Icon(
                                                imageVector = if (isCvvVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = if (isCvvVisible) VisaBlueColor else TextOnWhiteMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VisaBlueColor.copy(alpha = 0.08f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = VisaBlueColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Champs vierges pour votre sécurité bancaire • Aucune donnée de carte n'est pré-enregistrée",
                                    fontSize = 10.5.sp,
                                    color = VisaBlueColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        PaymentMethod.ESCROW_WALLET -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(EscrowGreenColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Garantie Séquestre Santé (100% Fiable)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                                    Text("Fonds protégés jusqu'à inspection du colis à la livraison", fontSize = 10.5.sp, color = TextSecondaryMuted)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = mobileOrCardNumber,
                                onValueChange = { mobileOrCardNumber = it },
                                label = { Text("Numéro Mobile pour déblocage des fonds") },
                                modifier = Modifier.fillMaxWidth().testTag("payment_account_input"),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EscrowGreenColor),
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = EscrowGreenColor)
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EscrowGreenColor.copy(alpha = 0.08f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EscrowGreenColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Un code PIN secret à 4 chiffres vous est délivré. Vous ne le transmettez au livreur qu'une fois vos médicaments vérifiés.",
                                    fontSize = 11.sp,
                                    color = EscrowGreenColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 3: Order Summary & Pharmaceutical Verification
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isOrderSummaryExpanded = !isOrderSummaryExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MedicalTealPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("3", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Récapitulatif & Pharmacie",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }

                        Icon(
                            imageVector = if (isOrderSummaryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSecondaryMuted
                        )
                    }

                    AnimatedVisibility(visible = isOrderSummaryExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            // Pharmacy info banner
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F8F6),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${defaultPharmacy.name} (${defaultPharmacy.district})",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalTealDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Item list
                            cartItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${item.medicineName} x${item.quantity}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimaryDark
                                        )
                                        Text(
                                            text = "${item.dosageForm} • ${item.dosageStrength}",
                                            fontSize = 10.5.sp,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                    Text(
                                        text = "${item.priceFcfa * item.quantity} FCFA",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalTealDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Promo Code Box
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = promoCodeInput,
                                    onValueChange = { promoCodeInput = it.uppercase() },
                                    label = { Text("Code Promo Santé") },
                                    placeholder = { Text("ex: SANTE2026") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    leadingIcon = { Icon(Icons.Default.Discount, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp)) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary)
                                )

                                Button(
                                    onClick = {
                                        val code = promoCodeInput.trim().uppercase()
                                        if (code == "SANTE2026" || code == "WAVE0" || code == "FIDELITE") {
                                            appliedDiscountPercent = 10
                                            promoMessage = "Code $code appliqué : -10% de réduction immédiate !"
                                        } else if (code.isNotBlank()) {
                                            appliedDiscountPercent = 0
                                            promoMessage = "Code invalide. Essayez SANTE2026"
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text("Appliquer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (promoMessage != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = promoMessage!!,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (appliedDiscountPercent > 0) VerifiedBadgeGreen else Color(0xFFDC2626)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Breakdown calculations
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Sous-total médicaments :", fontSize = 12.sp, color = TextSecondaryMuted)
                                Text("$subtotal FCFA", fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Frais de livraison :", fontSize = 12.sp, color = TextSecondaryMuted)
                                Text(
                                    if (deliveryFee == 0) "GRATUIT (0 FCFA)" else "$deliveryFee FCFA",
                                    fontSize = 12.sp,
                                    color = if (deliveryFee == 0) VerifiedBadgeGreen else TextPrimaryDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (discountAmount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Remise code promo (-$appliedDiscountPercent%) :", fontSize = 12.sp, color = Color(0xFFDC2626))
                                    Text("-$discountAmount FCFA", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Frais de transaction :", fontSize = 12.sp, color = TextSecondaryMuted)
                                Text("0 FCFA (Offerts)", fontSize = 12.sp, color = VerifiedBadgeGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trust & Medical Certification Badges
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE6F7F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Garantie Qualité & Chaîne du Froid",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnWhitePrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Préparation certifiée officinale • Sac isotherme 2-8°C",
                                fontSize = 10.sp,
                                color = TextOnWhiteSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge « 100% Authentique » with high-contrast adapted font color
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFDCFCE7),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color(0xFF0F5132),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% Authentique",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F5132)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
