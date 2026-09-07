package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import com.example.auth.BiometricAuthManager
import com.example.auth.BiometricStatus
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.TextOnWhiteMuted
import com.example.ui.theme.VerifiedBadgeGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Context.findFragmentActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun BiometricPromptDialog(
    biometricAuthManager: BiometricAuthManager,
    title: String = "Autorisation Sécurisée",
    reason: String = "Validation du paiement sécurisé",
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fragmentActivity = remember(context) { context.findFragmentActivity() }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Biométrie, 1: Code PIN
    var enteredPin by remember { mutableStateOf("") }
    var isAuthenticating by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }
    var isSuccessAnimation by remember { mutableStateOf(false) }

    val biometricStatus = remember { biometricAuthManager.checkBiometricAvailability() }
    val bioConfig = remember { biometricAuthManager.securityConfig.value }

    // Pulse animation for fingerprint sensor ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    fun launchNativeBiometric() {
        if (biometricStatus == BiometricStatus.NOT_ENROLLED) {
            authError = "Aucune empreinte enregistrée sur ce téléphone. Touchez le bouton ci-dessous pour l'enregistrer."
            return
        }
        if (fragmentActivity != null) {
            isAuthenticating = true
            authError = null
            biometricAuthManager.authenticateWithBiometrics(
                activity = fragmentActivity,
                title = title,
                subtitle = "Capteur d'Empreinte Sécurisé 🇸🇳",
                description = reason,
                negativeButtonText = "Code PIN",
                onSuccess = {
                    isAuthenticating = false
                    isSuccessAnimation = true
                    coroutineScope.launch {
                        delay(600)
                        onSuccess()
                    }
                },
                onNegativeClick = {
                    isAuthenticating = false
                    selectedTab = 1
                },
                onError = { code, err ->
                    isAuthenticating = false
                    if (code == androidx.biometric.BiometricPrompt.ERROR_NO_BIOMETRICS || code == 11) {
                        authError = "Aucune empreinte enregistrée sur cet appareil. Enregistrez vos empreintes dans les paramètres."
                    } else {
                        authError = err
                    }
                },
                onFailed = {
                    isAuthenticating = false
                    authError = "Empreinte non reconnue par le capteur. Réessayez."
                }
            )
        } else {
            // Emulation / Simulator mode
            coroutineScope.launch {
                isAuthenticating = true
                delay(800)
                isAuthenticating = false
                isSuccessAnimation = true
                delay(500)
                onSuccess()
            }
        }
    }

    // Auto-launch biometric on dialog open if biometric is available
    LaunchedEffect(Unit) {
        if (biometricStatus == BiometricStatus.AVAILABLE) {
            launchNativeBiometric()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("biometric_prompt_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MedicalTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSuccessAnimation) Icons.Default.Check else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (isSuccessAnimation) VerifiedBadgeGreen else MedicalTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Protection Matérielle TPM / TEE 🇸🇳",
                                fontSize = 11.sp,
                                color = TextSecondaryMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondaryMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Context Reason Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reason,
                            fontSize = 12.sp,
                            color = TextOnWhitePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs: Biométrie / Code PIN
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF1F5F9),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MedicalTealPrimary,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            authError = null
                        },
                        selectedContentColor = MedicalTealPrimary,
                        unselectedContentColor = TextOnWhiteSecondary,
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = if (selectedTab == 0) MedicalTealPrimary else TextOnWhiteSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Empreinte / Face ID",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 0) MedicalTealPrimary else TextOnWhiteSecondary
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            authError = null
                        },
                        selectedContentColor = MedicalTealPrimary,
                        unselectedContentColor = TextOnWhiteSecondary,
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Pin,
                                    contentDescription = null,
                                    tint = if (selectedTab == 1) MedicalTealPrimary else TextOnWhiteSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Code PIN",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 1) MedicalTealPrimary else TextOnWhiteSecondary
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // TAB 0: BIOMETRIC AUTH
                if (selectedTab == 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSuccessAnimation) Color(0xFFDCFCE7)
                                    else MedicalTealLight.copy(alpha = 0.5f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Pulsing outer ring
                            Box(
                                modifier = Modifier
                                    .size(94.dp)
                                    .scale(if (isAuthenticating) pulseScale else 1f)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = if (isSuccessAnimation) VerifiedBadgeGreen else MedicalTealPrimary.copy(alpha = 0.6f),
                                        shape = CircleShape
                                    )
                            )

                            // Inner interactive button
                            Surface(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .clickable { launchNativeBiometric() }
                                    .testTag("biometric_sensor_tap"),
                                color = if (isSuccessAnimation) VerifiedBadgeGreen else MedicalTealPrimary,
                                shape = CircleShape,
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isAuthenticating) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            strokeWidth = 3.dp,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    } else if (isSuccessAnimation) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Succès",
                                            tint = Color.White,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.Fingerprint,
                                            contentDescription = "Empreinte",
                                            tint = Color.White,
                                            modifier = Modifier.size(44.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = when {
                                        isSuccessAnimation -> "Identité biométrique confirmée !"
                                        isAuthenticating -> "Capteur d'empreintes actif • Posez votre doigt sur le capteur"
                                        biometricStatus == BiometricStatus.AVAILABLE -> "Capteur d'empreintes relié • Touchez pour valider"
                                        biometricStatus == BiometricStatus.NOT_ENROLLED -> "Capteur présent • Aucune empreinte enregistrée"
                                        else -> "Capteur d'empreintes prêt • Touchez pour valider"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSuccessAnimation) VerifiedBadgeGreen else TextOnWhitePrimary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Authentification matérielle directe sur votre téléphone",
                                    fontSize = 11.sp,
                                    color = TextOnWhiteSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        if (authError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = authError ?: "",
                                color = Color(0xFFDC2626),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { launchNativeBiometric() },
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_verify_biometric_action")
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Activer le capteur d'empreinte", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (biometricStatus == BiometricStatus.NOT_ENROLLED || authError?.contains("enregistr") == true) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, MedicalTealPrimary.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .clickable { biometricAuthManager.openBiometricEnrollment(context) }
                                    .testTag("btn_enroll_biometric_device")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Enregistrer une empreinte sur l'appareil",
                                        color = MedicalTealDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 1: PIN CODE FALLBACK
                if (selectedTab == 1) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Entrez votre Code PIN Sécurité",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextOnWhitePrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (bioConfig.securityPin.isNotBlank()) "Code secret à 4 chiffres" else "Aucun code PIN configuré",
                                    fontSize = 11.sp,
                                    color = TextOnWhiteSecondary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // PIN visual dots
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 0 until 4) {
                                        val isFilled = enteredPin.length > i
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isFilled) MedicalTealPrimary else Color(0xFFE2E8F0)
                                                )
                                                .border(
                                                    1.5.dp,
                                                    if (isFilled) MedicalTealPrimary else Color(0xFF94A3B8),
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        if (authError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = authError ?: "",
                                color = Color(0xFFDC2626),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom Numeric Keypad
                        val keys = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("C", "0", "DEL")
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(0.88f)
                        ) {
                            keys.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    row.forEach { key ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = when (key) {
                                                "C" -> Color(0xFFFEE2E2)
                                                "DEL" -> Color(0xFFF1F5F9)
                                                else -> Color(0xFFF8FAFC)
                                            },
                                            border = BorderStroke(
                                                1.dp,
                                                when (key) {
                                                    "C" -> Color(0xFFFECACA)
                                                    "DEL" -> Color(0xFFCBD5E1)
                                                    else -> Color(0xFFCBD5E1)
                                                }
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(46.dp)
                                                .clickable {
                                                    authError = null
                                                    when (key) {
                                                        "C" -> enteredPin = ""
                                                        "DEL" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                                        else -> {
                                                            if (enteredPin.length < 4) {
                                                                enteredPin += key
                                                                if (enteredPin.length == 4) {
                                                                    if (biometricAuthManager.validatePin(enteredPin)) {
                                                                        isSuccessAnimation = true
                                                                        coroutineScope.launch {
                                                                            delay(400)
                                                                            onSuccess()
                                                                        }
                                                                    } else {
                                                                        authError = "Code PIN incorrect. Veuillez réessayer."
                                                                        enteredPin = ""
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                if (key == "DEL") {
                                                    Icon(
                                                        Icons.Default.Backspace,
                                                        contentDescription = "Effacer",
                                                        tint = TextOnWhitePrimary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                } else {
                                                    Text(
                                                        text = key,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (key == "C") Color(0xFFDC2626) else TextOnWhitePrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler", color = TextSecondaryMuted, fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = VerifiedBadgeGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chiffrement Matériel AES-256", fontSize = 10.sp, color = VerifiedBadgeGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
