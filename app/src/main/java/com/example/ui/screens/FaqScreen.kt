package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DutyPharmacyBg
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalBackgroundLight
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalSurfaceWhite
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.OrangeMoneyColor
import com.example.ui.theme.PrescriptionAlertBg
import com.example.ui.theme.PrescriptionAlertRed
import com.example.ui.theme.SafeBlueLight
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.theme.WaveBlueColor

/**
 * Catégories de questions fréquentes (FAQ)
 */
enum class FaqCategory(val label: String, val icon: ImageVector) {
    ALL("Toutes les questions", Icons.AutoMirrored.Filled.HelpOutline),
    ORDERING("Procédure de Commande", Icons.Default.LocalShipping),
    MEDICINE_USAGE("Utilisation des Médicaments", Icons.Default.Medication),
    PRESCRIPTIONS("Ordonnances & Agréments", Icons.Default.UploadFile),
    PAYMENT_INVOICE("Paiement & Facturation", Icons.Default.Payment),
    COLD_CHAIN_SAFETY("Chaîne du Froid & Sécurité", Icons.Default.AcUnit)
}

/**
 * Modèle de données pour un élément FAQ
 */
data class FaqItem(
    val id: String,
    val category: FaqCategory,
    val question: String,
    val summary: String,
    val detailedAnswer: List<String>,
    val keyAdvice: String? = null,
    val tag: String,
    val tagColor: Color,
    val tagBg: Color,
    val isImportant: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(
    onBack: () -> Unit,
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToPrescriptions: () -> Unit = {},
    onNavigateToAdvice: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(FaqCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    
    // État d'expansion pour chaque question
    val expandedItems = remember { mutableStateMapOf<String, Boolean>() }
    
    // État de vote d'utilité (itemId -> true: utile, false: non utile)
    val feedbackMap = remember { mutableStateMapOf<String, Boolean>() }

    // Liste exhaustive des questions & réponses FAQ
    val faqList = remember { getInitialFaqItems() }

    // Filtrage dynamique
    val filteredFaq = faqList.filter { item ->
        val matchesCategory = selectedCategory == FaqCategory.ALL || item.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                item.question.contains(searchQuery, ignoreCase = true) ||
                item.summary.contains(searchQuery, ignoreCase = true) ||
                item.detailedAnswer.any { it.contains(searchQuery, ignoreCase = true) } ||
                item.tag.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("faq_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Guide & FAQ Utilisateur",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Usage des médicaments & Procédure de commande",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("faq_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1515"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.testTag("faq_samu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "SAMU 1515",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MedicalTealPrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MedicalBackgroundLight)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header Banner Découverte & Guide Rapide
            item {
                FaqHeroBanner(
                    onNavigateToAdvice = onNavigateToAdvice,
                    onNavigateToCatalog = onNavigateToCatalog
                )
            }

            // Guide Débutant en 4 Étapes Clés
            item {
                BeginnerGuideSection(
                    onNavigateToPrescriptions = onNavigateToPrescriptions,
                    onNavigateToCatalog = onNavigateToCatalog
                )
            }

            // Barre de Recherche FAQ
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("faq_search_input"),
                        placeholder = {
                            Text(
                                "Rechercher une question, posologie, Wave, ordonnance...",
                                fontSize = 13.sp,
                                color = TextSecondaryMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Recherche",
                                tint = MedicalTealPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Effacer",
                                        tint = TextSecondaryMuted
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MedicalSurfaceWhite,
                            unfocusedContainerColor = MedicalSurfaceWhite,
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFD6E3E0)
                        )
                    )
                }
            }

            // Filtres par Catégorie (Chips)
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FaqCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedicalTealPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White,
                                containerColor = MedicalSurfaceWhite,
                                labelColor = TextPrimaryDark,
                                iconColor = MedicalTealPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) MedicalTealPrimary else Color(0xFFD6E3E0)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("faq_chip_${category.name}")
                        )
                    }
                }
            }

            // Compteur de résultats
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredFaq.size} question(s) trouvée(s)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryMuted
                    )
                    
                    if (searchQuery.isNotEmpty() || selectedCategory != FaqCategory.ALL) {
                        TextButton(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = FaqCategory.ALL
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                "Réinitialiser les filtres",
                                fontSize = 12.sp,
                                color = MedicalTealPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Liste des Questions / Réponses
            if (filteredFaq.isEmpty()) {
                item {
                    EmptyFaqResult(
                        searchQuery = searchQuery,
                        onReset = {
                            searchQuery = ""
                            selectedCategory = FaqCategory.ALL
                        },
                        onContactPharmacist = onNavigateToAdvice
                    )
                }
            } else {
                items(filteredFaq, key = { it.id }) { item ->
                    val isExpanded = expandedItems[item.id] ?: false
                    val userFeedback = feedbackMap[item.id]

                    FaqExpandableCard(
                        faq = item,
                        isExpanded = isExpanded,
                        userFeedback = userFeedback,
                        onToggle = {
                            expandedItems[item.id] = !isExpanded
                        },
                        onFeedback = { helpful ->
                            feedbackMap[item.id] = helpful
                            val msg = if (helpful) "Merci pour votre retour positif !" else "Merci, nous allons clarifier cette réponse."
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onNavigateToCatalog = onNavigateToCatalog,
                        onNavigateToPrescriptions = onNavigateToPrescriptions,
                        onNavigateToAdvice = onNavigateToAdvice
                    )
                }
            }

            // Section Assistance d'Urgence & Contact Pharmacien
            item {
                Spacer(modifier = Modifier.height(12.dp))
                FaqHelpFooter(
                    onContactPharmacist = onNavigateToAdvice,
                    onEmergencyCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1515"))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

/**
 * Bannière d'en-tête informative
 */
@Composable
fun FaqHeroBanner(
    onNavigateToAdvice: () -> Unit,
    onNavigateToCatalog: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(MedicalTealDark, MedicalTealPrimary, MedicalEmeraldAccent)
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Centre d'Aide & Bonnes Pratiques",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Réglementation pharmaceutique du Sénégal • DPM",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Bienvenue sur PharmaDirect Sénégal ! Retrouvez ici toutes les réponses aux questions sur la commande de médicaments, la validation d'ordonnances, la chaîne du froid et la posologie sécurisée.",
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateToAdvice,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = MedicalTealDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Avis Pharmacien",
                            color = MedicalTealDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onNavigateToCatalog,
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Catalogue",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Guide interactif pas-à-pas pour les nouveaux utilisateurs
 */
@Composable
fun BeginnerGuideSection(
    onNavigateToPrescriptions: () -> Unit,
    onNavigateToCatalog: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalSurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE2ECE9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SafeBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SafeBlueSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Guide Débutant : Commander en 4 Étapes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextOnWhitePrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Procédure officielle simplifiée pour votre première commande",
                        fontSize = 11.5.sp,
                        color = TextOnWhiteSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Les 4 étapes illustrées bien rangées et lisibles
            StepGuideItem(
                stepNumber = "1",
                title = "Sélectionnez vos médicaments ou scannez l'ordonnance",
                desc = "Recherchez par nom ou symptôme dans notre catalogue agréé, ou prenez une photo claire de votre ordonnance médicale.",
                badge = "Catalogue ou Scan",
                icon = Icons.Default.Search,
                accentColor = MedicalTealPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            StepGuideItem(
                stepNumber = "2",
                title = "Choisissez votre pharmacie partenaire & de garde",
                desc = "Sélectionnez parmi les officines agréées au Sénégal avec contrôle thermique garanti et disponibilité certifiée en stock.",
                badge = "Officines Agréées",
                icon = Icons.Default.LocalPharmacy,
                accentColor = SafeBlueSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            StepGuideItem(
                stepNumber = "3",
                title = "Paiement numérique 100% sécurisé",
                desc = "Réglez vos achats sans contact via Wave, Orange Money, Free Money ou carte bancaire avec reçu immédiat.",
                badge = "Wave / Orange Money",
                icon = Icons.Default.Payment,
                accentColor = OrangeMoneyColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            StepGuideItem(
                stepNumber = "4",
                title = "Livraison express scellée & SMS de conformité",
                desc = "Suivez le coursier en temps réel par GPS. Remise sécurisée avec code PIN et notification SMS officielle.",
                badge = "20-35 min • SMS",
                icon = Icons.Default.LocalShipping,
                accentColor = VerifiedBadgeGreen
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Raccourcis d'actions rapides sous les 4 étapes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToCatalog,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MedicalTealPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MedicalTealPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Catalogue",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalTealPrimary
                    )
                }

                Button(
                    onClick = onNavigateToPrescriptions,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Scanner Ordonnance",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun StepGuideItem(
    stepNumber: String,
    title: String,
    desc: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Ligne 1 : En-tête bien aligné avec pastille d'étape et badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pastille numéro d'étape
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stepNumber,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Étiquette avec icône
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Étape $stepNumber",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }

                // Badge descriptif aligné à droite
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ligne 2 : Titre complet, visible et non compressé
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = TextOnWhitePrimary,
                lineHeight = 19.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Ligne 3 : Description aérée avec couleur sombre lisible
            Text(
                text = desc,
                fontSize = 12.sp,
                color = TextOnWhiteSecondary,
                lineHeight = 17.5.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Carte de question / réponse expandable avec détails et feedback
 */
@Composable
fun FaqExpandableCard(
    faq: FaqItem,
    isExpanded: Boolean,
    userFeedback: Boolean?,
    onToggle: () -> Unit,
    onFeedback: (Boolean) -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToPrescriptions: () -> Unit,
    onNavigateToAdvice: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .testTag("faq_card_${faq.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) Color(0xFFFAFCFC) else MedicalSurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 2.dp else 1.dp),
        border = BorderStroke(
            1.dp,
            if (isExpanded) MedicalTealPrimary.copy(alpha = 0.5f) else Color(0xFFE2ECE9)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(14.dp)
        ) {
            // En-tête de la carte : Tag catégorie + Question + Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(faq.tagBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = faq.tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = faq.tagColor
                            )
                        }

                        if (faq.isImportant) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrescriptionAlertBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "CONSEIL ESSENTIEL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrescriptionAlertRed
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = faq.question,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimaryDark,
                        lineHeight = 19.sp
                    )
                }

                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Réduire" else "Dérouler",
                        tint = if (isExpanded) MedicalTealPrimary else TextSecondaryMuted
                    )
                }
            }

            // Résumé court visible même si non déployé
            if (!isExpanded) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = faq.summary,
                    fontSize = 12.sp,
                    color = TextOnWhiteSecondary,
                    lineHeight = 16.5.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Contenu détaillé déplié
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFE2ECE9))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Paragraphes explicatifs
                    faq.detailedAnswer.forEach { paragraph ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MedicalTealPrimary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = paragraph,
                                fontSize = 12.sp,
                                color = TextPrimaryDark,
                                lineHeight = 17.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Encadré Conseil Clé / Vigilance
                    faq.keyAdvice?.let { advice ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (faq.isImportant) PrescriptionAlertBg else Color(0xFFE8F5E9),
                            border = BorderStroke(
                                1.dp,
                                if (faq.isImportant) Color(0xFFFFCDD2) else Color(0xFFC8E6C9)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = if (faq.isImportant) Icons.Default.Warning else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (faq.isImportant) PrescriptionAlertRed else VerifiedBadgeGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (faq.isImportant) "Attention & Précautions :" else "Bonne pratique pharmaceutique :",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (faq.isImportant) PrescriptionAlertRed else VerifiedBadgeGreen
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = advice,
                                        fontSize = 11.sp,
                                        color = TextPrimaryDark,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Barre de feedback d'utilité
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEFF5F4))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cette réponse vous a-t-elle aidé ?",
                            fontSize = 11.sp,
                            color = TextSecondaryMuted,
                            fontWeight = FontWeight.Medium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Utile
                            OutlinedButton(
                                onClick = { onFeedback(true) },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.height(28.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (userFeedback == true) VerifiedBadgeBg else Color.Transparent
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (userFeedback == true) VerifiedBadgeGreen else Color(0xFFC0D2CF)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Oui",
                                    tint = if (userFeedback == true) VerifiedBadgeGreen else TextSecondaryMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Oui",
                                    fontSize = 11.sp,
                                    color = if (userFeedback == true) VerifiedBadgeGreen else TextSecondaryMuted,
                                    fontWeight = if (userFeedback == true) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            // Non utile
                            OutlinedButton(
                                onClick = { onFeedback(false) },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.height(28.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (userFeedback == false) PrescriptionAlertBg else Color.Transparent
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (userFeedback == false) PrescriptionAlertRed else Color(0xFFC0D2CF)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbDown,
                                    contentDescription = "Non",
                                    tint = if (userFeedback == false) PrescriptionAlertRed else TextSecondaryMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Non",
                                    fontSize = 11.sp,
                                    color = if (userFeedback == false) PrescriptionAlertRed else TextSecondaryMuted,
                                    fontWeight = if (userFeedback == false) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Pied de page avec actions de support & urgence
 */
@Composable
fun FaqHelpFooter(
    onContactPharmacist: () -> Unit,
    onEmergencyCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalSurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFD6E3E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Vous n'avez pas trouvé votre réponse ?",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Nos pharmaciens diplômés d'État et le service d'assistance sont à votre écoute 24h/24 et 7j/7.",
                fontSize = 12.sp,
                color = TextSecondaryMuted,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onContactPharmacist,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat Pharmacien", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onEmergencyCall,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrescriptionAlertRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SAMU 1515", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Vue vide si aucun résultat de recherche
 */
@Composable
fun EmptyFaqResult(
    searchQuery: String,
    onReset: () -> Unit,
    onContactPharmacist: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAEFEF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondaryMuted,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Aucune question trouvée pour \"$searchQuery\"",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimaryDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Essayez d'autres mots-clés ou posez directement votre question à nos pharmaciens.",
                fontSize = 12.sp,
                color = TextSecondaryMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Effacer la recherche", fontSize = 12.sp)
                }
                Button(
                    onClick = onContactPharmacist,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                ) {
                    Text("Poser la question", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Base de données exhaustive des questions et réponses officielles
 */
fun getInitialFaqItems(): List<FaqItem> {
    return listOf(
        // Catégorie : Procédure de Commande
        FaqItem(
            id = "faq_order_1",
            category = FaqCategory.ORDERING,
            question = "Comment passer ma première commande de médicaments ?",
            summary = "Recherchez vos produits, ajoutez-les au panier, choisissez une officine partenaire, payez en toute sécurité et recevez votre livraison.",
            detailedAnswer = listOf(
                "1. Utilisez la barre de recherche sur l'écran d'accueil ou parcourez le catalogue par catégorie thérapeutique (Douleur, Fièvre, Antibiotiques, Diabète, Pédiatrie).",
                "2. Vérifiez la fiche détaillée du médicament (forme galénique, dosage, indications et précautions).",
                "3. Ajoutez au panier. Si une ordonnance est requise, vous serez invité à la numériser ou à la sélectionner.",
                "4. Choisissez votre adresse de livraison ou utilisez la géolocalisation GPS.",
                "5. Réglez en toute sécurité via Wave, Orange Money ou carte bancaire.",
                "6. Suivez en direct le livreur sur la carte GPS jusqu'à votre porte."
            ),
            keyAdvice = "Chaque commande est vérifiée par le Docteur en Pharmacie titulaire avant d'être scellée dans un emballage opaque et inviolable.",
            tag = "Procédure Débutant",
            tagColor = MedicalTealPrimary,
            tagBg = MedicalTealLight,
            isImportant = false
        ),
        FaqItem(
            id = "faq_order_2",
            category = FaqCategory.ORDERING,
            question = "Quels sont les délais et zones de livraison au Sénégal ?",
            summary = "Livraison express en 20 à 35 minutes à Dakar et banlieue (Plateau, Almadies, Mermoz, Parcelles, Guédiawaye, Rufisque) et expéditions régionales.",
            detailedAnswer = listOf(
                "• Dakar intra-muros (Plateau, Fann, Point E, Mermoz, Ouakam, Almadies) : 20 à 30 minutes en moyenne.",
                "• Banlieue de Dakar (Grand Yoff, Parcelles Assainies, Pikine, Guédiawaye, Keur Massar, Rufisque, Diamniadio) : 30 à 45 minutes.",
                "• Les livraisons sont assurées 24h/24 et 7j/7 grâce à notre réseau d'officines de garde d'astreinte nocturne.",
                "• Les frais de livraison sont transparents (généralement 1 000 à 1 500 FCFA selon la distance calculée par GPS)."
            ),
            keyAdvice = "En cas d'urgence vitale de nuit, le statut 'Urgence Prioritaire' est automatiquement attribué à votre commande.",
            tag = "Délais & Zones",
            tagColor = SafeBlueSecondary,
            tagBg = SafeBlueLight,
            isImportant = false
        ),
        FaqItem(
            id = "faq_order_3",
            category = FaqCategory.ORDERING,
            question = "Comment fonctionne le code PIN de sécurité lors de la livraison ?",
            summary = "Un code PIN à 4 chiffres généré pour chaque commande garantit que les médicaments vous sont remis en main propre.",
            detailedAnswer = listOf(
                "• Dès que votre commande est expédiée, un code PIN sécurisé à 4 chiffres apparaît sur votre écran de suivi et dans votre SMS de notification.",
                "• Lors de l'arrivée du coursier, communiquez-lui ce code pour déverrouiller et valider la remise des médicaments.",
                "• Ce protocole empêche toute erreur de destinataire et garantit la traçabilité médico-légale de la délivrance."
            ),
            keyAdvice = "Ne donnez jamais votre code PIN avant d'avoir vérifié l'intégrité du scellé d'inviolabilité du colis.",
            tag = "Sécurité Remise",
            tagColor = VerifiedBadgeGreen,
            tagBg = VerifiedBadgeBg,
            isImportant = true
        ),

        // Catégorie : Utilisation des Médicaments
        FaqItem(
            id = "faq_med_1",
            category = FaqCategory.MEDICINE_USAGE,
            question = "Comment bien respecter la posologie et les horaires de prise ?",
            summary = "Respectez scrupuleusement les intervalles horaires, la relation avec les repas (à jeun, pendant ou après) et la durée prescrite.",
            detailedAnswer = listOf(
                "• Les prises espacées (ex: 3 fois par jour) doivent idéalement respecter des intervalles réguliers (toutes les 8 heures pour maintenir le principe actif dans le sang).",
                "• 'Avant le repas' : prenez le médicament 30 minutes avant de manger pour éviter que la nourriture ne bloque son absorption.",
                "• 'Pendant ou après le repas' : souvent recommandé pour les anti-inflammatoires (Ibuprofène, Kétoprofène) afin de protéger la muqueuse de l'estomac.",
                "• Buvez toujours un grand verre d'eau plate (évitez les sodas, jus d'agrumes ou thé qui peuvent interagir)."
            ),
            keyAdvice = "Vous pouvez configurer des rappels automatiques quotidiens dans l'onglet 'Conseil > Rappels Médicaments' de l'application.",
            tag = "Bonnes Pratiques",
            tagColor = MedicalTealPrimary,
            tagBg = MedicalTealLight,
            isImportant = true
        ),
        FaqItem(
            id = "faq_med_2",
            category = FaqCategory.MEDICINE_USAGE,
            question = "Que faire si j'ai oublié de prendre une dose de médicament ?",
            summary = "Prenez la dose oubliée dès que possible, sauf s'il est presque l'heure de la prise suivante. Ne doublez jamais une dose.",
            detailedAnswer = listOf(
                "• Si vous vous en rendez compte peu de temps après l'heure prévue : prenez immédiatement votre dose habituelle.",
                "• Si l'heure de la dose suivante est proche : sautez la dose oubliée et continuez votre schéma posologique normal.",
                "• RÈGLE D'OR : Ne prenez JAMAIS une double dose pour compenser celle que vous avez oubliée. Cela augmente fortement le risque de toxicité ou de surdosage.",
                "• Pour les pilules contraceptives ou traitements chroniques critiques (insuline, anticoagulants), consultez immédiatement le chat avec notre pharmacien."
            ),
            keyAdvice = "En cas de doute sur une molécule à marge thérapeutique étroite, demandez conseil via le bouton 'Chat Pharmacien'.",
            tag = "Oubli de Prise",
            tagColor = DutyPharmacyOrange,
            tagBg = DutyPharmacyBg,
            isImportant = true
        ),
        FaqItem(
            id = "faq_med_3",
            category = FaqCategory.MEDICINE_USAGE,
            question = "Pourquoi ne jamais interrompre un traitement antibiotique avant la fin ?",
            summary = "Arrêter un antibiotique dès la disparition des symptômes favorise les rechutes et l'antibiorésistance des bactéries.",
            detailedAnswer = listOf(
                "• Même si la fièvre ou la douleur disparaît après 48h, des bactéries pathogènes restent vivantes dans votre organisme.",
                "• Si vous cessez le traitement trop tôt, les bactéries survivantes mutent et développent des résistances : le médicament deviendra inefficace pour vous à l'avenir.",
                "• Suivez la durée exacte prescrite par le médecin (ex: 5 jours, 7 jours) sans jamais garder les comprimés restants pour une automédication future."
            ),
            keyAdvice = "L'antibiorésistance est une urgence de santé publique mondiale. Ne partagez jamais vos antibiotiques avec des proches.",
            tag = "Antibiotiques",
            tagColor = PrescriptionAlertRed,
            tagBg = PrescriptionAlertBg,
            isImportant = true
        ),
        FaqItem(
            id = "faq_med_4",
            category = FaqCategory.MEDICINE_USAGE,
            question = "Comment conserver mes médicaments sous le climat chaud du Sénégal ?",
            summary = "Conservez les médicaments à l'abri de l'humidité et de la chaleur (< 25°C). Les produits thermosensibles doivent être placés au réfrigérateur (2°C-8°C).",
            detailedAnswer = listOf(
                "• Médicaments standards (comprimés, sirops, gélules) : Conservez-les dans un endroit sec, à l'abri de la lumière directe du soleil et à une température inférieure à 25°C-30°C.",
                "• Évitez de stocker vos médicaments dans la salle de bain ou la cuisine en raison des variations fortes d'humidité.",
                "• Médicaments thermosensibles (insuline, certains collyres, vaccins, probiotiques) : À conserver impérativement dans le compartiment central du réfrigérateur (entre 2°C et 8°C).",
                "• Ne mettez JAMAIS de médicaments au congélateur : la congélation détruit irréversiblement les structures moléculaires des protéines et vaccins."
            ),
            keyAdvice = "Nos livreurs utilisent des caissons isothermes thermorégulés certifiés pour maintenir la chaîne du froid durant tout le trajet.",
            tag = "Conservation & Climat",
            tagColor = SafeBlueSecondary,
            tagBg = SafeBlueLight,
            isImportant = true
        ),
        FaqItem(
            id = "faq_med_5",
            category = FaqCategory.MEDICINE_USAGE,
            question = "Quels sont les dangers de l'association de certains médicaments (interactions) ?",
            summary = "Certaines associations peuvent annuler l'effet du traitement ou provoquer des hémorragies et toxicité rénale ou hépatique.",
            detailedAnswer = listOf(
                "• Paracétamol + Alcool ou surdosage (> 4g/jour chez l'adulte) : Risque d'hépatite médicamenteuse sévère et destruction du foie.",
                "• Aspirine + Anti-inflammatoires (Ibuprofène, Diclofénac) : Risque majeur d'ulcère de l'estomac et d'hémorragies digestives.",
                "• Antibiotiques + Pamplemousse / Lait : Le jus de pamplemousse bloque certaines enzymes hépatiques et démultiplie la toxicité des molécules.",
                "• Notre application intègre un détecteur automatique d'interactions médicamenteuses qui analyse le contenu de votre panier avant validation."
            ),
            keyAdvice = "Renseignez toujours vos allergies et traitements chroniques dans votre profil santé PharmaDirect.",
            tag = "Interactions & Sécurité",
            tagColor = PrescriptionAlertRed,
            tagBg = PrescriptionAlertBg,
            isImportant = true
        ),

        // Catégorie : Ordonnances & Agréments
        FaqItem(
            id = "faq_presc_1",
            category = FaqCategory.PRESCRIPTIONS,
            question = "Comment faire valider mon ordonnance médicale sur l'application ?",
            summary = "Prenez une photo nette de votre ordonnance ou importez un fichier PDF. Un pharmacien l'analyse sous 10 minutes.",
            detailedAnswer = listOf(
                "1. Rendez-vous dans l'onglet 'Ordonnances' ou cliquez sur 'Scanner Ordonnance' depuis l'écran d'accueil.",
                "2. Prenez une photo claire et lisible ou importez votre document (nom du médecin, signature, cachet et date visibles).",
                "3. Le Docteur en Pharmacie vérifie la conformité réglementaire, les posologies, les contre-indications et prépare la délivrance.",
                "4. Vous recevez une notification dès que la préparation est validée, prête à être ajoutée à votre panier en un clic."
            ),
            keyAdvice = "Les ordonnances scannées sont conservées de manière cryptée conformément aux règles de confidentialité médicale (Secret Médical).",
            tag = "Validation Ordonnance",
            tagColor = MedicalTealPrimary,
            tagBg = MedicalTealLight,
            isImportant = false
        ),
        FaqItem(
            id = "faq_presc_2",
            category = FaqCategory.PRESCRIPTIONS,
            question = "Comment reconnaître un médicament authentique et certifié ?",
            summary = "Tous nos médicaments proviennent exclusivement d'officines agréées par la Direction de la Pharmacie et du Médicament (DPM) du Sénégal.",
            detailedAnswer = listOf(
                "• Chaque boîte comporte un numéro d'enregistrement DPM / AMM (Autorisation de Mise sur le Marché).",
                "• Présence systématique du numéro de lot, de la date de péremption imprimée en relief et du scellé d'inviolabilité officiel.",
                "• Les circuits informels ou les faux médicaments de la rue ('pharmacie par terre') sont formellement exclus de notre plateforme.",
                "• Vous pouvez scanner le QR Code présent sur votre facture pour vérifier la traçabilité complète du lot délivré."
            ),
            keyAdvice = "Acheter des médicaments hors officines agréées met votre vie en danger. PharmaDirect garantit 100% d'authenticité pharmaceutique.",
            tag = "Authenticité & DPM",
            tagColor = VerifiedBadgeGreen,
            tagBg = VerifiedBadgeBg,
            isImportant = true
        ),

        // Catégorie : Paiement & Facturation
        FaqItem(
            id = "faq_pay_1",
            category = FaqCategory.PAYMENT_INVOICE,
            question = "Quels sont les moyens de paiement acceptés ?",
            summary = "Paiement 100% dématérialisé et sécurisé via Wave Mobile Money, Orange Money, Free Money et Cartes Bancaires (Visa, Mastercard).",
            detailedAnswer = listOf(
                "• Wave Mobile Money : Validation instantanée sans frais cachés (0% de commission de paiement).",
                "• Orange Money Sénégal : Authentification par code OTP sécurisé envoyé sur votre numéro de téléphone.",
                "• Free Money : Règlement direct par portefeuille électronique sénégalais.",
                "• Carte Bancaire : Protocole 3D-Secure avec cryptage bancaire SSL/TLS 256 bits.",
                "• Les fonds sont sécurisés et ne sont débloqués à l'officine qu'après confirmation de la remise en main propre."
            ),
            keyAdvice = "Le paiement dématérialisé garantit la transparence des tarifs officiels fixés par le Ministère de la Santé.",
            tag = "Wave & Mobile Money",
            tagColor = WaveBlueColor,
            tagBg = SafeBlueLight,
            isImportant = false
        ),
        FaqItem(
            id = "faq_pay_2",
            category = FaqCategory.PAYMENT_INVOICE,
            question = "Comment obtenir ma facture officielle et ma notification SMS ?",
            summary = "Une facture certifiée avec QR Code est téléchargeable dans l'application et un SMS de confirmation vous est envoyé automatiquement.",
            detailedAnswer = listOf(
                "• Dès que la commande est livrée, une facture acquittée conforme au format officiel sénégalais est générée.",
                "• La facture contient : le nom de l'officine titulaire, son numéro d'inscription à l'Ordre, la liste des médicaments avec TVA/exonérations, et le matricule du coursier.",
                "• Vous pouvez l'imprimer ou l'exporter en PDF pour vos remboursements d'assurance (IPM, mutuelles de santé, assurances privées).",
                "• Un SMS certifié contenant le détail de votre transaction et l'adresse de délivrance vous est instantanément transmis."
            ),
            keyAdvice = "Consultez l'historique complet de vos factures dans 'Profil > Factures Certifiées & Liens SMS'.",
            tag = "Factures & Assurances",
            tagColor = MedicalTealDark,
            tagBg = MedicalTealLight,
            isImportant = false
        ),

        // Catégorie : Chaîne du Froid & Sécurité
        FaqItem(
            id = "faq_cold_1",
            category = FaqCategory.COLD_CHAIN_SAFETY,
            question = "Comment est assurée la chaîne du froid (2°C - 8°C) pour l'insuline et les vaccins ?",
            summary = "Caissons isothermes rigides équipés d'accumulateurs de froid thermorégulés et sondes de température en continu.",
            detailedAnswer = listOf(
                "• Les médicaments thermosensibles (insulines, vaccins, sérums, hormones, collyres réfrigérés) sont conditionnés dans des sacoches isothermes dédiées.",
                "• La température est contrôlée en continu à 4.8°C (dans la plage réglementaire stricte de 2°C à 8°C).",
                "• Le coursier livre le colis dans un délai d'urgence garanti pour éviter tout choc thermique.",
                "• Un témoin de conformité thermique est apposé sur le scellé lors de la remise."
            ),
            keyAdvice = "À la réception de vos flacons ou stylos d'insuline, placez-les immédiatement au réfrigérateur (ne jamais congeler).",
            tag = "Chaîne du Froid 2-8°C",
            tagColor = SafeBlueSecondary,
            tagBg = SafeBlueLight,
            isImportant = true
        ),
        FaqItem(
            id = "faq_cold_2",
            category = FaqCategory.COLD_CHAIN_SAFETY,
            question = "Que faire en cas d'urgence la nuit ou les jours fériés ?",
            summary = "Activez le filtre 'Pharmacies de Garde 24h/24' sur l'application ou composez le numéro d'urgence SAMU 1515.",
            detailedAnswer = listOf(
                "• Activez le bouton orange 'Pharmacies de Garde' sur l'écran d'accueil ou dans le catalogue.",
                "• L'application filtre uniquement les officines officiellement désignées d'astreinte nocturne et de week-end à Dakar.",
                "• Les coursiers de nuit d'astreinte assurent la livraison en moins de 30 minutes à domicile.",
                "• En cas de malaise grave ou de détresse respiratoire/cardiaque, contactez sans attendre le SAMU National au 1515 ou les Sapeurs-Pompiers au 18."
            ),
            keyAdvice = "Notre plateforme actualise chaque semaine le tableau officiel des tours de garde des pharmacies de la région de Dakar.",
            tag = "Garde de Nuit 24h/24",
            tagColor = DutyPharmacyOrange,
            tagBg = DutyPharmacyBg,
            isImportant = true
        )
    )
}
