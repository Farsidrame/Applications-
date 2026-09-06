package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.OrderEntity
import com.example.data.model.SmsDeliveryNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Service Android & Gestionnaire de Messagerie pour simuler et diffuser
 * des notifications SMS certifiées de livraison de médicaments.
 * Utilise les bibliothèques Android Messaging (NotificationCompat.MessagingStyle,
 * NotificationManagerCompat, SmsManager, Intent.ACTION_SENDTO).
 */
class SmsDeliveryNotificationService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val orderNumber = intent?.getStringExtra(EXTRA_ORDER_NUMBER) ?: "CMD-${(1000..9999).random()}"
        val pharmacyName = intent?.getStringExtra(EXTRA_PHARMACY_NAME) ?: "Grande Pharmacie Guigon (Dakar Plateau)"
        val recipientPhone = intent?.getStringExtra(EXTRA_RECIPIENT_PHONE) ?: ""
        val deliveryAddress = intent?.getStringExtra(EXTRA_DELIVERY_ADDRESS) ?: "Résidence Keur Gorgui, Dakar"
        val totalFcfa = intent?.getIntExtra(EXTRA_TOTAL_FCFA, 6500) ?: 6500
        val paymentMethod = intent?.getStringExtra(EXTRA_PAYMENT_METHOD) ?: "Wave Mobile Money"
        val courierName = intent?.getStringExtra(EXTRA_COURIER_NAME) ?: "Mamadou Ndiaye"
        val delayMillis = intent?.getLongExtra(EXTRA_DELAY_MILLIS, 0L) ?: 0L
        val scenarioName = intent?.getStringExtra(EXTRA_SCENARIO) ?: DeliveryScenario.STANDARD_DELIVERED.name

        serviceScope.launch {
            if (delayMillis > 0) {
                delay(delayMillis)
            }
            val scenario = try {
                DeliveryScenario.valueOf(scenarioName)
            } catch (e: Exception) {
                DeliveryScenario.STANDARD_DELIVERED
            }

            dispatchSmsNotification(
                context = applicationContext,
                orderNumber = orderNumber,
                pharmacyName = pharmacyName,
                recipientPhone = recipientPhone,
                deliveryAddress = deliveryAddress,
                totalFcfa = totalFcfa,
                paymentMethod = paymentMethod,
                courierName = courierName,
                scenario = scenario
            )
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    enum class DeliveryScenario(
        val title: String,
        val subtitle: String,
        val defaultSender: String,
        val operatorNetwork: String
    ) {
        STANDARD_DELIVERED(
            title = "Livraison Médicaments Confirmée",
            subtitle = "Colis remis en main propre",
            defaultSender = "PHARMADIRECT-SN",
            operatorNetwork = "Orange Sénégal / Sonatel"
        ),
        COLD_CHAIN_DELIVERED(
            title = "Livraison Chaîne du Froid Certifiée (4.8°C)",
            subtitle = "Insuline & Thermosensibles scellés",
            defaultSender = "PHARMA-COLDCHAIN-SN",
            operatorNetwork = "Wave SMS Gateway"
        ),
        NIGHT_DUTY_DELIVERED(
            title = "Livraison Pharmacie de Garde 24h/24",
            subtitle = "Dispensé en urgence nocturne",
            defaultSender = "GARDE-PHARMA-SN",
            operatorNetwork = "Free Sénégal Mobile"
        ),
        PRESCRIPTION_VALIDATED(
            title = "Ordonnance Validée par le Pharmacien",
            subtitle = "Préparation officinale prête",
            defaultSender = "DOCTEUR-PHARMA-SN",
            operatorNetwork = "Expresso Telecom SN"
        ),
        INVOICE_AND_PAYMENT(
            title = "Reçu & Facture Certifiée",
            subtitle = "Paiement numérique consigné",
            defaultSender = "FACTURE-PHARMA-SN",
            operatorNetwork = "Orange Money / Wave Gateway"
        )
    }

    companion object {
        const val CHANNEL_ID = "pharmacy_sms_delivery_channel"
        const val CHANNEL_NAME = "SMS & Confirmations de Livraison Pharmaceutique"
        const val CHANNEL_DESC = "Notifications et SMS prioritaires de confirmation de livraison de médicaments"

        const val ACTION_SIMULATE_DELIVERY = "com.example.action.SIMULATE_DELIVERY_SMS"
        const val EXTRA_ORDER_NUMBER = "extra_order_number"
        const val EXTRA_PHARMACY_NAME = "extra_pharmacy_name"
        const val EXTRA_RECIPIENT_PHONE = "extra_recipient_phone"
        const val EXTRA_DELIVERY_ADDRESS = "extra_delivery_address"
        const val EXTRA_TOTAL_FCFA = "extra_total_fcfa"
        const val EXTRA_PAYMENT_METHOD = "extra_payment_method"
        const val EXTRA_COURIER_NAME = "extra_courier_name"
        const val EXTRA_DELAY_MILLIS = "extra_delay_millis"
        const val EXTRA_SCENARIO = "extra_scenario"

        fun ensureNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC
                    enableLights(true)
                    lightColor = android.graphics.Color.parseColor("#00875A")
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 250, 150, 250)
                    setShowBadge(true)
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        /**
         * Déclenche une notification système Android au format SMS (MessagingStyle / BigTextStyle)
         * et retourne l'entité SmsDeliveryNotification pour l'historique dans l'application.
         */
        fun dispatchSmsNotification(
            context: Context,
            orderNumber: String,
            pharmacyName: String,
            recipientPhone: String,
            deliveryAddress: String,
            totalFcfa: Int,
            paymentMethod: String,
            courierName: String,
            scenario: DeliveryScenario = DeliveryScenario.STANDARD_DELIVERED
        ): SmsDeliveryNotification {
            ensureNotificationChannel(context)

            val now = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date())
            val messageText = buildSmsMessageBody(
                scenario = scenario,
                orderNumber = orderNumber,
                pharmacyName = pharmacyName,
                deliveryAddress = deliveryAddress,
                totalFcfa = totalFcfa,
                paymentMethod = paymentMethod,
                courierName = courierName
            )

            val smsNotification = SmsDeliveryNotification(
                id = UUID.randomUUID().toString(),
                orderId = orderNumber,
                orderNumber = orderNumber,
                sender = scenario.defaultSender,
                recipientPhone = recipientPhone,
                messageText = messageText,
                timestamp = now,
                pharmacyName = pharmacyName,
                isRead = false
            )

            // Intent vers MainActivity
            val appIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingAppIntent = PendingIntent.getActivity(
                context,
                orderNumber.hashCode(),
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Intent direct vers l'application SMS native Android
            val smsAppIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$recipientPhone")
                putExtra("sms_body", messageText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val pendingSmsAppIntent = PendingIntent.getActivity(
                context,
                (orderNumber + "_sms").hashCode(),
                smsAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Personnes pour MessagingStyle
            val senderPerson = Person.Builder()
                .setName(scenario.defaultSender)
                .setKey("pharma_sender")
                .setBot(true)
                .build()

            val userPerson = Person.Builder()
                .setName("Patient / Client")
                .setKey("patient_user")
                .build()

            val messagingStyle = NotificationCompat.MessagingStyle(userPerson)
                .setConversationTitle("💬 ${scenario.title} • $pharmacyName")
                .addMessage(
                    NotificationCompat.MessagingStyle.Message(
                        messageText,
                        System.currentTimeMillis(),
                        senderPerson
                    )
                )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle("📩 SMS [${scenario.defaultSender}] : ${scenario.title}")
                .setContentText("Commande $orderNumber livrée avec succès par $courierName")
                .setStyle(messagingStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(android.graphics.Color.parseColor("#00875A"))
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 250, 150, 250))
                .setContentIntent(pendingAppIntent)
                .addAction(
                    android.R.drawable.sym_action_chat,
                    "📱 Ouvrir dans l'app SMS",
                    pendingSmsAppIntent
                )
                .addAction(
                    android.R.drawable.ic_menu_view,
                    "📄 Consulter dans l'App",
                    pendingAppIntent
                )

            try {
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(orderNumber.hashCode(), notificationBuilder.build())
            } catch (e: SecurityException) {
                Log.w("SmsService", "Permission POST_NOTIFICATIONS requise pour afficher la notification système", e)
            } catch (e: Exception) {
                Log.e("SmsService", "Erreur lors de l'envoi de la notification", e)
            }

            // Tentative d'utilisation de SmsManager Android pour simulation réseau
            simulateTelephonySmsDispatch(recipientPhone, messageText)

            return smsNotification
        }

        /**
         * Simule ou utilise SmsManager de la pile de téléphonie Android.
         */
        private fun simulateTelephonySmsDispatch(recipientPhone: String, message: String) {
            try {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Modern Android 12+ API
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                // En mode simulation ou avec cartes SIM virtuelles, SmsManager est prêt
                val parts = smsManager.divideMessage(message)
                Log.d("SmsDeliveryService", "Message découpé en ${parts.size} segments SMS pour $recipientPhone via Android Telephony.")
            } catch (e: Exception) {
                Log.d("SmsDeliveryService", "Simulation SMS effectuée sans carte SIM physique: ${e.message}")
            }
        }

        fun triggerFromOrder(
            context: Context,
            order: OrderEntity,
            scenario: DeliveryScenario = DeliveryScenario.STANDARD_DELIVERED
        ): SmsDeliveryNotification {
            return dispatchSmsNotification(
                context = context,
                orderNumber = order.orderNumber,
                pharmacyName = order.pharmacyName,
                recipientPhone = order.patientPhone,
                deliveryAddress = order.deliveryAddress,
                totalFcfa = order.totalFcfa,
                paymentMethod = order.paymentMethod,
                courierName = order.courierName,
                scenario = scenario
            )
        }

        fun startDelayedSimulationService(
            context: Context,
            order: OrderEntity,
            delaySeconds: Int = 3,
            scenario: DeliveryScenario = DeliveryScenario.STANDARD_DELIVERED
        ) {
            val intent = Intent(context, SmsDeliveryNotificationService::class.java).apply {
                action = ACTION_SIMULATE_DELIVERY
                putExtra(EXTRA_ORDER_NUMBER, order.orderNumber)
                putExtra(EXTRA_PHARMACY_NAME, order.pharmacyName)
                putExtra(EXTRA_RECIPIENT_PHONE, order.patientPhone)
                putExtra(EXTRA_DELIVERY_ADDRESS, order.deliveryAddress)
                putExtra(EXTRA_TOTAL_FCFA, order.totalFcfa)
                putExtra(EXTRA_PAYMENT_METHOD, order.paymentMethod)
                putExtra(EXTRA_COURIER_NAME, order.courierName)
                putExtra(EXTRA_DELAY_MILLIS, (delaySeconds * 1000).toLong())
                putExtra(EXTRA_SCENARIO, scenario.name)
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                Log.e("SmsDeliveryService", "Impossible de démarrer le service en arrière-plan", e)
            }
        }

        fun openNativeSmsMessenger(context: Context, recipientPhone: String, messageText: String) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$recipientPhone")
                putExtra("sms_body", messageText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Ouverture de la messagerie SMS...", Toast.LENGTH_SHORT).show()
            }
        }

        private fun buildSmsMessageBody(
            scenario: DeliveryScenario,
            orderNumber: String,
            pharmacyName: String,
            deliveryAddress: String,
            totalFcfa: Int,
            paymentMethod: String,
            courierName: String
        ): String {
            return when (scenario) {
                DeliveryScenario.STANDARD_DELIVERED -> {
                    """
                    🏥 PHARMADIRECT SÉNÉGAL [SMS DE LIVRAISON] :
                    Votre commande de médicaments ($orderNumber) auprès de "$pharmacyName" a été remise avec succès en main propre à "$deliveryAddress".
                    
                    • Montant réglé : $totalFcfa FCFA ($paymentMethod)
                    • Livreur certifié : $courierName
                    • Facture certifiée & traçabilité disponibles dans l'application.
                    
                    Service Client 24/7 & Urgences disponibles via l'application.
                    """.trimIndent()
                }
                DeliveryScenario.COLD_CHAIN_DELIVERED -> {
                    """
                    ❄️ PHARMADIRECT - CONTRÔLE QUALITÉ THERMIQUE VALIDÉ :
                    Médicaments thermosensibles ($orderNumber) livrés à "$deliveryAddress".
                    
                    • Température certifiée : 4.8°C (Chaîne du froid respectée 2°C - 8°C)
                    • Scellé d'inviolabilité : Conforme #SN-8921
                    • Délivré par : $pharmacyName
                    • Livreur : $courierName (Sac isotherme thermorégulé)
                    
                    Conservez immédiatement vos médicaments au réfrigérateur.
                    """.trimIndent()
                }
                DeliveryScenario.NIGHT_DUTY_DELIVERED -> {
                    """
                    🌙 PHARMACIE DE GARDE SÉNÉGAL [URGENCE 24H/24] :
                    Vos médicaments de garde ($orderNumber) ont été délivrés en express par "$pharmacyName".
                    
                    • Adresse de remise : $deliveryAddress
                    • Règlement sécurisé : $totalFcfa FCFA ($paymentMethod)
                    • Livreur de nuit d'astreinte : $courierName
                    
                    En cas de crise ou d'effet indésirable : SAMU National 1515.
                    """.trimIndent()
                }
                DeliveryScenario.PRESCRIPTION_VALIDATED -> {
                    """
                    💊 DOCTEUR EN PHARMACIE - ORDONNANCE VALIDÉE :
                    Votre ordonnance ($orderNumber) a été contrôlée et validée par le pharmacien titulaire de "$pharmacyName".
                    
                    • Préparation officinale : Terminée et scellée
                    • Posologies et contre-indications : Vérifiées
                    • Départ en livraison vers : $deliveryAddress
                    """.trimIndent()
                }
                DeliveryScenario.INVOICE_AND_PAYMENT -> {
                    """
                    🧾 REÇU DE PAIEMENT & FACTURE OFFICIELLE :
                    Paiement de $totalFcfa FCFA validé pour la commande $orderNumber ($pharmacyName).
                    
                    • Mode : $paymentMethod
                    • Statut : Fonds sécurisés et consignés
                    • Téléchargez votre facture conforme dans votre profil PharmaDirect.
                    """.trimIndent()
                }
            }
        }
    }
}
