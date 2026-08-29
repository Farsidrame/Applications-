package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.components.InvoiceDialog
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersHistoryScreen(
    viewModel: PharmaViewModel,
    onSelectOrder: (OrderEntity) -> Unit,
    onNavigateToCart: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: Toutes, 1: En cours, 2: Livrées, 3: Annulées
    var searchQuery by remember { mutableStateOf("") }

    var invoiceOrderToView by remember { mutableStateOf<OrderEntity?>(null) }
    var orderToCancel by remember { mutableStateOf<OrderEntity?>(null) }
    var orderToDelete by remember { mutableStateOf<OrderEntity?>(null) }
    var orderFullDetailToView by remember { mutableStateOf<OrderEntity?>(null) }

    // Dialogs
    invoiceOrderToView?.let { order ->
        InvoiceDialog(order = order, onDismiss = { invoiceOrderToView = null })
    }

    orderFullDetailToView?.let { order ->
        OrderFullDetailDialog(
            order = order,
            onDismiss = { orderFullDetailToView = null },
            onViewInvoice = {
                orderFullDetailToView = null
                invoiceOrderToView = order
            },
            onTrack = {
                orderFullDetailToView = null
                onSelectOrder(order)
            },
            onReorder = {
                viewModel.reorderPastOrder(order) { count ->
                    orderFullDetailToView = null
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "$count article(s) de la commande ${order.orderNumber} ajouté(s) au panier !",
                            actionLabel = "Voir panier",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            onNavigateToCart()
                        }
                    }
                }
            }
        )
    }

    // Cancel Order Dialog
    orderToCancel?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToCancel = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Annuler la commande ${order.orderNumber} ?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Êtes-vous sûr de vouloir annuler cette commande passée auprès de ${order.pharmacyName} ?",
                        fontSize = 13.sp,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Remboursement intégral de ${order.totalFcfa} FCFA déclenché instantanément sur votre compte ${order.paymentMethod} (0% frais d'annulation).",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelOrder(order.id)
                        orderToCancel = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Commande ${order.orderNumber} annulée. Remboursement initié.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_cancel_order_button")
                ) {
                    Text("Oui, Annuler la commande", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToCancel = null }) {
                    Text("Conserver la commande", color = MedicalTealPrimary, fontSize = 12.sp)
                }
            }
        )
    }

    // Delete Order Dialog
    orderToDelete?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Supprimer de l'historique ?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Text(
                    text = "Cette action supprimera définitivement la commande ${order.orderNumber} de votre historique local.",
                    fontSize = 13.sp,
                    color = TextSecondaryMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteOrder(order.id)
                        orderToDelete = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Commande supprimée de l'historique.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_delete_order_button")
                ) {
                    Text("Supprimer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text("Annuler", color = TextSecondaryMuted)
                }
            }
        )
    }

    // Filter calculations
    val totalOrdersCount = orders.size
    val totalSpentFcfa = orders.filter { it.status != OrderStatus.CANCELLED.name }.sumOf { it.totalFcfa }
    val activeOrdersCount = orders.count {
        it.status in listOf(
            OrderStatus.PENDING_PAYMENT.name,
            OrderStatus.PAID_CONFIRMED.name,
            OrderStatus.PHARMACIST_PREPARING.name,
            OrderStatus.SEALED_DISPATCHED.name,
            OrderStatus.OUT_FOR_DELIVERY.name
        )
    }
    val deliveredOrdersCount = orders.count { it.status == OrderStatus.DELIVERED.name }
    val cancelledOrdersCount = orders.count { it.status == OrderStatus.CANCELLED.name }

    val filteredOrders = orders.filter { order ->
        val statusMatches = when (selectedFilterIndex) {
            1 -> order.status in listOf(
                OrderStatus.PENDING_PAYMENT.name,
                OrderStatus.PAID_CONFIRMED.name,
                OrderStatus.PHARMACIST_PREPARING.name,
                OrderStatus.SEALED_DISPATCHED.name,
                OrderStatus.OUT_FOR_DELIVERY.name
            )
            2 -> order.status == OrderStatus.DELIVERED.name
            3 -> order.status == OrderStatus.CANCELLED.name
            else -> true
        }

        val searchMatches = if (searchQuery.isBlank()) true else {
            val q = searchQuery.trim().lowercase()
            order.orderNumber.lowercase().contains(q) ||
            order.pharmacyName.lowercase().contains(q) ||
            order.itemsSummary.lowercase().contains(q) ||
            order.deliveryAddress.lowercase().contains(q) ||
            order.paymentMethod.lowercase().contains(q)
        }

        statusMatches && searchMatches
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("orders_history_screen"),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Header Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Historique des Commandes",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Détails complets, factures certifiées & rachat express",
                                fontSize = 12.sp,
                                color = TextSecondaryMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(VerifiedBadgeBg)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = VerifiedBadgeGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "100% Officiel",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VerifiedBadgeGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary KPI Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Total Orders
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = MedicalEmeraldAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Commandes", fontSize = 10.sp, color = TextSecondaryMuted, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$totalOrdersCount passée${if (totalOrdersCount > 1) "s" else ""}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                        }

                        // Total Spent
                        Card(
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MedicalTealLight)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payment, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Total Dépensé", fontSize = 10.sp, color = TextSecondaryMuted, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$totalSpentFcfa FCFA", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = MedicalTealPrimary)
                            }
                        }

                        // Active orders indicator
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (activeOrdersCount > 0) Color(0xFFE0F7FA) else Color(0xFFF5F5F5)
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = if (activeOrdersCount > 0) SafeBlueSecondary else TextSecondaryMuted, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("En cours", fontSize = 10.sp, color = TextSecondaryMuted, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (activeOrdersCount > 0) "$activeOrdersCount active${if (activeOrdersCount > 1) "s" else ""}" else "0 active",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeOrdersCount > 0) SafeBlueSecondary else TextSecondaryMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("orders_search_input"),
                        placeholder = { Text("Rechercher par N° (#PH-...), médicament, pharmacie...", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Rechercher", tint = TextSecondaryMuted, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = TextSecondaryMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filterTabs = listOf(
                            "Toutes ($totalOrdersCount)",
                            "En cours ($activeOrdersCount)",
                            "Livrées ($deliveredOrdersCount)",
                            "Annulées ($cancelledOrdersCount)"
                        )

                        items(filterTabs.indices.toList()) { index ->
                            val isSelected = selectedFilterIndex == index
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilterIndex = index },
                                label = {
                                    Text(
                                        text = filterTabs[index],
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MedicalTealPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = TextPrimaryDark
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("order_filter_tab_$index")
                            )
                        }
                    }
                }
            }

            // Order List or Empty State
            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MedicalTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedFilterIndex != 0)
                                "Aucune commande correspondante"
                            else
                                "Aucune commande pour le moment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedFilterIndex != 0)
                                "Modifiez vos filtres de recherche pour afficher vos commandes passées."
                            else
                                "Vos ordonnances délivrées, reçus officiels et suivis en temps réel apparaîtront ici dès votre premier achat.",
                            fontSize = 12.sp,
                            color = TextSecondaryMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (searchQuery.isNotBlank() || selectedFilterIndex != 0) {
                            OutlinedButton(
                                onClick = {
                                    searchQuery = ""
                                    selectedFilterIndex = 0
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Réinitialiser les filtres", color = MedicalTealPrimary, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = onNavigateToCatalog,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                            ) {
                                Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Découvrir le catalogue médical", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        EnhancedOrderHistoryCard(
                            order = order,
                            onTrack = { onSelectOrder(order) },
                            onViewInvoice = { invoiceOrderToView = order },
                            onViewFullDetail = { orderFullDetailToView = order },
                            onCancel = { orderToCancel = order },
                            onDelete = { orderToDelete = order },
                            onReorder = {
                                viewModel.reorderPastOrder(order) { count ->
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "$count article(s) ajouté(s) au panier avec succès !",
                                            actionLabel = "Voir panier",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            onNavigateToCart()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedOrderHistoryCard(
    order: OrderEntity,
    onTrack: () -> Unit,
    onViewInvoice: () -> Unit,
    onViewFullDetail: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onReorder: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val dateStr = SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRENCH).format(Date(order.orderTimestamp))
    val statusEnum = try { OrderStatus.valueOf(order.status) } catch (e: Exception) { OrderStatus.PAID_CONFIRMED }
    val isCancelled = statusEnum == OrderStatus.CANCELLED
    val isDelivered = statusEnum == OrderStatus.DELIVERED
    val isActive = !isCancelled && !isDelivered
    val canCancel = isActive && statusEnum != OrderStatus.OUT_FOR_DELIVERY

    // Parse product items
    val productItems = remember(order.itemsSummary) {
        order.itemsSummary.split(" | ").mapNotNull { itemStr ->
            val parts = itemStr.split(" x")
            val name = parts.firstOrNull()?.trim() ?: return@mapNotNull null
            val qty = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 1
            Pair(name, qty)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_item_${order.orderNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Order number, date, status and delete action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = order.orderNumber,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (order.isPrescriptionVerified) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE3F2FD))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = SafeBlueSecondary, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Rx Conforme", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SafeBlueSecondary)
                                }
                            }
                        }
                    }
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = TextSecondaryMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isCancelled -> Color(0xFFFFEBEE)
                                    isDelivered -> VerifiedBadgeBg
                                    statusEnum == OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFE0F7FA)
                                    else -> Color(0xFFE0F2F1)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when {
                                    isCancelled -> Icons.Default.Cancel
                                    isDelivered -> Icons.Default.CheckCircle
                                    statusEnum == OrderStatus.OUT_FOR_DELIVERY -> Icons.Default.LocalShipping
                                    else -> Icons.Default.LocalPharmacy
                                },
                                contentDescription = null,
                                tint = when {
                                    isCancelled -> Color(0xFFD32F2F)
                                    isDelivered -> VerifiedBadgeGreen
                                    statusEnum == OrderStatus.OUT_FOR_DELIVERY -> SafeBlueSecondary
                                    else -> MedicalTealPrimary
                                },
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = statusEnum.label,
                                color = when {
                                    isCancelled -> Color(0xFFD32F2F)
                                    isDelivered -> VerifiedBadgeGreen
                                    statusEnum == OrderStatus.OUT_FOR_DELIVERY -> SafeBlueSecondary
                                    else -> MedicalTealPrimary
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_order_btn_${order.orderNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Supprimer de l'historique",
                            tint = Color(0xFFB0BEC5),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pharmacy Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF9FBFA))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalPharmacy,
                    contentDescription = null,
                    tint = MedicalTealPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = order.pharmacyName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = order.pharmacyAddress,
                        fontSize = 10.sp,
                        color = TextSecondaryMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFEFF4F2))
            Spacer(modifier = Modifier.height(10.dp))

            // Products Itemized Section
            Text(
                text = "Médicaments & Produits commandés (${productItems.size}) :",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            productItems.forEach { (name, quantity) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MedicalTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFECEFF1))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Qté: $quantity",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF455A64)
                        )
                    }
                }
            }

            // Expandable extra details (Delivery address, courier, PIN, payment transaction)
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFEFF4F2))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Delivery Address
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Livré à :", fontSize = 10.sp, color = TextSecondaryMuted)
                            Text("${order.patientName} (${order.patientPhone})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text(order.deliveryAddress, fontSize = 11.sp, color = TextSecondaryMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Payment & Transaction ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = SafeBlueSecondary, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Paiement :", fontSize = 10.sp, color = TextSecondaryMuted)
                                Text(order.paymentMethod, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Transaction N° :", fontSize = 10.sp, color = TextSecondaryMuted)
                            Text(order.paymentTransactionId, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Courier & Secret PIN
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5FBF9))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MedicalEmeraldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Coursier agréé :", fontSize = 9.sp, color = TextSecondaryMuted)
                                Text(order.courierName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.dp, VerifiedBadgeGreen, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = VerifiedBadgeGreen, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("PIN: ${order.deliveryPinCode}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VerifiedBadgeGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price Breakdown Detail
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sous-total articles :", fontSize = 11.sp, color = TextSecondaryMuted)
                        Text("${order.subtotalFcfa} FCFA", fontSize = 11.sp, color = TextPrimaryDark)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Livraison sécurisée sous chaîne du froid :", fontSize = 11.sp, color = TextSecondaryMuted)
                        Text("${order.deliveryFeeFcfa} FCFA", fontSize = 11.sp, color = TextPrimaryDark)
                    }
                }
            }

            // Expand / Collapse Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Afficher moins" else "Voir détails de livraison & paiement",
                    fontSize = 11.sp,
                    color = MedicalTealPrimary,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MedicalTealPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFEFF4F2))
            Spacer(modifier = Modifier.height(10.dp))

            // Total & Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total payé TTC :", fontSize = 10.sp, color = TextSecondaryMuted)
                    Text(
                        text = "${order.totalFcfa} FCFA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCancelled) Color(0xFF9E9E9E) else MedicalTealPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Reorder button (1-click)
                    Button(
                        onClick = onReorder,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("reorder_btn_${order.orderNumber}"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Racheter", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Invoice / Receipt button
                    OutlinedButton(
                        onClick = onViewInvoice,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("view_invoice_btn_${order.orderNumber}"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(13.dp), tint = TextPrimaryDark)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Reçu", fontSize = 11.sp, color = TextPrimaryDark)
                    }

                    // Track button or cancel button
                    if (isActive) {
                        Button(
                            onClick = onTrack,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("track_order_btn_${order.orderNumber}"),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Suivre", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else if (canCancel) {
                        OutlinedButton(
                            onClick = onCancel,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("Annuler", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onViewFullDetail,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(13.dp), tint = MedicalTealPrimary)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Fiche", fontSize = 11.sp, color = MedicalTealPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderFullDetailDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onViewInvoice: () -> Unit,
    onTrack: () -> Unit,
    onReorder: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRENCH).format(Date(order.orderTimestamp))
    val statusEnum = try { OrderStatus.valueOf(order.status) } catch (e: Exception) { OrderStatus.PAID_CONFIRMED }
    val isCancelled = statusEnum == OrderStatus.CANCELLED
    val isDelivered = statusEnum == OrderStatus.DELIVERED
    val isActive = !isCancelled && !isDelivered

    val productItems = remember(order.itemsSummary) {
        order.itemsSummary.split(" | ").mapNotNull { itemStr ->
            val parts = itemStr.split(" x")
            val name = parts.firstOrNull()?.trim() ?: return@mapNotNull null
            val qty = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 1
            Pair(name, qty)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Fiche Médicale de Commande",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = order.orderNumber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalTealPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Clear, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Final status pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                isCancelled -> Color(0xFFFFEBEE)
                                isDelivered -> VerifiedBadgeBg
                                else -> Color(0xFFE0F2F1)
                            }
                        )
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                isCancelled -> Icons.Default.Cancel
                                isDelivered -> Icons.Default.CheckCircle
                                else -> Icons.Default.LocalShipping
                            },
                            contentDescription = null,
                            tint = when {
                                isCancelled -> Color(0xFFD32F2F)
                                isDelivered -> VerifiedBadgeGreen
                                else -> MedicalTealPrimary
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Statut : ${statusEnum.label}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = when {
                                    isCancelled -> Color(0xFFD32F2F)
                                    isDelivered -> VerifiedBadgeGreen
                                    else -> MedicalTealPrimary
                                }
                            )
                            Text(
                                text = "Date d'enregistrement : $dateStr",
                                fontSize = 11.sp,
                                color = TextSecondaryMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dispensing Pharmacy Certificate
                Text("Pharmacie Dispensatrice Agréée :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBFA))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(order.pharmacyName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)
                        }
                        Text(order.pharmacyAddress, fontSize = 11.sp, color = TextSecondaryMuted, modifier = Modifier.padding(start = 22.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.padding(start = 22.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBadgeGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Certifié par l'Ordre National des Pharmaciens du Sénégal", fontSize = 10.sp, color = VerifiedBadgeGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Items list
                Text("Médicaments Délivrés :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(6.dp))
                productItems.forEach { (name, qty) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimaryDark)
                        }
                        Text("$qty boîte${if (qty > 1) "s" else ""}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF455A64))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFEFF4F2))
                Spacer(modifier = Modifier.height(10.dp))

                // Logistics & Cold Chain Assurance
                Text("Garantie & Télémétrie Logistique :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Chaîne du Froid", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text("2°C - 8°C Respecté", fontSize = 11.sp, color = Color(0xFF1B5E20))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0F7FA))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Code Secret Remise", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SafeBlueSecondary)
                            Text("PIN: ${order.deliveryPinCode}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SafeBlueSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Patient and Delivery destination
                Text("Destinataire & Adresse :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Patient : ${order.patientName} (${order.patientPhone})", fontSize = 11.sp, color = TextPrimaryDark)
                Text("Adresse : ${order.deliveryAddress}", fontSize = 11.sp, color = TextSecondaryMuted)

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Audit Log
                Text("Audit Financier & Transaction :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Passerelle : ${order.paymentMethod}", fontSize = 11.sp, color = TextPrimaryDark)
                Text("N° Transaction : ${order.paymentTransactionId}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondaryMuted)
                Text("Total Réglé : ${order.totalFcfa} FCFA (Sous-total: ${order.subtotalFcfa} FCFA + Livraison: ${order.deliveryFeeFcfa} FCFA)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedicalTealPrimary)

                Spacer(modifier = Modifier.height(18.dp))

                // Dialog action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewInvoice,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Facture PDF", fontSize = 11.sp)
                    }

                    if (isActive) {
                        Button(
                            onClick = onTrack,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Suivre", fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = onReorder,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Racheter", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
