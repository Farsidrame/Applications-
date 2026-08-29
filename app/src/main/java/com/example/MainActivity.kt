package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.PharmaDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.Medicine
import com.example.data.model.OrderEntity
import com.example.data.model.Pharmacy
import com.example.data.repository.PharmaRepository
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.CheckoutPaymentScreen
import com.example.ui.screens.HomeScreen
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
import com.example.ui.viewmodel.PharmaViewModel

enum class Screen(val title: String, val icon: ImageVector, val tag: String) {
    HOME("Accueil", Icons.Default.Home, "nav_home"),
    CATALOG("Catalogue", Icons.Default.Medication, "nav_catalog"),
    PRESCRIPTIONS("Ordonnances", Icons.Default.Description, "nav_prescriptions"),
    CART("Panier", Icons.Default.ShoppingCart, "nav_cart"),
    ORDERS("Commandes", Icons.Default.ReceiptLong, "nav_orders"),
    PROFILE("Profil", Icons.Default.Person, "nav_profile"),
    ADVICE("Conseil", Icons.Default.Chat, "nav_advice"),
    MEDICINE_DETAIL("Détail", Icons.Default.Medication, "nav_detail"),
    PHARMACY_DETAIL("Pharmacie", Icons.Default.LocalPharmacy, "nav_pharmacy_detail"),
    CHECKOUT("Paiement", Icons.Default.ShoppingCart, "nav_checkout"),
    TRACKING("Suivi", Icons.Default.ReceiptLong, "nav_tracking")
}

class MainActivity : ComponentActivity() {
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

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val cartCount = cartItems.sumOf { it.quantity }

    val bottomNavScreens = listOf(
        Screen.HOME,
        Screen.CATALOG,
        Screen.PRESCRIPTIONS,
        Screen.CART,
        Screen.ORDERS,
        Screen.PROFILE
    )

    val showBottomBar = currentScreen in bottomNavScreens

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavScreens.forEach { screen ->
                        val isSelected = currentScreen == screen
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
                                                Text("$cartCount")
                                            }
                                        }
                                    ) {
                                        Icon(screen.icon, contentDescription = screen.title)
                                    }
                                } else {
                                    Icon(screen.icon, contentDescription = screen.title)
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MedicalTealPrimary,
                                selectedTextColor = MedicalTealPrimary,
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
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCatalog = { currentScreen = Screen.CATALOG },
                        onNavigateToPrescriptions = { currentScreen = Screen.PRESCRIPTIONS },
                        onNavigateToCart = { currentScreen = Screen.CART },
                        onNavigateToProfile = { currentScreen = Screen.PROFILE },
                        onNavigateToAdvice = { currentScreen = Screen.ADVICE },
                        onNavigateToTracking = { order ->
                            trackingOrder = order
                            currentScreen = Screen.TRACKING
                        },
                        onMedicineClick = { medicine ->
                            selectedMedicine = medicine
                            currentScreen = Screen.MEDICINE_DETAIL
                        },
                        onPharmacyClick = { pharmacy ->
                            selectedPharmacy = pharmacy
                            currentScreen = Screen.PHARMACY_DETAIL
                        }
                    )

                    Screen.CATALOG -> CatalogScreen(
                        viewModel = viewModel,
                        onMedicineClick = { medicine ->
                            selectedMedicine = medicine
                            currentScreen = Screen.MEDICINE_DETAIL
                        },
                        onPharmacyClick = { pharmacy ->
                            selectedPharmacy = pharmacy
                            currentScreen = Screen.PHARMACY_DETAIL
                        }
                    )

                    Screen.PRESCRIPTIONS -> PrescriptionsScreen(
                        viewModel = viewModel,
                        onNavigateToCart = { currentScreen = Screen.CART }
                    )

                    Screen.CART -> CartScreen(
                        viewModel = viewModel,
                        onNavigateToCheckout = { currentScreen = Screen.CHECKOUT },
                        onNavigateToCatalog = { currentScreen = Screen.CATALOG }
                    )

                    Screen.ORDERS -> OrdersHistoryScreen(
                        viewModel = viewModel,
                        onSelectOrder = { order ->
                            trackingOrder = order
                            currentScreen = Screen.TRACKING
                        },
                        onNavigateToCart = { currentScreen = Screen.CART },
                        onNavigateToCatalog = { currentScreen = Screen.CATALOG }
                    )

                    Screen.PROFILE -> ProfileScreen(viewModel = viewModel)

                    Screen.ADVICE -> PharmacistAdviceScreen(viewModel = viewModel)

                    Screen.MEDICINE_DETAIL -> {
                        selectedMedicine?.let { med ->
                            MedicineDetailScreen(
                                medicine = med,
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.CATALOG },
                                onAddToCartAndGo = { currentScreen = Screen.CART }
                            )
                        } ?: run {
                            currentScreen = Screen.CATALOG
                        }
                    }

                    Screen.PHARMACY_DETAIL -> {
                        selectedPharmacy?.let { pharm ->
                            PharmacyDetailScreen(
                                pharmacy = pharm,
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.HOME },
                                onMedicineClick = { med ->
                                    selectedMedicine = med
                                    currentScreen = Screen.MEDICINE_DETAIL
                                }
                            )
                        } ?: run {
                            currentScreen = Screen.HOME
                        }
                    }

                    Screen.CHECKOUT -> CheckoutPaymentScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = Screen.CART },
                        onPaymentSuccess = { newOrder ->
                            trackingOrder = newOrder
                            currentScreen = Screen.TRACKING
                        }
                    )

                    Screen.TRACKING -> {
                        val activeOrder = trackingOrder ?: orders.firstOrNull()
                        if (activeOrder != null) {
                            OrderTrackingScreen(
                                order = activeOrder,
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.ORDERS }
                            )
                        } else {
                            currentScreen = Screen.ORDERS
                        }
                    }
                }
            }
        }
    }
}
