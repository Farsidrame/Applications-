package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.example.data.model.PaymentMethod
import com.example.ui.theme.OrangeMoneyColor
import com.example.ui.theme.WaveBlueColor
import com.example.ui.theme.MtnMomoYellow
import com.example.ui.theme.VisaBlueColor

/**
 * Informations de configuration et intégration des applications d'opérateurs de paiement mobile.
 */
data class PaymentOperatorAppConfig(
    val paymentMethod: PaymentMethod,
    val operatorName: String,
    val shortName: String,
    val packageCandidates: List<String>,
    val deepLinkPrefix: String?,
    val webCheckoutUrl: String,
    val ussdCode: String?,
    val brandColor: Color,
    val codeInputLabel: String,
    val codePlaceholder: String,
    val inAppInstruction: String,
    val byCodeInstruction: String
)

sealed class PaymentLaunchResult {
    data class Success(val appName: String, val packageName: String, val message: String) : PaymentLaunchResult()
    data class AppNotInstalled(val appName: String, val fallbackPackage: String, val message: String) : PaymentLaunchResult()
    data class FallbackUrlOpened(val appName: String, val url: String, val message: String) : PaymentLaunchResult()
    data class Error(val message: String) : PaymentLaunchResult()
}

/**
 * Gestionnaire intelligent des ouvertures d'applications de paiement et des validations (Wave, Orange Money, MoMo).
 */
object MobilePaymentLauncher {

    // Liste des configurations d'opérateurs
    val operators: Map<PaymentMethod, PaymentOperatorAppConfig> = mapOf(
        PaymentMethod.WAVE to PaymentOperatorAppConfig(
            paymentMethod = PaymentMethod.WAVE,
            operatorName = "Wave Mobile Money",
            shortName = "Wave",
            packageCandidates = listOf(
                "com.wave.personal",
                "com.wave.business"
            ),
            deepLinkPrefix = "wave://checkout",
            webCheckoutUrl = "https://pay.wave.com/m/pharmadirect_sn",
            ussdCode = null, // Wave est 100% digital / app-based
            brandColor = WaveBlueColor,
            codeInputLabel = "Code secret ou OTP Wave",
            codePlaceholder = "Code à 4 ou 6 chiffres",
            inAppInstruction = "L'application Wave s'ouvre pour valider instantanément la transaction sans aucun frais (0%).",
            byCodeInstruction = "Saisissez le code secret temporaire de débit reçu par SMS ou notification push Wave."
        ),
        PaymentMethod.ORANGE_MONEY to PaymentOperatorAppConfig(
            paymentMethod = PaymentMethod.ORANGE_MONEY,
            operatorName = "Orange Money Sénégal",
            shortName = "Orange Money",
            packageCandidates = listOf(
                "com.orange.sn.orangemoneysn",
                "com.orange.orangemoneyafrique",
                "com.orange.omservices",
                "com.orange.maxit.sn"
            ),
            deepLinkPrefix = "orangemoney://checkout",
            webCheckoutUrl = "https://pay.orange-money.sn/checkout",
            ussdCode = "#144#391#", // Code USSD marchand Sénégal pour générer un code d'autorisation
            brandColor = OrangeMoneyColor,
            codeInputLabel = "Code d'autorisation Orange Money",
            codePlaceholder = "Code d'autorisation (ex: 4 chiffres)",
            inAppInstruction = "L'application Orange Money (ou Max it) s'ouvre pour confirmer le paiement sécurisé.",
            byCodeInstruction = "Composez le #144#391# sur votre téléphone pour générer votre code d'autorisation puis saisissez-le ici."
        ),
        PaymentMethod.MTN_MOMO to PaymentOperatorAppConfig(
            paymentMethod = PaymentMethod.MTN_MOMO,
            operatorName = "MTN Mobile Money",
            shortName = "MTN MoMo",
            packageCandidates = listOf(
                "com.mtn.momo",
                "com.momo.agent"
            ),
            deepLinkPrefix = "momo://pay",
            webCheckoutUrl = "https://pay.mtn.com/momo/checkout",
            ussdCode = "*133#",
            brandColor = MtnMomoYellow,
            codeInputLabel = "Code secret OTP MTN MoMo",
            codePlaceholder = "Code secret temporaire",
            inAppInstruction = "L'application MTN MoMo s'ouvre pour valider le transfert vers PharmaDirect.",
            byCodeInstruction = "Saisissez le code OTP reçu par SMS ou généré via le menu *133#."
        )
    )

    /**
     * Vérifie si un package spécifique est présent sur le téléphone.
     */
    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Recherche le premier package installé parmi une liste de candidats.
     */
    fun findInstalledPackage(context: Context, candidates: List<String>): String? {
        val pm = context.packageManager
        for (pkg in candidates) {
            if (isPackageInstalled(context, pkg)) {
                return pkg
            }
            // Essayer également via getLaunchIntentForPackage
            if (pm.getLaunchIntentForPackage(pkg) != null) {
                return pkg
            }
        }
        return null
    }

    /**
     * Vérifie si l'application officielle de l'opérateur est installée sur le téléphone.
     */
    fun isOperatorAppInstalled(context: Context, method: PaymentMethod): Boolean {
        val config = operators[method] ?: return false
        return findInstalledPackage(context, config.packageCandidates) != null
    }

    /**
     * Renvoie le package installé pour un moyen de paiement, ou le premier candidat si non installé.
     */
    fun getPrimaryPackage(context: Context, method: PaymentMethod): String {
        val config = operators[method]
            ?: return if (method == PaymentMethod.WAVE) "com.wave.personal" else "com.orange.sn.orangemoneysn"
        return findInstalledPackage(context, config.packageCandidates)
            ?: config.packageCandidates.firstOrNull()
            ?: "com.wave.personal"
    }

    /**
     * Lance l'application officielle de l'opérateur installée sur le téléphone,
     * ou propose une alternative transparente si non installée.
     */
    fun launchOperatorApp(
        context: Context,
        method: PaymentMethod,
        amountFcfa: Int,
        orderRef: String = "CMD-${System.currentTimeMillis() % 100000}",
        clientPhone: String = "",
        notifyUser: Boolean = true
    ): PaymentLaunchResult {
        val config = operators[method] ?: return PaymentLaunchResult.Error("Moyen de paiement non pris en charge pour ouverture directe")
        val pm = context.packageManager

        // 1. Chercher le package installé
        val installedPackage = findInstalledPackage(context, config.packageCandidates)

        if (installedPackage != null) {
            try {
                // Tenter un Intent direct pour lancer l'application installée
                val launchIntent = pm.getLaunchIntentForPackage(installedPackage)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    // Passer les paramètres utiles de paiement dans l'intent
                    launchIntent.putExtra("AMOUNT", amountFcfa)
                    launchIntent.putExtra("CURRENCY", "XOF")
                    launchIntent.putExtra("ORDER_REF", orderRef)
                    launchIntent.putExtra("MERCHANT", "PharmaDirect Sénégal")
                    if (clientPhone.isNotBlank()) {
                        launchIntent.putExtra("CLIENT_PHONE", clientPhone)
                    }

                    context.startActivity(launchIntent)

                    if (notifyUser) {
                        Toast.makeText(
                            context,
                            "Ouverture de ${config.operatorName} pour validation ($amountFcfa FCFA)...",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    return PaymentLaunchResult.Success(
                        appName = config.operatorName,
                        packageName = installedPackage,
                        message = "Application ${config.operatorName} ouverte avec succès sur votre téléphone."
                    )
                }
            } catch (e: Exception) {
                // Échec du launch direct, essayer via deep link
            }
        }

        // 2. Si non installé ou si l'intent direct a échoué, tenter le Deep Link / Universal Link
        try {
            val deepLinkUri = when (method) {
                PaymentMethod.WAVE -> {
                    // Universal link Wave : ouvre l'application Wave si installée, sinon portail web Wave
                    Uri.parse("https://pay.wave.com/m/pharmadirect_sn?amount=$amountFcfa&ref=$orderRef")
                }
                PaymentMethod.ORANGE_MONEY -> {
                    Uri.parse("https://pay.orange-money.sn/checkout?merchant=PHARMADIRECT&amt=$amountFcfa&order=$orderRef")
                }
                PaymentMethod.MTN_MOMO -> {
                    Uri.parse("https://pay.mtn.com/momo/checkout?merchant=PHARMADIRECT&amt=$amountFcfa&ref=$orderRef")
                }
                else -> Uri.parse(config.webCheckoutUrl)
            }

            val viewIntent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Vérifier s'il existe une activité capable de gérer ce lien
            val activities = pm.queryIntentActivities(viewIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (activities.isNotEmpty()) {
                context.startActivity(viewIntent)
                return PaymentLaunchResult.FallbackUrlOpened(
                    appName = config.operatorName,
                    url = deepLinkUri.toString(),
                    message = "Passerelle ${config.operatorName} ouverte dans votre navigateur ou application."
                )
            }
        } catch (_: Exception) {
            // Ignorer
        }

        // 3. Si l'application n'est pas installée sur le téléphone
        val defaultPkg = config.packageCandidates.firstOrNull() ?: "com.wave.personal"
        return PaymentLaunchResult.AppNotInstalled(
            appName = config.operatorName,
            fallbackPackage = defaultPkg,
            message = "L'application ${config.operatorName} n'est pas encore installée sur votre appareil. Vous pouvez la télécharger sur Google Play ou valider directement par code."
        )
    }

    /**
     * Ouvre la fiche de l'application sur le Google Play Store pour téléchargement.
     */
    fun openPlayStore(context: Context, packageName: String) {
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (_: Exception) {
            try {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Impossible d'ouvrir Google Play Store", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Ouvre le composeur téléphonique avec le code USSD (ex: #144#391# pour Orange Money).
     */
    fun openUssdDialer(context: Context, ussdCode: String) {
        try {
            // Encoder le caractère # pour l'Uri tel
            val encodedCode = Uri.encode(ussdCode)
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$encodedCode")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(dialIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Impossible d'ouvrir le composeur : $ussdCode", Toast.LENGTH_SHORT).show()
        }
    }
}
