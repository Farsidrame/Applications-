package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthUser
import com.example.data.model.Medicine
import com.example.data.model.OrderEntity
import com.example.data.model.Pharmacy
import com.example.ui.components.AuthDialog
import com.example.ui.components.SmsDeliveryAlertDialog
import com.example.ui.components.SmsInboxBottomSheet
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.CheckoutPaymentScreen
import com.example.ui.screens.FaqScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MandatoryAuthScreen
import com.example.ui.screens.MedicineDetailScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.OrdersHistoryScreen
import com.example.ui.screens.PharmacistAdviceScreen
import com.example.ui.screens.PharmacyDetailScreen
import com.example.ui.screens.PrescriptionsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.viewmodel.NetworkConnectionInfo
import com.example.ui.viewmodel.PharmaViewModel

enum class Screen(val title: String, val icon: ImageVector, val tag: String) {
    HOME("Accueil", Icons.Default.Home, "nav_home"),
    CATALOG("Catalogue", Icons.Default.Medication, "nav_catalog"),
    PRESCRIPTIONS("Ordonnances", Icons.Default.Description, "nav_prescriptions"),
    CART("Panier", Icons.Default.ShoppingCart, "nav_cart"),
    ORDERS("Commandes", Icons.Default.ReceiptLong, "nav_orders"),
    PROFILE("Profil", Icons.Default.Person, "nav_profile"),
    ADVICE("Conseil", Icons.Default.Chat, "nav_advice"),
    FAQ("Guide & FAQ", Icons.AutoMirrored.Filled.HelpOutline, "nav_faq"),
    MEDICINE_DETAIL("Détail", Icons.Default.Medication, "nav_detail"),
    PHARMACY_DETAIL("Pharmacie", Icons.Default.LocalPharmacy, "nav_pharmacy_detail"),
    CHECKOUT("Paiement", Icons.Default.ShoppingCart, "nav_checkout"),
    TRACKING("Suivi", Icons.Default.ReceiptLong, "nav_tracking")
}

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val pharmaViewModel: PharmaViewModel = viewModel()
                PharmaApp(viewModel = pharmaViewModel)
            }
        }
    }
}

@Composable
fun PharmaApp(viewModel: PharmaViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    var selectedPharmacy by remember { mutableStateOf<Pharmacy?>(null) }
    var trackingOrder by remember { mutableStateOf<OrderEntity?>(null) }

    val isUserAuthenticated by viewModel.isUserAuthenticated.collectAsStateWithLifecycle()

    if (!isUserAuthenticated) {
        MandatoryAuthScreen(
            viewModel = viewModel,
            onAuthenticated = {
                currentScreen = Screen.HOME
            }
        )
        return
    }

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val showSmsAlert by viewModel.showSmsAlertDialog.collectAsStateWithLifecycle()
    val latestSms by viewModel.latestDeliveredSmsAlert.collectAsStateWithLifecycle()
    val smsList by viewModel.smsNotifications.collectAsStateWithLifecycle()
    val showSmsInbox by viewModel.showSmsInboxSheet.collectAsStateWithLifecycle()
    val networkInfo by viewModel.networkInfo.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val cartCount = cartItems.sumOf { it.quantity }
    val cartTotalFcfa = cartItems.sumOf { it.priceFcfa * it.quantity }

    val bottomNavScreens = listOf(
        Screen.HOME,
        Screen.CATALOG,
        Screen.PRESCRIPTIONS,
        Screen.CART,
        Screen.PROFILE
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isDesktopPlatform = maxWidth >= 720.dp

        if (isDesktopPlatform) {
            // ==========================================
            // FORMAT PLATEFORME SUR ORDINATEUR (DESKTOP)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAF9))
            ) {
                // Barre de Navigation Latérale (Sidebar Plateforme)
                PlatformDesktopSidebar(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it },
                    cartCount = cartCount,
                    networkInfo = networkInfo,
                    currentUser = currentUser,
                    smsCount = smsList.size,
                    onOpenSmsInbox = { viewModel.openSmsInbox() },
                    onRefreshNetwork = { viewModel.refreshNetworkStatus() }
                )

                VerticalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                // Espace de Travail Principal
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // En-tête Plateforme Supérieur
                    PlatformDesktopTopBar(
                        currentScreen = currentScreen,
                        networkInfo = networkInfo,
                        cartCount = cartCount,
                        cartTotalFcfa = cartTotalFcfa,
                        onNavigateToCart = { currentScreen = Screen.CART },
                        onNavigateToCatalog = { currentScreen = Screen.CATALOG },
                        onRefreshNetwork = { viewModel.refreshNetworkStatus() }
                    )

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                    // Contenu de la vue avec centrage ergonomique
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAF9)),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 1200.dp)
                        ) {
                            PharmaScreenRouter(
                                currentScreen = currentScreen,
                                viewModel = viewModel,
                                orders = orders,
                                selectedMedicine = selectedMedicine,
                                selectedPharmacy = selectedPharmacy,
                                trackingOrder = trackingOrder,
                                onNavigate = { currentScreen = it },
                                onSelectMedicine = { med ->
                                    selectedMedicine = med
                                    currentScreen = Screen.MEDICINE_DETAIL
                                },
                                onSelectPharmacy = { pharm ->
                                    selectedPharmacy = pharm
                                    currentScreen = Screen.PHARMACY_DETAIL
                                },
                                onSelectOrder = { ord ->
                                    trackingOrder = ord
                                    currentScreen = Screen.TRACKING
                                }
                            )
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // FORMAT MOBILE (TABLETTE COMPACTE / TÉLÉPHONE)
            // ==========================================
            val showBottomBar = currentScreen in bottomNavScreens || currentScreen == Screen.ORDERS

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            bottomNavScreens.forEach { screen ->
                                val isSelected = currentScreen == screen || (screen == Screen.PROFILE && currentScreen == Screen.ORDERS)
                                NavigationBarItem(
                                    modifier = Modifier.testTag(screen.tag),
                                    selected = isSelected,
                                    onClick = {
                                        currentScreen = screen
                                    },
                                    icon = {
                                        if (screen == Screen.CART && cartCount > 0) {
                                            BadgedBox(
                                                badge = {
                                                    Badge(
                                                        containerColor = MedicalTealPrimary,
                                                        contentColor = Color.White
                                                    ) {
                                                        Text(
                                                            text = "$cartCount",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = screen.icon,
                                                    contentDescription = screen.title,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        } else {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = screen.title,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MedicalTealPrimary,
                                        selectedTextColor = MedicalTealDark,
                                        unselectedIconColor = TextSecondaryMuted,
                                        unselectedTextColor = TextSecondaryMuted,
                                        indicatorColor = MedicalTealLight
                                    )
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    PharmaScreenRouter(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        orders = orders,
                        selectedMedicine = selectedMedicine,
                        selectedPharmacy = selectedPharmacy,
                        trackingOrder = trackingOrder,
                        onNavigate = { currentScreen = it },
                        onSelectMedicine = { med ->
                            selectedMedicine = med
                            currentScreen = Screen.MEDICINE_DETAIL
                        },
                        onSelectPharmacy = { pharm ->
                            selectedPharmacy = pharm
                            currentScreen = Screen.PHARMACY_DETAIL
                        },
                        onSelectOrder = { ord ->
                            trackingOrder = ord
                            currentScreen = Screen.TRACKING
                        }
                    )
                }
            }
        }

        // Global Real-Time Delivery SMS Alert Dialog
        if (showSmsAlert && latestSms != null) {
            val alertSms = latestSms!!
            SmsDeliveryAlertDialog(
                sms = alertSms,
                onDismiss = { viewModel.dismissSmsAlert() },
                onViewOrder = {
                    val targetOrder = orders.find { it.id == alertSms.orderId }
                    if (targetOrder != null) {
                        trackingOrder = targetOrder
                        currentScreen = Screen.TRACKING
                    }
                },
                onDelete = {
                    viewModel.deleteSmsNotification(alertSms.id)
                }
            )
        }

        // Global SMS Inbox Bottom Sheet
        if (showSmsInbox) {
            val currentContext = LocalContext.current
            SmsInboxBottomSheet(
                smsList = smsList,
                onDismiss = { viewModel.closeSmsInbox() },
                onDeleteSms = { smsId ->
                    viewModel.deleteSmsNotification(smsId)
                },
                onDeleteBillingSms = {
                    viewModel.deleteAllBillingSms()
                },
                onClearAll = {
                    viewModel.clearAllSms()
                },
                onSimulateScenario = { scenario ->
                    val activeOrder = trackingOrder ?: orders.firstOrNull()
                    viewModel.simulateDeliverySmsWithScenario(currentContext, scenario, activeOrder)
                }
            )
        }

        // Global Firebase Authentication Dialog
        val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()
        val isAuthLoading by viewModel.authLoading.collectAsStateWithLifecycle()

        if (showAuthDialog) {
            AuthDialog(
                onDismiss = { viewModel.closeAuthDialog() },
                isLoading = isAuthLoading,
                onSignInEmail = { email, password, onSuccess, onError ->
                    viewModel.signInWithEmail(email, password, onSuccess, onError)
                },
                onSignUpEmail = { email, password, fullName, phone, role, onSuccess, onError ->
                    viewModel.signUpWithEmail(email, password, fullName, phone, role, onSuccess, onError)
                },
                onSignInPhone = { phone, otp, fullName, role, onSuccess, onError ->
                    viewModel.signInWithPhone(phone, otp, fullName, role, onSuccess, onError)
                },
                onSignInGoogle = { email, name, role, onSuccess, onError ->
                    viewModel.signInWithGoogle(email, name, role, onSuccess, onError)
                },
                onPasswordReset = { email, onSuccess, onError ->
                    viewModel.sendPasswordReset(email, onSuccess, onError)
                },
                onAuthSuccess = { _ ->
                    viewModel.closeAuthDialog()
                }
            )
        }
    }
}

/**
 * Routeur unique pour afficher les vues selon l'écran actif
 */
@Composable
private fun PharmaScreenRouter(
    currentScreen: Screen,
    viewModel: PharmaViewModel,
    orders: List<OrderEntity>,
    selectedMedicine: Medicine?,
    selectedPharmacy: Pharmacy?,
    trackingOrder: OrderEntity?,
    onNavigate: (Screen) -> Unit,
    onSelectMedicine: (Medicine) -> Unit,
    onSelectPharmacy: (Pharmacy) -> Unit,
    onSelectOrder: (OrderEntity) -> Unit
) {
    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                viewModel = viewModel,
                onNavigateToCatalog = { onNavigate(Screen.CATALOG) },
                onNavigateToPrescriptions = { onNavigate(Screen.PRESCRIPTIONS) },
                onNavigateToCart = { onNavigate(Screen.CART) },
                onNavigateToProfile = { onNavigate(Screen.PROFILE) },
                onNavigateToAdvice = { onNavigate(Screen.ADVICE) },
                onNavigateToFaq = { onNavigate(Screen.FAQ) },
                onNavigateToTracking = { order -> onSelectOrder(order) },
                onMedicineClick = { med -> onSelectMedicine(med) },
                onPharmacyClick = { pharm -> onSelectPharmacy(pharm) }
            )

            Screen.CATALOG -> CatalogScreen(
                viewModel = viewModel,
                onMedicineClick = { med -> onSelectMedicine(med) },
                onPharmacyClick = { pharm -> onSelectPharmacy(pharm) }
            )

            Screen.PRESCRIPTIONS -> PrescriptionsScreen(
                viewModel = viewModel,
                onNavigateToCart = { onNavigate(Screen.CART) }
            )

            Screen.CART -> CartScreen(
                viewModel = viewModel,
                onNavigateToCheckout = { onNavigate(Screen.CHECKOUT) },
                onNavigateToCatalog = { onNavigate(Screen.CATALOG) }
            )

            Screen.ORDERS -> OrdersHistoryScreen(
                viewModel = viewModel,
                onSelectOrder = { order -> onSelectOrder(order) },
                onNavigateToCart = { onNavigate(Screen.CART) },
                onNavigateToCatalog = { onNavigate(Screen.CATALOG) }
            )

            Screen.PROFILE -> ProfileScreen(viewModel = viewModel)

            Screen.ADVICE -> PharmacistAdviceScreen(
                viewModel = viewModel,
                onBack = { onNavigate(Screen.HOME) }
            )

            Screen.FAQ -> FaqScreen(
                onBack = { onNavigate(Screen.HOME) },
                onNavigateToCatalog = { onNavigate(Screen.CATALOG) },
                onNavigateToPrescriptions = { onNavigate(Screen.PRESCRIPTIONS) },
                onNavigateToAdvice = { onNavigate(Screen.ADVICE) }
            )

            Screen.MEDICINE_DETAIL -> {
                selectedMedicine?.let { med ->
                    MedicineDetailScreen(
                        medicine = med,
                        viewModel = viewModel,
                        onBack = { onNavigate(Screen.CATALOG) },
                        onAddToCartAndGo = { onNavigate(Screen.CART) }
                    )
                } ?: run {
                    onNavigate(Screen.CATALOG)
                }
            }

            Screen.PHARMACY_DETAIL -> {
                selectedPharmacy?.let { pharm ->
                    PharmacyDetailScreen(
                        pharmacy = pharm,
                        viewModel = viewModel,
                        onBack = { onNavigate(Screen.HOME) },
                        onMedicineClick = { med -> onSelectMedicine(med) }
                    )
                } ?: run {
                    onNavigate(Screen.HOME)
                }
            }

            Screen.CHECKOUT -> CheckoutPaymentScreen(
                viewModel = viewModel,
                onBack = { onNavigate(Screen.CART) },
                onPaymentSuccess = { newOrder -> onSelectOrder(newOrder) }
            )

            Screen.TRACKING -> {
                val activeOrder = trackingOrder ?: orders.firstOrNull()
                if (activeOrder != null) {
                    OrderTrackingScreen(
                        order = activeOrder,
                        viewModel = viewModel,
                        onBack = { onNavigate(Screen.ORDERS) }
                    )
                } else {
                    onNavigate(Screen.ORDERS)
                }
            }
        }
    }
}

/**
 * Barre de navigation latérale pour la disposition Plateforme Ordinateur
 */
@Composable
fun PlatformDesktopSidebar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    cartCount: Int,
    networkInfo: NetworkConnectionInfo,
    currentUser: AuthUser?,
    smsCount: Int,
    onOpenSmsInbox: () -> Unit,
    onRefreshNetwork: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight(),
        color = Color(0xFF0F1E1B), // Dark medical slate
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header Logo Plateforme
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MedicalTealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalPharmacy,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PharmaDirect",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.3).sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = null,
                                tint = MedicalEmeraldAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Plateforme Ordinateur",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MedicalEmeraldAccent
                            )
                        }
                    }
                }

                // Badge Statut de Connexion Réseau
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onRefreshNetwork() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (networkInfo.isConnected) Color(0xFF132B25) else Color(0xFF331B1B)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (networkInfo.isConnected) VerifiedBadgeGreen.copy(alpha = 0.5f) else Color(0xFFEF4444).copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (networkInfo.isConnected) Color(0xFF10B981) else Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (networkInfo.isConnected) "Connecté en Ligne" else "Mode Hors-Ligne",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (networkInfo.isConnected) Color(0xFF34D399) else Color(0xFFF87171)
                            )
                            Text(
                                text = "${networkInfo.latencyMs} ms • Synchro active",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(
                            onClick = onRefreshNetwork,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Rafraîchir",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(bottom = 12.dp))

                // Section 1 : Navigation Principale
                Text(
                    text = "ESPACE PRINCIPAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.45f),
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )

                val mainItems = listOf(
                    Triple(Screen.HOME, "Accueil", Icons.Default.Home),
                    Triple(Screen.CATALOG, "Catalogue Médicaments", Icons.Default.Medication),
                    Triple(Screen.PRESCRIPTIONS, "Ordonnances & IA", Icons.Default.Description),
                    Triple(Screen.CART, "Panier Santé", Icons.Default.ShoppingCart),
                    Triple(Screen.ORDERS, "Mes Commandes", Icons.Default.ReceiptLong)
                )

                mainItems.forEach { (screen, title, icon) ->
                    val isSelected = currentScreen == screen
                    PlatformSidebarItem(
                        title = title,
                        icon = icon,
                        isSelected = isSelected,
                        badgeCount = if (screen == Screen.CART) cartCount else 0,
                        onClick = { onNavigate(screen) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2 : Services & Assistance
                Text(
                    text = "SERVICES & ASSISTANCE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.45f),
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )

                val serviceItems = listOf(
                    Triple(Screen.ADVICE, "Conseil Pharmacien", Icons.Default.Chat),
                    Triple(Screen.FAQ, "Guide & FAQ Officine", Icons.AutoMirrored.Filled.HelpOutline),
                    Triple(Screen.PROFILE, "Mon Profil Sécurisé", Icons.Default.Person)
                )

                serviceItems.forEach { (screen, title, icon) ->
                    val isSelected = currentScreen == screen
                    PlatformSidebarItem(
                        title = title,
                        icon = icon,
                        isSelected = isSelected,
                        badgeCount = 0,
                        onClick = { onNavigate(screen) }
                    )
                }
            }

            // Pied de barre latérale : Notifications & Utilisateur
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                // Raccourci Notifications SMS
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenSmsInbox() },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162723))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MedicalEmeraldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Notifications SMS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                        if (smsCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = MedicalTealPrimary
                            ) {
                                Text(
                                    text = "$smsCount",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fiche Profil Utilisateur
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onNavigate(Screen.PROFILE) }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MedicalTealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.displayName?.take(1) ?: "U").uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.displayName ?: "Utilisateur Connecté",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Client Santé Vérifié",
                            fontSize = 10.5.sp,
                            color = MedicalEmeraldAccent
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bouton d'élément de navigation latérale
 */
@Composable
private fun PlatformSidebarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = if (isSelected) MedicalTealPrimary.copy(alpha = 0.25f) else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, MedicalTealPrimary.copy(alpha = 0.6f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) MedicalEmeraldAccent else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (badgeCount > 0) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MedicalTealPrimary
                ) {
                    Text(
                        text = "$badgeCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * En-tête supérieur pour la disposition Plateforme Ordinateur
 */
@Composable
fun PlatformDesktopTopBar(
    currentScreen: Screen,
    networkInfo: NetworkConnectionInfo,
    cartCount: Int,
    cartTotalFcfa: Int,
    onNavigateToCart: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onRefreshNetwork: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Fil d'Ariane
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Plateforme Santé",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondaryMuted
                )
                Text(
                    text = " / ",
                    fontSize = 14.sp,
                    color = Color(0xFFCBD5E1)
                )
                Text(
                    text = currentScreen.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalTealDark
                )
            }

            // Statut de connexion et Actions rapides
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Pilule de statut réseau
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (networkInfo.isConnected) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                    border = BorderStroke(
                        1.dp,
                        if (networkInfo.isConnected) Color(0xFFA7F3D0) else Color(0xFFFECACA)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onRefreshNetwork() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (networkInfo.isConnected) Color(0xFF10B981) else Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (networkInfo.isConnected) "${networkInfo.connectionType} • En Ligne" else "Mode Hors-Connexion",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (networkInfo.isConnected) Color(0xFF047857) else Color(0xFFB91C1C)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Synchroniser",
                            tint = if (networkInfo.isConnected) Color(0xFF047857) else Color(0xFFB91C1C),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                // Raccourci Recherche Catalogue
                OutlinedButton(
                    onClick = onNavigateToCatalog,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalTealDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Rechercher",
                        modifier = Modifier.size(16.dp),
                        tint = MedicalTealPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rechercher Médicaments",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Raccourci Panier Santé
                Button(
                    onClick = onNavigateToCart,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalTealPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Panier",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Panier ($cartCount)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (cartTotalFcfa > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• $cartTotalFcfa FCFA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}
