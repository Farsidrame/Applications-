package com.example.desktop

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.lightColorScheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

/**
 * Point d'entrée natif Compose Multiplatform Desktop pour Windows, macOS et Linux.
 * Permet l'exécution native indépendamment du runtime mobile Android.
 */
fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1280.dp, 820.dp),
        position = WindowPosition(Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "PharmaDirect — Plateforme Santé Multiplateforme (Desktop)"
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = Color(0xFF0F766E),
                secondary = Color(0xFF0284C7),
                background = Color(0xFFF8FAF9),
                surface = Color.White
            )
        ) {
            DesktopPlatformApp()
        }
    }
}

enum class DesktopNavScreen(val title: String, val icon: ImageVector) {
    HOME("Accueil", Icons.Default.Home),
    CATALOG("Catalogue Médicaments", Icons.Default.Medication),
    PRESCRIPTIONS("Ordonnances Sécurisées", Icons.Default.Description),
    CART("Panier Officinal", Icons.Default.ShoppingCart),
    ORDERS("Historique Commandes", Icons.Default.ReceiptLong),
    PROFILE("Profil & Sécurité", Icons.Default.Person),
    FAQ("Aide & FAQ", Icons.AutoMirrored.Filled.HelpOutline)
}

@Composable
fun DesktopPlatformApp() {
    var currentScreen by remember { mutableStateOf(DesktopNavScreen.HOME) }
    var cartCount by remember { mutableStateOf(2) }
    var isConnected by remember { mutableStateOf(true) }
    var pingMs by remember { mutableStateOf(24) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAF9))
    ) {
        // Barre Latérale Desktop (Windows / macOS / Linux)
        Surface(
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight(),
            color = Color(0xFF0F1E1B)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // En-tête de marque
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
                                .background(Color(0xFF0F766E)),
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
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Computer,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Desktop Multiplateforme",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF34D399)
                                )
                            }
                        }
                    }

                    // Statut de Connexion Natif
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132B25)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
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
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Connecté en Direct",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34D399)
                                )
                                Text(
                                    text = "$pingMs ms • Synchro Cloud",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            IconButton(
                                onClick = { pingMs = (18..35).random() },
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

                    Text(
                        text = "ESPACE BUREAU",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.45f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )

                    DesktopNavScreen.values().forEach { screen ->
                        val isSelected = currentScreen == screen
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { currentScreen = screen },
                            color = if (isSelected) Color(0xFF0F766E).copy(alpha = 0.25f) else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, Color(0xFF0F766E).copy(alpha = 0.6f)) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (isSelected) Color(0xFF34D399) else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = screen.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (screen == DesktopNavScreen.CART && cartCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF0F766E)
                                    ) {
                                        Text(
                                            text = "$cartCount",
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
                }

                // Pied de barre
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162723))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Certification Ordre Officinal",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Exécution native Skiko / JVM",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        VerticalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        // Zone de Travail Principale
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            // En-tête supérieur
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                color = Color.White,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Plateforme Desktop",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
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
                            color = Color(0xFF134E4A)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Poste Fixe • Windows / macOS / Linux",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF047857)
                                )
                            }
                        }

                        Button(
                            onClick = { currentScreen = DesktopNavScreen.CART },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                        ) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Panier ($cartCount)")
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Contenu centralisé
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(modifier = Modifier.fillMaxWidth().widthIn(max = 1100.dp)) {
                    Crossfade(targetState = currentScreen) { screen ->
                        when (screen) {
                            DesktopNavScreen.HOME -> DesktopHomeView(
                                onOpenCatalog = { currentScreen = DesktopNavScreen.CATALOG },
                                onOpenPrescriptions = { currentScreen = DesktopNavScreen.PRESCRIPTIONS }
                            )
                            DesktopNavScreen.CATALOG -> DesktopCatalogView(
                                onAddToCart = { cartCount++ }
                            )
                            DesktopNavScreen.PRESCRIPTIONS -> DesktopPrescriptionView()
                            DesktopNavScreen.CART -> DesktopCartView(
                                cartCount = cartCount,
                                onClear = { cartCount = 0 }
                            )
                            DesktopNavScreen.ORDERS -> DesktopOrdersView()
                            DesktopNavScreen.PROFILE -> DesktopProfileView()
                            DesktopNavScreen.FAQ -> DesktopFaqView()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DesktopHomeView(onOpenCatalog: () -> Unit, onOpenPrescriptions: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E))
        ) {
            Column(modifier = Modifier.padding(28.dp)) {
                Text(
                    text = "Bienvenue sur PharmaDirect Desktop",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Application native haute performance conçue pour les officines, pharmaciens et postes de travail sous Windows, macOS et distributions Linux.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onOpenCatalog,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Parcourir le Catalogue", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onOpenPrescriptions,
                        border = BorderStroke(1.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Transmettre Ordonnance")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Indicateurs Multiplateformes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF134E4A)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val stats = listOf(
                Triple("Support Natif", "Windows / Mac / Linux", Color(0xFF0F766E)),
                Triple("Moteur Graphique", "Compose Multiplatform Skiko", Color(0xFF0284C7)),
                Triple("Synchronisation", "En Direct • Temps Réel", Color(0xFF10B981))
            )
            stats.forEach { (label, value, tint) ->
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = label, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = tint)
                    }
                }
            }
        }
    }
}

@Composable
fun DesktopCatalogView(onAddToCart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Catalogue Médicaments Multiplateforme", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF134E4A))
        Spacer(modifier = Modifier.height(16.dp))
        val sampleMeds = listOf(
            Pair("Paracétamol 1000mg", "1 500 FCFA"),
            Pair("Amoxicilline 500mg", "3 200 FCFA"),
            Pair("Ibuprofène 400mg", "2 100 FCFA"),
            Pair("Sérum Physiologique", "800 FCFA")
        )
        sampleMeds.forEach { (name, price) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(text = "Officine Certifiée • En Stock", fontSize = 12.sp, color = Color(0xFF10B981))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = price, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F766E))
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = onAddToCart,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                        ) {
                            Text("+ Ajouter")
                        }
                    }
                }
            }
        }
    }
}

@Composable fun DesktopPrescriptionView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Module Ordonnances Sécurisées", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF134E4A))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Glissez-déposez vos fichiers ordonnances PDF, JPEG ou scannez directement depuis votre scanner de bureau.", fontSize = 13.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))) {
                Text("Importer un document d'ordonnance")
            }
        }
    }
}

@Composable fun DesktopCartView(cartCount: Int, onClear: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Votre Panier Multiplateforme", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF134E4A))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Articles sélectionnés : $cartCount", fontSize = 14.sp, color = Color(0xFF0F766E), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))) {
                    Text("Passer à la Commande")
                }
                OutlinedButton(onClick = onClear) {
                    Text("Vider le Panier")
                }
            }
        }
    }
}

@Composable fun DesktopOrdersView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Historique des Commandes Officine", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF134E4A))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Toutes les commandes passées depuis mobile et ordinateur sont synchronisées.", fontSize = 13.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable fun DesktopProfileView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Profil Utilisateur & Paramètres Bureau", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF134E4A))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Gestion des certificats officinaux, des adresses de livraison et de la sécurité TEE.", fontSize = 13.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable fun DesktopFaqView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Guide d'Utilisation & Documentation Desktop", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF134E4A))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Guide complet pour l'utilisation de PharmaDirect en officine sur Windows, macOS et Linux.", fontSize = 13.sp, color = Color(0xFF64748B))
        }
    }
}
