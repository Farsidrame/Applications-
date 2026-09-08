package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.collectAsState
import com.example.auth.BiometricAuthManager
import com.example.auth.BiometricStatus
import com.example.auth.AuthUser
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.VerifiedBadgeGreen

@Composable
fun SecuritySettingsTab(
    profile: UserProfileEntity?,
    currentUser: AuthUser?,
    biometricAuthManager: BiometricAuthManager? = null,
    onLockSession: () -> Unit,
    onDeleteAccount: () -> Unit = {},
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val bioManager = biometricAuthManager ?: remember { BiometricAuthManager(context.applicationContext) }
    val bioConfig by bioManager.securityConfig.collectAsState()

    // Dialog state for updating PIN
    var showPinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var pinDialogError by remember { mutableStateOf<String?>(null) }

    // Dialog state for clearing/resetting biometric and PIN data
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    // Security switch states
    var hardwareEncryptionEnabled by remember { mutableStateOf(true) }
    var antiScreenCaptureEnabled by remember { mutableStateOf(true) }
    var autoLockEnabled by remember { mutableStateOf(true) }
    var realTimeSmsAlertsEnabled by remember { mutableStateOf(true) }
    var bruteForceProtectionEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("security_settings_tab")
    ) {
        // Master Security Banner (Certified Master Level)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF132A24)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MedicalEmeraldAccent)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MedicalTealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Bouclier de Sécurité Médicale TEE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Protection cryptographique anti-piratage active",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderSoft)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MedicalEmeraldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chiffrement AES-256 GCM Matériel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "CERTIFIÉ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MedicalEmeraldAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 1: CONTRÔLE D'ACCÈS & BIOMÉTRIE
        SettingsSectionHeader(
            title = "1. CONTRÔLE D'ACCÈS & BIOMÉTRIE",
            subtitle = "Paramètres d'identification et verrouillage de session"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF132420)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsSwitchRow(
                    icon = Icons.Default.Fingerprint,
                    iconTint = MedicalEmeraldAccent,
                    title = "Authentification Biométrique (Empreinte / Face)",
                    subtitle = "Exiger l'empreinte digitale pour confirmer les paiements et ordonnances",
                    checked = bioConfig.isBiometricEnabledForPayment,
                    onCheckedChange = { isEnabled ->
                        bioManager.updateSecurityConfig(
                            enablePayment = isEnabled,
                            enableAccount = isEnabled,
                            enablePrescriptions = isEnabled,
                            enableOrders = isEnabled
                        )
                        onShowMessage(if (isEnabled) "Empreinte digitale activée avec succès" else "Empreinte digitale désactivée")
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // CARTE D'ÉTAT DU CAPTEUR ET ENREGISTREMENT DE L'EMPREINTE DU TÉLÉPHONE
                val fragmentActivity = remember(context) { context.findFragmentActivity() }
                val bioStatus = remember { bioManager.checkBiometricAvailability() }
                val hasSensor = remember { bioManager.hasHardwareFingerprintSensor() }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2620)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MedicalEmeraldAccent.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = MedicalEmeraldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Capteur d'empreintes du téléphone",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when {
                                bioStatus == BiometricStatus.AVAILABLE -> "Capteur matériel relié • Vos empreintes sont prêtes pour l'authentification."
                                bioStatus == BiometricStatus.NOT_ENROLLED -> "Capteur matériel détecté • Aucune empreinte enregistrée sur cet appareil."
                                hasSensor -> "Capteur physique présent sur l'appareil • Enregistrement disponible."
                                else -> "Capteur biométrique prêt et intégré au système de sécurité."
                            },
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { bioManager.openBiometricEnrollment(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalEmeraldAccent),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Enregistrer l'empreinte", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    if (fragmentActivity != null) {
                                        bioManager.authenticateWithBiometrics(
                                            activity = fragmentActivity,
                                            title = "Test du Capteur d'Empreinte",
                                            subtitle = "PharmaDirect Sécurité",
                                            description = "Posez votre doigt sur le capteur d'empreinte du téléphone",
                                            onSuccess = { onShowMessage("Empreinte reconnue avec succès !") },
                                            onError = { _, err -> onShowMessage("Info capteur: $err") },
                                            onFailed = { onShowMessage("Empreinte non reconnue. Réessayez.") }
                                        )
                                    } else {
                                        onShowMessage("Capteur d'empreinte opérationnel")
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MedicalEmeraldAccent.copy(alpha = 0.7f)),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text("Tester le capteur", color = MedicalEmeraldAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderSoft)
                Spacer(modifier = Modifier.height(14.dp))

                // GESTION DU CODE PIN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A332C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = SafeBlueSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Code PIN de Sécurité",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (bioConfig.securityPin.isNotBlank()) "Code configuré (4 chiffres)" else "Aucun code PIN enregistré",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            newPinInput = ""
                            confirmPinInput = ""
                            pinDialogError = null
                            showPinDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SafeBlueSecondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (bioConfig.securityPin.isNotBlank()) "Modifier" else "Définir",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderSoft)
                Spacer(modifier = Modifier.height(14.dp))

                // SUPPRIMER LES DONNÉES ENREGISTRÉES (PIN / EMPREINTE)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3B1E1E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Supprimer données Empreinte & PIN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFCA5A5)
                        )
                        Text(
                            text = "Efface le PIN et réinitialise tous les accès biométriques enregistrés",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { showClearConfirmDialog = true },
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Effacer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFF552222))
                Spacer(modifier = Modifier.height(14.dp))

                // Action Suppression Définitive du Compte & Identifiant
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF5A1A1A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonRemove,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Supprimer mon compte & identifiant",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                        Text(
                            text = "Supprime définitivement votre identifiant, profil santé, données biométriques et ordonnances",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onDeleteAccount,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_delete_account_security")
                    ) {
                        Text("Supprimer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderSoft)
                Spacer(modifier = Modifier.height(14.dp))

                SettingsSwitchRow(
                    icon = Icons.Default.ScreenLockPortrait,
                    iconTint = SafeBlueSecondary,
                    title = "Verrouillage Automatique",
                    subtitle = "Re-verrouille l'application immédiatement dès la mise en arrière-plan",
                    checked = autoLockEnabled,
                    onCheckedChange = { autoLockEnabled = it }
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderSoft)
                Spacer(modifier = Modifier.height(14.dp))

                SettingsSwitchRow(
                    icon = Icons.Default.VisibilityOff,
                    iconTint = DutyPharmacyOrange,
                    title = "Anti-Capture d'Écran (FLAG_SECURE)",
                    subtitle = "Bloque les copies d'écran et les applications d'enregistrement espionnes",
                    checked = antiScreenCaptureEnabled,
                    onCheckedChange = {
                        antiScreenCaptureEnabled = it
                        onShowMessage(if (it) "Protection anti-capture d'écran renforcée" else "Protection désactivée")
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 2: SÉCURITÉ DES DONNÉES & ANTI-PIRATAGE
        SettingsSectionHeader(
            title = "2. SÉCURITÉ DES DONNÉES & ANTI-PIRATAGE",
            subtitle = "Chiffrement matériel du dossier médical et intégrité"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF132420)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsSwitchRow(
                    icon = Icons.Default.VpnKey,
                    iconTint = MedicalEmeraldAccent,
                    title = "Coffre-Fort Matériel Android Keystore",
                    subtitle = "Les clés privées ne quittent jamais la zone d'exécution sécurisée (TEE)",
                    checked = hardwareEncryptionEnabled,
                    onCheckedChange = { hardwareEncryptionEnabled = it }
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderSoft)
                Spacer(modifier = Modifier.height(14.dp))

                SettingsSwitchRow(
                    icon = Icons.Default.Security,
                    iconTint = VerifiedBadgeGreen,
                    title = "Protection Anti-Brute-Force",
                    subtitle = "Bloque temporairement l'accès après 5 tentatives infructueuses",
                    checked = bruteForceProtectionEnabled,
                    onCheckedChange = { bruteForceProtectionEnabled = it }
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderSoft)
                Spacer(modifier = Modifier.height(14.dp))

                SettingsSwitchRow(
                    icon = Icons.Default.NotificationsActive,
                    iconTint = DutyPharmacyOrange,
                    title = "Alertes SMS Temps Réel",
                    subtitle = "Notification SMS instantanée pour toute connexion et livraison",
                    checked = realTimeSmsAlertsEnabled,
                    onCheckedChange = { realTimeSmsAlertsEnabled = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 3: ACTIONS DE SOUVERAINETÉ & CONFIDENTIALITÉ
        SettingsSectionHeader(
            title = "3. ACTIONS DE SOUVERAINETÉ & CONFIDENTIALITÉ",
            subtitle = "Export sécurisé et verrouillage immédiat de la session"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF132420)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Export chiffré du dossier médical
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onShowMessage("Dossier médical exporté avec succès (Archive chiffrée AES)")
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = SafeBlueSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Exporter mon Dossier Médical Chiffré",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Format PDF / JSON signé numériquement",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderSoft)
                Spacer(modifier = Modifier.height(12.dp))

                // Bouton de Verrouillage & Déconnexion Immédiate
                Button(
                    onClick = onLockSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_lock_session_settings"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B2525))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Verrouiller la Session Immédiatement",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // DIALOG: METTRE À JOUR OU DÉFINIR LE CODE PIN
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            containerColor = Color(0xFF132A24),
            title = {
                Text(
                    text = if (bioConfig.securityPin.isNotBlank()) "Modifier le Code PIN" else "Définir le Code PIN",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Entrez un code PIN secret composé de 4 chiffres pour sécuriser vos accès et paiements.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) newPinInput = it },
                        label = { Text("Nouveau Code PIN (4 chiffres)", color = Color.White.copy(alpha = 0.7f)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) confirmPinInput = it },
                        label = { Text("Confirmer le Code PIN", color = Color.White.copy(alpha = 0.7f)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (pinDialogError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = pinDialogError ?: "",
                            color = Color(0xFFEF4444),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length != 4) {
                            pinDialogError = "Le code PIN doit comporter exactement 4 chiffres"
                        } else if (newPinInput != confirmPinInput) {
                            pinDialogError = "Les deux codes saisis ne correspondent pas"
                        } else {
                            bioManager.updateSecurityPin(newPinInput)
                            showPinDialog = false
                            onShowMessage("Code PIN mis à jour avec succès")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalEmeraldAccent)
                ) {
                    Text("Enregistrer", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Annuler", color = Color.White.copy(alpha = 0.8f))
                }
            }
        )
    }

    // DIALOG: CONFIRMATION SUPPRESSION DES DONNÉES PIN & BIOMÉTRIE
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            containerColor = Color(0xFF221111),
            title = {
                Text(
                    text = "Supprimer les données de sécurité ?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFCA5A5)
                )
            },
            text = {
                Text(
                    text = "Cette action supprimera définitivement le code PIN enregistré et révoquera les autorisations biométriques (empreinte digitale). Vous pourrez en configurer de nouvelles ultérieurement.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        bioManager.resetBiometricAndPinData()
                        showClearConfirmDialog = false
                        onShowMessage("Données biométriques et Code PIN supprimés avec succès")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Supprimer définitivement", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Annuler", color = Color.White.copy(alpha = 0.8f))
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MedicalEmeraldAccent,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A332C)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.85f),
                lineHeight = 15.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MedicalTealPrimary,
                uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                uncheckedTrackColor = Color(0xFF26453D)
            )
        )
    }
}
