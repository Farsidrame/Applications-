package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CourierChatMessage
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.components.CertifiedBadge
import com.example.ui.components.InvoiceDialog
import com.example.ui.components.LiveDeliveryMapCanvas
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.viewmodel.PharmaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    order: OrderEntity,
    viewModel: PharmaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(order.id) {
        viewModel.initLiveTracking(order)
    }

    val liveActiveOrder by viewModel.activeOrder.collectAsStateWithLifecycle()
    val displayOrder = if (liveActiveOrder?.id == order.id) (liveActiveOrder ?: order) else order
    val telemetry by viewModel.liveTelemetry.collectAsStateWithLifecycle()
    val liveEvents by viewModel.liveEventsLog.collectAsStateWithLifecycle()
    val chatMessages by viewModel.courierChatMessages.collectAsStateWithLifecycle()
    val isAutoSimulating by viewModel.isLiveSimulationRunning.collectAsStateWithLifecycle()

    var showInvoiceDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCallModal by remember { mutableStateOf(false) }
    var isChatExpanded by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }

    if (showInvoiceDialog) {
        InvoiceDialog(order = displayOrder, onDismiss = { showInvoiceDialog = false })
    }

    // Call Simulator Dialog
    if (showCallModal) {
        AlertDialog(
            onDismissRequest = { showCallModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(VerifiedBadgeGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Appel en cours...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(telemetry.courierName, fontSize = 12.sp, color = TextSecondaryMuted)
                    }
                }
            },
            text = {
                Column {
                    Text(
                        text = "Numéro sécurisé du livreur : ${telemetry.courierPhone}\n\n« Bonjour ! Je suis Mamadou Ndiaye, votre livreur de santé PharmaDirect. Je suis actuellement à ${telemetry.distanceRemainingMeters}m de votre adresse avec votre sac isotherme scellé. »",
                        fontSize = 13.sp,
                        color = TextPrimaryDark
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCallModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_end_simulated_call")
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Raccrocher", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Annuler cette commande ?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Le montant de ${displayOrder.totalFcfa} FCFA sera remboursé automatiquement sur votre compte ${displayOrder.paymentMethod}.\n\nConfirmez-vous l'annulation immédiate ?",
                    fontSize = 13.sp,
                    color = TextSecondaryMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelOrder(displayOrder.id)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("tracking_cancel_confirm_btn")
                ) {
                    Text("Oui, Annuler", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Non, retour", color = MedicalTealPrimary)
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer la commande ?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Voulez-vous supprimer définitivement la commande ${displayOrder.orderNumber} de votre historique ?",
                    fontSize = 13.sp,
                    color = TextSecondaryMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteOrder(displayOrder.id, onComplete = onBack)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("tracking_delete_confirm_btn")
                ) {
                    Text("Supprimer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler", color = TextSecondaryMuted)
                }
            }
        )
    }

    val currentStatus = try {
        OrderStatus.valueOf(displayOrder.status)
    } catch (e: Exception) {
        OrderStatus.PAID_CONFIRMED
    }
    val isCancelled = currentStatus == OrderStatus.CANCELLED
    val isDelivered = currentStatus == OrderStatus.DELIVERED
    val canCancel = !isCancelled && !isDelivered

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("order_tracking_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Suivi en Direct", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isCancelled) Color(0xFFD32F2F) else Color(0xFF00E676))
                            )
                        }
                        Text(order.orderNumber, fontSize = 12.sp, color = TextSecondaryMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openSmsInbox() },
                        modifier = Modifier.testTag("tracking_open_sms_inbox_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = "SMS Notifications",
                            tint = MedicalTealPrimary
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.testTag("tracking_top_delete_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Supprimer la commande",
                            tint = Color(0xFF9E9E9E)
                        )
                    }
                    IconButton(onClick = { showInvoiceDialog = true }, modifier = Modifier.testTag("open_invoice_button")) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = "Facture", tint = MedicalTealPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = TextPrimaryDark
                )
            )
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
            // Delivered SMS Notification Banner Card
            if (isDelivered) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("delivered_sms_notification_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(VerifiedBadgeGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = "SMS",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SMS de confirmation envoyé !",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "Un SMS de confirmation a été transmis à ${if (displayOrder.patientPhone.isNotBlank()) displayOrder.patientPhone else "votre numéro"}.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.triggerDeliverySms(displayOrder)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VerifiedBadgeGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_view_delivered_sms")
                        ) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Consulter le SMS de livraison reçu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Cancelled banner
            if (isCancelled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Commande Annulée",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Remboursement de ${displayOrder.totalFcfa} FCFA intégralement recrédité sur votre compte ${displayOrder.paymentMethod}.",
                                fontSize = 12.sp,
                                color = Color(0xFF7F0000)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Real-Time GPS Map Canvas
            if (!isCancelled) {
                LiveDeliveryMapCanvas(
                    telemetry = telemetry,
                    pharmacyName = displayOrder.pharmacyName,
                    deliveryAddress = displayOrder.deliveryAddress
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Real-Time Telemetry & Cold Chain Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header with Live Pulse
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isDelivered) VerifiedBadgeGreen else Color(0xFF00E676))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentStatus.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MedicalTealPrimary
                            )
                        }

                        // Auto Simulation Toggle Button
                        if (!isCancelled && !isDelivered) {
                            OutlinedButton(
                                onClick = { viewModel.toggleLiveAutoSimulation() },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(32.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isAutoSimulating) VerifiedBadgeGreen else TextSecondaryMuted
                                )
                            ) {
                                Icon(
                                    imageVector = if (isAutoSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAutoSimulating) "Direct Actif" else "En Pause",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Telemetry Grid: Distance, ETA, Cold-Chain Temp, Speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // ETA Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE0F2F1))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ARRIVÉE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MedicalTealDark)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val mins = telemetry.etaSeconds / 60
                                val secs = telemetry.etaSeconds % 60
                                Text(
                                    text = if (isDelivered) "0 min" else if (telemetry.etaSeconds > 0) "~${mins}m ${secs}s" else "~${displayOrder.deliveryEtaMinutes} min",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MedicalTealPrimary
                                )
                            }
                        }

                        // Distance Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8EAF6))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = SafeBlueSecondary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("DISTANCE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SafeBlueSecondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isDelivered) "0 m" else "${telemetry.distanceRemainingMeters} m",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SafeBlueSecondary
                                )
                            }
                        }

                        // Cold Chain Temp Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE0F7FA))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Thermostat, contentDescription = null, tint = Color(0xFF00838F), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("FROID ISOTHERME", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00838F))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${telemetry.temperatureCelsius}°C ✓",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00838F)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secret Delivery PIN Code
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF3E0))
                            .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CODE CONFIDENTIEL DE REMISE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "À communiquer au coursier lors de la livraison",
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Code PIN ${displayOrder.deliveryPinCode} copié !")
                                        }
                                    }
                                    .background(Color.White)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = displayOrder.deliveryPinCode,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE65100),
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copier",
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Courier Profile & Quick Communication Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MedicalTealLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "MN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MedicalTealPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = telemetry.courierName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimaryDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Certifié",
                                        tint = VerifiedBadgeGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "${telemetry.courierVehicle} • 4.95 ★ (${telemetry.courierDeliveriesCount} courses)",
                                    fontSize = 11.sp,
                                    color = TextSecondaryMuted
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Chat trigger
                            IconButton(
                                onClick = { isChatExpanded = !isChatExpanded },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2F1))
                                    .testTag("btn_toggle_courier_chat")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "Messagerie",
                                    tint = MedicalTealPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Call trigger
                            IconButton(
                                onClick = { showCallModal = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(VerifiedBadgeBg)
                                    .testTag("btn_call_courier")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Appeler",
                                    tint = VerifiedBadgeGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Collapsible Live Chat with Courier
                    AnimatedVisibility(visible = isChatExpanded, enter = fadeIn(), exit = fadeOut()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            HorizontalDivider(color = Color(0xFFEFF4F2))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Messagerie directe avec votre coursier de santé",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalTealPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Chat Messages Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF8FAF9))
                                    .padding(8.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    chatMessages.forEach { msg ->
                                        val isUser = msg.sender == "user"
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(
                                                        RoundedCornerShape(
                                                            topStart = 10.dp,
                                                            topEnd = 10.dp,
                                                            bottomStart = if (isUser) 10.dp else 2.dp,
                                                            bottomEnd = if (isUser) 2.dp else 10.dp
                                                        )
                                                    )
                                                    .background(if (isUser) MedicalTealPrimary else Color(0xFFE0ECE8))
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Column {
                                                    Text(
                                                        text = msg.text,
                                                        fontSize = 11.sp,
                                                        color = if (isUser) Color.White else TextPrimaryDark
                                                    )
                                                    Text(
                                                        text = "${msg.senderName.take(14)} • ${msg.timestamp}",
                                                        fontSize = 9.sp,
                                                        color = if (isUser) Color.White.copy(alpha = 0.7f) else TextSecondaryMuted,
                                                        modifier = Modifier.align(Alignment.End)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick Reply Pills
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val quickReplies = listOf(
                                    "Je suis au 2ème étage",
                                    "Sonnez à la porte",
                                    "Mon code PIN: ${displayOrder.deliveryPinCode}",
                                    "Le gardien est prévenu"
                                )
                                items(quickReplies) { reply ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFE0F2F1))
                                            .clickable { viewModel.sendCourierChatMessage(reply) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(reply, fontSize = 10.sp, color = MedicalTealPrimary, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Message Input Field
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = chatInputText,
                                    onValueChange = { chatInputText = it },
                                    placeholder = { Text("Écrire une consigne au livreur...", fontSize = 11.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("chat_input_field"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        if (chatInputText.isNotBlank()) {
                                            viewModel.sendCourierChatMessage(chatInputText)
                                            chatInputText = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MedicalTealPrimary)
                                        .testTag("btn_send_chat_msg")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Envoyer",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-Time Steps Timeline Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Étapes de livraison sécurisée en temps réel",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TimelineStep(
                        title = "1. Paiement en ligne validé",
                        subtitle = "Règlement ${order.paymentMethod} sécurisé • Réf: ${order.paymentTransactionId}",
                        isCompleted = currentStatus.stepIndex >= 1,
                        isCurrent = currentStatus.stepIndex == 1
                    )

                    TimelineStep(
                        title = "2. Contrôle & Préparation par le Pharmacien",
                        subtitle = "Vérification des dosages, dates de péremption et ordonnance",
                        isCompleted = currentStatus.stepIndex >= 2,
                        isCurrent = currentStatus.stepIndex == 2
                    )

                    TimelineStep(
                        title = "3. Colis pharmaceutique scellé",
                        subtitle = "Sac isotherme étanche et scellé avec bande de garantie inviolable",
                        isCompleted = currentStatus.stepIndex >= 3,
                        isCurrent = currentStatus.stepIndex == 3
                    )

                    TimelineStep(
                        title = "4. En cours de livraison express",
                        subtitle = "${telemetry.courierName} est en route vers ${displayOrder.deliveryAddress}",
                        isCompleted = currentStatus.stepIndex >= 4,
                        isCurrent = currentStatus.stepIndex == 4
                    )

                    TimelineStep(
                        title = "5. Livré à domicile avec succès",
                        subtitle = "Médicaments réceptionnés et vérifiés contre code PIN",
                        isCompleted = currentStatus.stepIndex >= 5,
                        isCurrent = currentStatus.stepIndex == 5,
                        isLast = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Order Content Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Médicaments dans ce colis",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = displayOrder.itemsSummary,
                        fontSize = 12.sp,
                        color = TextSecondaryMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFEFF4F2))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pharmacie dispensatrice :", fontSize = 11.sp, color = TextSecondaryMuted)
                        Text(displayOrder.pharmacyName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total payé (${displayOrder.paymentMethod}) :", fontSize = 11.sp, color = TextSecondaryMuted)
                        Text("${displayOrder.totalFcfa} FCFA", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MedicalTealPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            if (canCancel) {
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("tracking_cancel_order_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Annuler cette commande (Remboursement immédiat)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showInvoiceDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Voir la facture", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (!isCancelled) {
                    Button(
                        onClick = { viewModel.advanceOrderStatus(displayOrder.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (currentStatus == OrderStatus.DELIVERED) "Terminé ✓" else "Avancer étape",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TimelineStep(
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> VerifiedBadgeGreen
                            isCurrent -> MedicalTealPrimary
                            else -> Color(0xFFD0DDD9)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(if (isCompleted) VerifiedBadgeGreen else Color(0xFFD0DDD9))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 18.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent || isCompleted) TextPrimaryDark else TextSecondaryMuted
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondaryMuted
            )
        }
    }
}
