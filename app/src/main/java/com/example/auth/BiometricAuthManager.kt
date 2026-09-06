package com.example.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executor

enum class BiometricStatus {
    AVAILABLE,
    NOT_ENROLLED,
    NO_HARDWARE,
    HW_UNAVAILABLE,
    UNSUPPORTED
}

enum class BiometricType {
    FINGERPRINT,
    FACE,
    IRIS,
    MULTIPLE,
    PIN_ONLY
}

data class BiometricSecurityConfig(
    val isBiometricEnabledForPayment: Boolean = false,
    val isBiometricEnabledForAccount: Boolean = false,
    val isBiometricEnabledForPrescriptions: Boolean = false,
    val isBiometricEnabledForOrders: Boolean = false,
    val securityPin: String = "",
    val isPinRequiredAsFallback: Boolean = false,
    val biometricType: BiometricType = BiometricType.FINGERPRINT,
    val lastAuthTimestamp: Long = 0L
)

class BiometricAuthManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("pharma_biometric_prefs", Context.MODE_PRIVATE)

    private val _securityConfig = MutableStateFlow(loadConfig())
    val securityConfig: StateFlow<BiometricSecurityConfig> = _securityConfig.asStateFlow()

    private val _isBiometricLocked = MutableStateFlow(false)
    val isBiometricLocked: StateFlow<Boolean> = _isBiometricLocked.asStateFlow()

    private fun loadConfig(): BiometricSecurityConfig {
        return BiometricSecurityConfig(
            isBiometricEnabledForPayment = prefs.getBoolean("bio_payment_enabled", false),
            isBiometricEnabledForAccount = prefs.getBoolean("bio_account_enabled", false),
            isBiometricEnabledForPrescriptions = prefs.getBoolean("bio_rx_enabled", false),
            isBiometricEnabledForOrders = prefs.getBoolean("bio_orders_enabled", false),
            securityPin = prefs.getString("security_pin", "") ?: "",
            isPinRequiredAsFallback = prefs.getBoolean("pin_fallback_enabled", false),
            biometricType = detectBiometricType()
        )
    }

    private fun saveConfig(config: BiometricSecurityConfig) {
        prefs.edit()
            .putBoolean("bio_payment_enabled", config.isBiometricEnabledForPayment)
            .putBoolean("bio_account_enabled", config.isBiometricEnabledForAccount)
            .putBoolean("bio_rx_enabled", config.isBiometricEnabledForPrescriptions)
            .putBoolean("bio_orders_enabled", config.isBiometricEnabledForOrders)
            .putString("security_pin", config.securityPin)
            .putBoolean("pin_fallback_enabled", config.isPinRequiredAsFallback)
            .apply()
        _securityConfig.value = config
    }

    fun updateSecurityConfig(
        enablePayment: Boolean? = null,
        enableAccount: Boolean? = null,
        enablePrescriptions: Boolean? = null,
        enableOrders: Boolean? = null,
        newPin: String? = null
    ) {
        val current = _securityConfig.value
        val updated = current.copy(
            isBiometricEnabledForPayment = enablePayment ?: current.isBiometricEnabledForPayment,
            isBiometricEnabledForAccount = enableAccount ?: current.isBiometricEnabledForAccount,
            isBiometricEnabledForPrescriptions = enablePrescriptions ?: current.isBiometricEnabledForPrescriptions,
            isBiometricEnabledForOrders = enableOrders ?: current.isBiometricEnabledForOrders,
            securityPin = newPin ?: current.securityPin
        )
        saveConfig(updated)
    }

    /**
     * Supprimer toutes les données enregistrées d'identification biométrique et réinitialiser le code PIN
     */
    fun resetBiometricAndPinData() {
        prefs.edit().clear().apply()
        _securityConfig.value = BiometricSecurityConfig(
            isBiometricEnabledForPayment = false,
            isBiometricEnabledForAccount = false,
            isBiometricEnabledForPrescriptions = false,
            isBiometricEnabledForOrders = false,
            securityPin = "",
            isPinRequiredAsFallback = false,
            biometricType = detectBiometricType(),
            lastAuthTimestamp = 0L
        )
    }

    /**
     * Mettre à jour le code PIN de sécurité
     */
    fun updateSecurityPin(newPin: String) {
        val current = _securityConfig.value
        val updated = current.copy(
            securityPin = newPin.trim(),
            isPinRequiredAsFallback = newPin.isNotBlank()
        )
        saveConfig(updated)
    }

    fun checkBiometricAvailability(): BiometricStatus {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BIOMETRIC_STRONG or BIOMETRIC_WEAK
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HW_UNAVAILABLE
            else -> BiometricStatus.UNSUPPORTED
        }
    }

    fun hasHardwareFingerprintSensor(): Boolean {
        return context.packageManager.hasSystemFeature("android.hardware.fingerprint")
    }

    /**
     * Ouvre directement la page d'enregistrement des empreintes digitales dans les paramètres du téléphone Android
     */
    fun openBiometricEnrollment(targetContext: Context? = null) {
        val ctx = targetContext ?: context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or BIOMETRIC_WEAK
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                ctx.startActivity(enrollIntent)
                return
            } catch (e: Exception) {
                Log.w("BiometricAuth", "ACTION_BIOMETRIC_ENROLL non supporté, essai des paramètres de sécurité", e)
            }
        }

        try {
            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            ctx.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                ctx.startActivity(intent)
            } catch (e2: Exception) {
                Log.e("BiometricAuth", "Impossible d'ouvrir les paramètres du téléphone", e2)
            }
        }
    }

    fun detectBiometricType(): BiometricType {
        val pm = context.packageManager
        val hasFingerprint = pm.hasSystemFeature("android.hardware.fingerprint")
        val hasFace = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pm.hasSystemFeature("android.hardware.biometrics.face")
        } else false
        val hasIris = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pm.hasSystemFeature("android.hardware.biometrics.iris")
        } else false

        return when {
            hasFingerprint && hasFace -> BiometricType.MULTIPLE
            hasFace -> BiometricType.FACE
            hasIris -> BiometricType.IRIS
            hasFingerprint -> BiometricType.FINGERPRINT
            else -> BiometricType.FINGERPRINT // Default fallback representation
        }
    }

    /**
     * Authenticate using native AndroidX BiometricPrompt.
     */
    fun authenticateWithBiometrics(
        activity: FragmentActivity,
        title: String = "Vérification Biométrique Sécurisée",
        subtitle: String = "PharmaDirect Sénégal 🇸🇳",
        description: String = "Confirmez votre identité avec votre empreinte digitale ou votre visage",
        negativeButtonText: String = "Utiliser mon Code PIN",
        onSuccess: () -> Unit,
        onNegativeClick: () -> Unit = {},
        onError: (errorCode: Int, errString: String) -> Unit = { _, _ -> },
        onFailed: () -> Unit = {}
    ) {
        val executor: Executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("BiometricAuth", "Authentication succeeded!")
                    recordSuccess()
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.w("BiometricAuth", "Auth error: $errorCode - $errString")
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        onNegativeClick()
                    } else {
                        onError(errorCode, errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.w("BiometricAuth", "Auth failed (fingerprint unrecognised)")
                    onFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
            .setConfirmationRequired(true)
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Log.e("BiometricAuth", "Failed to launch biometric prompt: ${e.message}", e)
            onError(-1, e.localizedMessage ?: "Erreur biométrique")
        }
    }

    /**
     * Validate PIN fallback
     */
    fun validatePin(pin: String): Boolean {
        val configuredPin = _securityConfig.value.securityPin
        if (configuredPin.isBlank()) return false
        val isValid = pin.trim() == configuredPin
        if (isValid) {
            recordSuccess()
        }
        return isValid
    }

    private fun recordSuccess() {
        val updated = _securityConfig.value.copy(lastAuthTimestamp = System.currentTimeMillis())
        _securityConfig.value = updated
    }
}
