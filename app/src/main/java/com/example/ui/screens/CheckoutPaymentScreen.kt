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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.InitialData
import com.example.data.model.CartItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PaymentMethod
import com.example.ui.components.PaymentMethodSelector
import com.example.ui.theme.EscrowGreenColor
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.viewmodel.PaymentProcessState
import com.example.ui.viewmodel.PharmaViewModel

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutPaymentScreen(
    viewModel: PharmaViewModel,
    onBack: () -> Unit,
    onPaymentSuccess: (OrderEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()
    val userAddress by viewModel.userDeliveryAddress.collectAsStateWithLifecycle()
    val savedAddresses by viewModel.deliveryAddresses.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userPhone by viewModel.userPhone.collectAsStateWithLifecycle()

    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.WAVE) }
    var mobileOrCardNumber by remember(userPhone) { mutableStateOf(userPhone) }
    var otpPinCode by remember { mutableStateOf("") }

    var isEditingAddress by remember { mutableStateOf(false) }
    var addressInput by remember(userAddress) { mutableStateOf(userAddress) }
    var selectedRegion by remember { mutableStateOf("Dakar") }
    var selectedCity by remember { mutableStateOf("Dakar") }
    var selectedNeighborhood by remember { mutableStateOf("Sacré-Cœur / Keur Gorgui") }

    val defaultPharmacy = InitialData.pharmacies.first()
    val subtotal = cartItems.sumOf { it.priceFcfa * it.quantity }
    val deliveryFee = defaultPharmacy.deliveryFeeFcfa
    val total = subtotal + deliveryFee

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
                    CircularProgressIndicator(
                        color = MedicalTealPrimary,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(52.dp)
                    )
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
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = VerifiedBadgeGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Protocole bancaire SSL 256-bit crypté", fontSize = 10.sp, color = VerifiedBadgeGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("checkout_payment_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Paiement Sécurisé en Ligne", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
                            Text("Montant net débité:", fontSize = 11.sp, color = TextSecondaryMuted)
                            Text(
                                text = "$total FCFA",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MedicalTealPrimary
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.processOnlinePayment(
                                    items = cartItems,
                                    pharmacy = defaultPharmacy,
                                    deliveryAddress = if (addressInput.isNotBlank()) addressInput else userAddress,
                                    patientName = userName,
                                    patientPhone = if (mobileOrCardNumber.isNotBlank()) mobileOrCardNumber else userPhone,
                                    paymentMethod = selectedPaymentMethod,
                                    mobileNumberOrCard = mobileOrCardNumber,
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
                                text = "Confirmer & Payer",
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
            // Order Recap Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Récapitulatif de la commande",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "${cartItems.size} article(s)",
                            fontSize = 12.sp,
                            color = TextSecondaryMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.medicineName} x${item.quantity}",
                                fontSize = 12.sp,
                                color = TextPrimaryDark,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${item.priceFcfa * item.quantity} FCFA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MedicalTealDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFEFF4F2))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pharmacie : ${defaultPharmacy.name}", fontSize = 11.sp, color = TextSecondaryMuted)
                        Text("Frais livraison : $deliveryFee FCFA", fontSize = 11.sp, color = TextSecondaryMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Delivery Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🇸🇳", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Destination & Destinataire",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
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
                                text = if (isEditingAddress) "Fermer" else "Modifier région",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalTealPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF7FAF9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = userAddress,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimaryDark
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("$userName • $userPhone", fontSize = 11.sp, color = TextSecondaryMuted)
                            }
                        }
                    }

                    // Saved Addresses Chips
                    if (savedAddresses.size > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Changer rapidement de lieu de livraison :",
                            fontSize = 10.sp,
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

                    // Expandable region picker for 14 regions of Senegal
                    AnimatedVisibility(visible = isEditingAddress) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "14 Régions du Sénégal :",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))

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

                            OutlinedTextField(
                                value = addressInput,
                                onValueChange = { addressInput = it },
                                label = { Text("Adresse de livraison") },
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
                                Text("Valider l'adresse pour cette commande", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods Header & Selector
            Text(
                text = "Choisissez votre moyen de paiement en ligne",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Text(
                text = "Paiement direct sans vous déplacer avec accusé de réception",
                fontSize = 12.sp,
                color = TextSecondaryMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            PaymentMethodSelector(
                selectedMethod = selectedPaymentMethod,
                onMethodSelected = { selectedPaymentMethod = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Input Box (Mobile Money Phone Number / Card Details)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = when (selectedPaymentMethod) {
                            PaymentMethod.ORANGE_MONEY -> "Numéro Orange Money du compte à débiter"
                            PaymentMethod.WAVE -> "Numéro Wave pour notification de paiement"
                            PaymentMethod.MTN_MOMO -> "Numéro MTN Mobile Money"
                            PaymentMethod.CREDIT_CARD -> "Numéro de Carte Bancaire (16 chiffres)"
                            PaymentMethod.ESCROW_WALLET -> "Compte de Séquestre Santé Sécurisé"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = mobileOrCardNumber,
                        onValueChange = { mobileOrCardNumber = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_account_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MedicalTealPrimary),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = {
                            Icon(
                                imageVector = if (selectedPaymentMethod == PaymentMethod.CREDIT_CARD) Icons.Default.CreditCard else Icons.Default.Phone,
                                contentDescription = null,
                                tint = MedicalTealPrimary
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Reassurance Escrow info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F8F6))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MedicalTealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Protection client : Un code PIN secret vous est remis pour déverrouiller la remise du colis par le livreur.",
                            fontSize = 11.sp,
                            color = MedicalTealDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
