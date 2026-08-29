package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.InitialData
import com.example.data.local.PharmaDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.CourierChatMessage
import com.example.data.model.DeliveryAddressEntity
import com.example.data.model.LiveCourierTelemetry
import com.example.data.model.LiveOrderEvent
import com.example.data.model.Medicine
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.Pharmacy
import com.example.data.model.PrescriptionEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.PharmaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface PaymentProcessState {
    object Idle : PaymentProcessState
    data class Processing(val method: PaymentMethod, val stepMessage: String) : PaymentProcessState
    data class Success(val order: OrderEntity) : PaymentProcessState
    data class Error(val message: String) : PaymentProcessState
}

class PharmaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PharmaRepository

    init {
        val dao = PharmaDatabase.getDatabase(application).pharmaDao()
        repository = PharmaRepository(dao)
        seedDefaultsIfEmpty()
        observeProfileAndAddresses()
    }

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tous")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedRegion = MutableStateFlow("Toutes les régions")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

    private val _dutyOnlyFilter = MutableStateFlow(false)
    val dutyOnlyFilter: StateFlow<Boolean> = _dutyOnlyFilter.asStateFlow()

    private val _selectedMedicine = MutableStateFlow<Medicine?>(null)
    val selectedMedicine: StateFlow<Medicine?> = _selectedMedicine.asStateFlow()

    private val _selectedPharmacy = MutableStateFlow<Pharmacy?>(null)
    val selectedPharmacy: StateFlow<Pharmacy?> = _selectedPharmacy.asStateFlow()

    private val _activeOrder = MutableStateFlow<OrderEntity?>(null)
    val activeOrder: StateFlow<OrderEntity?> = _activeOrder.asStateFlow()

    private val _isPrescriptionAttachedToCart = MutableStateFlow(false)
    val isPrescriptionAttachedToCart: StateFlow<Boolean> = _isPrescriptionAttachedToCart.asStateFlow()

    private val _attachedPrescriptionSummary = MutableStateFlow<String?>(null)
    val attachedPrescriptionSummary: StateFlow<String?> = _attachedPrescriptionSummary.asStateFlow()

    private val _paymentState = MutableStateFlow<PaymentProcessState>(PaymentProcessState.Idle)
    val paymentState: StateFlow<PaymentProcessState> = _paymentState.asStateFlow()

    // Default User Delivery Details
    val userDeliveryAddress = MutableStateFlow("Résidence Keur Gorgui, Immeuble B, Appt 42, Dakar")
    val userName = MutableStateFlow("Mamadou Dramé")
    val userPhone = MutableStateFlow("+221 77 654 32 10")

    // Real-Time Courier Telemetry & Live Tracking Engine
    private val _liveTelemetry = MutableStateFlow(LiveCourierTelemetry())
    val liveTelemetry: StateFlow<LiveCourierTelemetry> = _liveTelemetry.asStateFlow()

    private val _liveEventsLog = MutableStateFlow<List<LiveOrderEvent>>(emptyList())
    val liveEventsLog: StateFlow<List<LiveOrderEvent>> = _liveEventsLog.asStateFlow()

    private val _courierChatMessages = MutableStateFlow<List<CourierChatMessage>>(
        listOf(
            CourierChatMessage(
                id = "msg_1",
                sender = "courier",
                senderName = "Mamadou Ndiaye (Livreur)",
                text = "Bonjour ! J'ai récupéré votre ordonnance et vos médicaments à la pharmacie dans un sac isotherme scellé. Je prends la route vers votre adresse.",
                timestamp = "10:42"
            )
        )
    )
    val courierChatMessages: StateFlow<List<CourierChatMessage>> = _courierChatMessages.asStateFlow()

    private var liveSimulationJob: Job? = null
    private val _isLiveSimulationRunning = MutableStateFlow(true)
    val isLiveSimulationRunning: StateFlow<Boolean> = _isLiveSimulationRunning.asStateFlow()

    // Categories & Regions
    val categories: List<String> = repository.getCategories()
    val regions: List<String> = repository.getRegions()

    // Filtered Medicines
    val filteredMedicines: StateFlow<List<Medicine>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        var list = repository.getAllMedicines()
        if (category != "Tous") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.dci.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.description.lowercase().contains(q)
            }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.getAllMedicines()
    )

    // Filtered Pharmacies across Senegal
    val filteredPharmacies: StateFlow<List<Pharmacy>> = combine(
        _searchQuery,
        _dutyOnlyFilter,
        _selectedRegion
    ) { query, dutyOnly, region ->
        var list = repository.getAllPharmacies()
        if (region != "Toutes les régions") {
            list = list.filter { it.region.equals(region, ignoreCase = true) }
        }
        if (dutyOnly) {
            list = list.filter { it.isDutyPharmacy }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.district.lowercase().contains(q) ||
                it.city.lowercase().contains(q) ||
                it.region.lowercase().contains(q) ||
                it.address.lowercase().contains(q)
            }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.getAllPharmacies()
    )

    val allPharmacies: StateFlow<List<Pharmacy>> = MutableStateFlow(repository.getAllPharmacies()).asStateFlow()

    // Cart Items Flow from Room
    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Orders Flow from Room
    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Prescriptions Flow from Room
    val prescriptions: StateFlow<List<PrescriptionEntity>> = repository.allPrescriptions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Reminders Flow from Room
    val reminders: StateFlow<List<ReminderEntity>> = repository.allReminders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // User Profile Flow from Room
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Delivery Addresses Flow from Room
    val deliveryAddresses: StateFlow<List<DeliveryAddressEntity>> = repository.deliveryAddresses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val defaultAddress: StateFlow<DeliveryAddressEntity?> = repository.getDefaultAddress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Computed Safety Alerts (e.g., Paracetamol cumulative dosage check)
    val safetyAlerts: StateFlow<List<String>> = cartItems.combine(_isPrescriptionAttachedToCart) { items, hasRx ->
        val alerts = mutableListOf<String>()
        val paracetamolCount = items.count { it.medicineName.contains("Paracétamol", ignoreCase = true) || it.medicineName.contains("Doliprane", ignoreCase = true) || it.medicineName.contains("Efferalgan", ignoreCase = true) }
        if (paracetamolCount > 1) {
            alerts.add("Attention : Votre panier contient plusieurs médicaments à base de Paracétamol. Respectez un intervalle de 6h et ne dépassez pas 3g/jour.")
        }
        val requiresRx = items.any { it.requiresPrescription }
        if (requiresRx && !hasRx) {
            alerts.add("Ordonnance requise : Certains médicaments de votre panier exigent une ordonnance médicale certifiée avant expédition.")
        }
        alerts
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onRegionSelected(region: String) {
        _selectedRegion.value = region
    }

    fun toggleDutyOnlyFilter() {
        _dutyOnlyFilter.value = !_dutyOnlyFilter.value
    }

    fun selectMedicine(medicine: Medicine?) {
        _selectedMedicine.value = medicine
    }

    fun selectPharmacy(pharmacy: Pharmacy?) {
        _selectedPharmacy.value = pharmacy
    }

    fun setActiveOrder(order: OrderEntity?) {
        _activeOrder.value = order
    }

    fun attachPrescriptionToCart(summary: String) {
        _isPrescriptionAttachedToCart.value = true
        _attachedPrescriptionSummary.value = summary
    }

    fun detachPrescriptionFromCart() {
        _isPrescriptionAttachedToCart.value = false
        _attachedPrescriptionSummary.value = null
    }

    // Cart Actions
    fun addToCart(medicine: Medicine, pharmacy: Pharmacy? = null, quantity: Int = 1) {
        viewModelScope.launch {
            val targetPharmacy = pharmacy ?: repository.getPharmacyById(medicine.pharmacyId) ?: InitialData.pharmacies.first()
            repository.addToCart(medicine, targetPharmacy, quantity)
        }
    }

    fun updateCartItemQuantity(item: CartItemEntity, delta: Int) {
        viewModelScope.launch {
            repository.updateItemQuantity(item, delta)
        }
    }

    fun removeCartItem(cartItemId: Int) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Process Secure Online Payment
    fun processOnlinePayment(
        items: List<CartItemEntity>,
        pharmacy: Pharmacy,
        deliveryAddress: String,
        patientName: String,
        patientPhone: String,
        paymentMethod: PaymentMethod,
        mobileNumberOrCard: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            if (paymentMethod == PaymentMethod.WAVE) {
                _paymentState.value = PaymentProcessState.Processing(
                    paymentMethod,
                    "Connexion à l'API Wave Mobile Money Sénégal..."
                )
                delay(800)

                _paymentState.value = PaymentProcessState.Processing(
                    paymentMethod,
                    "Envoi de la notification de confirmation Wave sur $mobileNumberOrCard (0% Frais)..."
                )
                delay(1200)

                _paymentState.value = PaymentProcessState.Processing(
                    paymentMethod,
                    "Débit Wave validé avec succès • Séquestre pharmaceutique activé..."
                )
                delay(800)
            } else {
                _paymentState.value = PaymentProcessState.Processing(
                    paymentMethod,
                    "Connexion sécurisée avec la passerelle ${paymentMethod.displayName}..."
                )
                delay(1000)

                _paymentState.value = PaymentProcessState.Processing(
                    paymentMethod,
                    "Vérification du compte ($mobileNumberOrCard) & Sécurisation des fonds..."
                )
                delay(1200)

                _paymentState.value = PaymentProcessState.Processing(
                    paymentMethod,
                    "Validation pharmaceutique & Génération de la facture certifiée..."
                )
                delay(800)
            }

            val newOrder = repository.createOrder(
                items = items,
                pharmacy = pharmacy,
                deliveryAddress = deliveryAddress,
                patientName = patientName,
                patientPhone = patientPhone,
                paymentMethod = paymentMethod,
                isPrescriptionProvided = _isPrescriptionAttachedToCart.value
            )

            _isPrescriptionAttachedToCart.value = false
            _attachedPrescriptionSummary.value = null
            _paymentState.value = PaymentProcessState.Success(newOrder)
            _activeOrder.value = newOrder
            onSuccess(newOrder)
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentProcessState.Idle
    }

    // Cancel Order
    fun cancelOrder(orderId: String, reason: String = "", onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.cancelOrder(orderId)
            val currentOrder = _activeOrder.value
            if (currentOrder != null && currentOrder.id == orderId) {
                _activeOrder.value = currentOrder.copy(status = OrderStatus.CANCELLED.name)
            }
            onComplete()
        }
    }

    // Delete Order from history
    fun deleteOrder(orderId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
            if (_activeOrder.value?.id == orderId) {
                _activeOrder.value = null
            }
            onComplete()
        }
    }

    // Reorder items from a past order
    fun reorderPastOrder(order: OrderEntity, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            var itemsAdded = 0
            val itemChunks = order.itemsSummary.split(" | ")
            val pharmacy = repository.getPharmacyById(order.pharmacyId) ?: InitialData.pharmacies.first()
            for (chunk in itemChunks) {
                val parts = chunk.split(" x")
                val medName = parts.firstOrNull()?.trim() ?: continue
                val qty = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 1
                val matchedMed = InitialData.medicines.find {
                    it.name.equals(medName, ignoreCase = true) ||
                    medName.contains(it.name, ignoreCase = true) ||
                    it.name.contains(medName, ignoreCase = true)
                } ?: InitialData.medicines.firstOrNull()

                if (matchedMed != null) {
                    repository.addToCart(matchedMed, pharmacy, qty)
                    itemsAdded += qty
                }
            }
            onComplete(itemsAdded)
        }
    }

    // Delete Prescription
    fun deletePrescription(prescriptionId: String) {
        viewModelScope.launch {
            repository.deletePrescription(prescriptionId)
        }
    }

    // Advance Order Status Simulator (Live tracking experience)
    fun advanceOrderStatus(orderId: String) {
        viewModelScope.launch {
            val currentOrder = _activeOrder.value ?: repository.getOrderById(orderId).firstOrNull() ?: return@launch
            val nextStatus = when (currentOrder.status) {
                OrderStatus.PAID_CONFIRMED.name -> OrderStatus.PHARMACIST_PREPARING
                OrderStatus.PHARMACIST_PREPARING.name -> OrderStatus.SEALED_DISPATCHED
                OrderStatus.SEALED_DISPATCHED.name -> OrderStatus.OUT_FOR_DELIVERY
                OrderStatus.OUT_FOR_DELIVERY.name -> OrderStatus.DELIVERED
                else -> OrderStatus.DELIVERED
            }
            repository.updateOrderStatus(orderId, nextStatus)
            _activeOrder.value = currentOrder.copy(status = nextStatus.name)
            updateTelemetryForStatus(nextStatus)
        }
    }

    fun initLiveTracking(order: OrderEntity) {
        _activeOrder.value = order
        val status = try { OrderStatus.valueOf(order.status) } catch (e: Exception) { OrderStatus.OUT_FOR_DELIVERY }
        updateTelemetryForStatus(status)
        startOrResumeLiveSimulation(order.id)
    }

    private fun updateTelemetryForStatus(status: OrderStatus) {
        val baseTime = SimpleDateFormat("HH:mm", Locale.FRENCH)
        val now = System.currentTimeMillis()
        val t0 = baseTime.format(Date(now - 12 * 60 * 1000))
        val t1 = baseTime.format(Date(now - 8 * 60 * 1000))
        val t2 = baseTime.format(Date(now - 4 * 60 * 1000))
        val t3 = baseTime.format(Date(now - 1 * 60 * 1000))
        val tNow = baseTime.format(Date(now))

        val events = mutableListOf<LiveOrderEvent>()
        events.add(
            LiveOrderEvent(
                timestamp = t0,
                title = "Paiement en ligne sécurisé validé",
                description = "Fonds consignés sous séquestre pharmaceutique 0% fraude.",
                isCompleted = status.stepIndex >= 1,
                isCurrent = status.stepIndex == 1
            )
        )
        events.add(
            LiveOrderEvent(
                timestamp = t1,
                title = "Contrôle & Préparation par le Docteur en Pharmacie",
                description = "Vérification des lots, posologies et validité de l'ordonnance.",
                isCompleted = status.stepIndex >= 2,
                isCurrent = status.stepIndex == 2
            )
        )
        events.add(
            LiveOrderEvent(
                timestamp = t2,
                title = "Colis pharmaceutique scellé sous sac isotherme",
                description = "Scellé d'inviolabilité #SN-8921 appliqué avec capteur température 4.8°C.",
                isCompleted = status.stepIndex >= 3,
                isCurrent = status.stepIndex == 3
            )
        )
        events.add(
            LiveOrderEvent(
                timestamp = t3,
                title = "Coursier de santé en route (GPS actif)",
                description = "Mamadou Ndiaye roule sur l'Avenue Cheikh Anta Diop.",
                isCompleted = status.stepIndex >= 4,
                isCurrent = status.stepIndex == 4
            )
        )
        events.add(
            LiveOrderEvent(
                timestamp = tNow,
                title = "Remise en main propre contre code PIN",
                description = "Vérification d'identité et du code secret à la réception.",
                isCompleted = status.stepIndex >= 5,
                isCurrent = status.stepIndex == 5
            )
        )
        _liveEventsLog.value = events

        when (status) {
            OrderStatus.PAID_CONFIRMED -> {
                _liveTelemetry.value = _liveTelemetry.value.copy(
                    progress = 0.05f,
                    currentStreet = "Grande Pharmacie (Préparation des boîtes)",
                    distanceRemainingMeters = 3400,
                    etaSeconds = 1200,
                    speedKmh = 0,
                    temperatureCelsius = 4.5
                )
            }
            OrderStatus.PHARMACIST_PREPARING -> {
                _liveTelemetry.value = _liveTelemetry.value.copy(
                    progress = 0.20f,
                    currentStreet = "Laboratoire de dispensation pharmaceutique",
                    distanceRemainingMeters = 3100,
                    etaSeconds = 950,
                    speedKmh = 0,
                    temperatureCelsius = 4.6
                )
            }
            OrderStatus.SEALED_DISPATCHED -> {
                _liveTelemetry.value = _liveTelemetry.value.copy(
                    progress = 0.40f,
                    currentStreet = "Sortie Pharmacie • Boulevard de la République",
                    distanceRemainingMeters = 2400,
                    etaSeconds = 720,
                    speedKmh = 24,
                    temperatureCelsius = 4.7
                )
            }
            OrderStatus.OUT_FOR_DELIVERY -> {
                _liveTelemetry.value = _liveTelemetry.value.copy(
                    progress = 0.72f,
                    currentStreet = "Avenue Cheikh Anta Diop, près UCAD Dakar",
                    distanceRemainingMeters = 750,
                    etaSeconds = 290,
                    speedKmh = 34,
                    temperatureCelsius = 4.8
                )
            }
            OrderStatus.DELIVERED -> {
                _liveTelemetry.value = _liveTelemetry.value.copy(
                    progress = 1.0f,
                    currentStreet = "Arrivé à votre adresse de livraison",
                    distanceRemainingMeters = 0,
                    etaSeconds = 0,
                    speedKmh = 0,
                    temperatureCelsius = 4.9
                )
            }
            OrderStatus.CANCELLED -> {
                _liveTelemetry.value = _liveTelemetry.value.copy(
                    speedKmh = 0,
                    etaSeconds = 0,
                    currentStreet = "Course annulée - Remboursement effectué"
                )
            }
            else -> {}
        }
    }

    fun toggleLiveAutoSimulation() {
        val nextRunning = !_isLiveSimulationRunning.value
        _isLiveSimulationRunning.value = nextRunning
        val order = _activeOrder.value ?: return
        if (nextRunning) {
            startOrResumeLiveSimulation(order.id)
        } else {
            liveSimulationJob?.cancel()
        }
    }

    private fun startOrResumeLiveSimulation(orderId: String) {
        liveSimulationJob?.cancel()
        liveSimulationJob = viewModelScope.launch {
            val streetNames = listOf(
                "Avenue Cheikh Anta Diop (Fann Résidence)",
                "Rond-point EMG / Stèle Mermoz",
                "Avenue Bourguiba vers Keur Gorgui",
                "Rue 10 x Voie de Dégagement Nord",
                "Entrée Résidence Keur Gorgui (Devant le portail)"
            )
            var streetIdx = 0

            while (_isLiveSimulationRunning.value) {
                delay(3000)
                val curTel = _liveTelemetry.value
                val currentOrder = _activeOrder.value ?: break
                val status = try { OrderStatus.valueOf(currentOrder.status) } catch (e: Exception) { OrderStatus.OUT_FOR_DELIVERY }

                if (status == OrderStatus.CANCELLED || status == OrderStatus.DELIVERED) {
                    break
                }

                val newProgress = (curTel.progress + 0.04f).coerceAtMost(1.0f)
                val newDistance = (curTel.distanceRemainingMeters - 120).coerceAtLeast(0)
                val newEta = (curTel.etaSeconds - 30).coerceAtLeast(0)
                val randomSpeed = if (newDistance == 0) 0 else (28..38).random()
                val randomTemp = 4.6 + ((-2..2).random() * 0.1)

                if (streetIdx < streetNames.size - 1 && (newProgress > (streetIdx + 1) * 0.2f)) {
                    streetIdx++
                }

                _liveTelemetry.value = curTel.copy(
                    progress = newProgress,
                    distanceRemainingMeters = newDistance,
                    etaSeconds = newEta,
                    speedKmh = randomSpeed,
                    temperatureCelsius = String.format(Locale.US, "%.1f", randomTemp).toDoubleOrNull() ?: 4.8,
                    currentStreet = if (newDistance == 0) "Arrivé devant votre porte !" else streetNames[streetIdx]
                )

                if (newProgress >= 0.98f && status == OrderStatus.OUT_FOR_DELIVERY) {
                    repository.updateOrderStatus(orderId, OrderStatus.DELIVERED)
                    _activeOrder.value = currentOrder.copy(status = OrderStatus.DELIVERED.name)
                    updateTelemetryForStatus(OrderStatus.DELIVERED)
                    break
                } else if (newProgress >= 0.65f && status == OrderStatus.SEALED_DISPATCHED) {
                    repository.updateOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY)
                    _activeOrder.value = currentOrder.copy(status = OrderStatus.OUT_FOR_DELIVERY.name)
                    updateTelemetryForStatus(OrderStatus.OUT_FOR_DELIVERY)
                } else if (newProgress >= 0.35f && status == OrderStatus.PHARMACIST_PREPARING) {
                    repository.updateOrderStatus(orderId, OrderStatus.SEALED_DISPATCHED)
                    _activeOrder.value = currentOrder.copy(status = OrderStatus.SEALED_DISPATCHED.name)
                    updateTelemetryForStatus(OrderStatus.SEALED_DISPATCHED)
                }
            }
        }
    }

    fun sendCourierChatMessage(text: String) {
        if (text.isBlank()) return
        val nowStr = SimpleDateFormat("HH:mm", Locale.FRENCH).format(Date())
        val userMsg = CourierChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = "user",
            senderName = userName.value,
            text = text.trim(),
            timestamp = nowStr
        )
        _courierChatMessages.value = _courierChatMessages.value + userMsg

        // Courier Auto-reply simulation
        viewModelScope.launch {
            delay(1600)
            val replyText = when {
                text.contains("étage", ignoreCase = true) || text.contains("porte", ignoreCase = true) ->
                    "Bien noté ! Je monte directement vous remettre le colis sécurisé."
                text.contains("pin", ignoreCase = true) || text.contains("code", ignoreCase = true) ->
                    "Parfait, j'enregistre votre code PIN pour valider la remise sans contact."
                text.contains("froid", ignoreCase = true) || text.contains("température", ignoreCase = true) ->
                    "Pas d'inquiétude ! La boîte est conservée dans la sacoche isotherme certifiée à 4.8°C."
                else ->
                    "Message bien reçu ! Je suis en route, j'arrive dans quelques instants."
            }
            val replyMsg = CourierChatMessage(
                id = "reply_${System.currentTimeMillis()}",
                sender = "courier",
                senderName = "Mamadou Ndiaye (Livreur)",
                text = replyText,
                timestamp = SimpleDateFormat("HH:mm", Locale.FRENCH).format(Date())
            )
            _courierChatMessages.value = _courierChatMessages.value + replyMsg
        }
    }

    // Prescriptions
    fun submitPrescription(
        patientName: String,
        doctorName: String,
        prescriptionDate: String,
        photoUri: String,
        notes: String,
        recognizedMedicines: String,
        pharmacyId: String = "pharm_1",
        pharmacyName: String = "Grande Pharmacie Guigon (Dakar Plateau)",
        pharmacyRegion: String = "Dakar",
        onSuccess: (PrescriptionEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            val rx = repository.submitPrescription(
                patientName = patientName,
                doctorName = doctorName,
                prescriptionDate = prescriptionDate,
                photoUri = photoUri,
                notes = notes,
                recognizedMedicines = recognizedMedicines,
                pharmacyId = pharmacyId,
                pharmacyName = pharmacyName,
                pharmacyRegion = pharmacyRegion
            )
            attachPrescriptionToCart("Ordonnance validée par $pharmacyName - Dr. $doctorName")
            onSuccess(rx)
        }
    }

    fun orderDirectlyFromPrescription(prescription: PrescriptionEntity) {
        viewModelScope.launch {
            // Auto add prescribed medicines to cart
            val amox = repository.getMedicineById("med_2")
            val ventoline = repository.getMedicineById("med_9")
            val pharmacy = repository.getPharmacyById(prescription.pharmacyId) ?: InitialData.pharmacies.first()

            if (amox != null) repository.addToCart(amox, pharmacy, 1)
            if (ventoline != null) repository.addToCart(ventoline, pharmacy, 1)

            attachPrescriptionToCart("Ordonnance #${prescription.id.take(6)} - ${prescription.pharmacyName} - Dr. ${prescription.doctorName}")
        }
    }

    // Reminders
    fun addReminder(name: String, dosage: String, time: String, instructions: String) {
        viewModelScope.launch {
            repository.addReminder(name, dosage, time, instructions)
        }
    }

    fun deleteReminder(id: Int) {
        viewModelScope.launch {
            repository.deleteReminder(id)
        }
    }

    fun toggleReminder(id: Int, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleReminder(id, isActive)
        }
    }

    private fun observeProfileAndAddresses() {
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                if (profile != null) {
                    userName.value = profile.fullName
                    userPhone.value = profile.phoneNumber
                }
            }
        }
        viewModelScope.launch {
            repository.getDefaultAddress().collect { defaultAddr ->
                if (defaultAddr != null) {
                    userDeliveryAddress.value = "${defaultAddr.fullAddress}, ${defaultAddr.neighborhood}, ${defaultAddr.city}"
                }
            }
        }
    }

    // Profile & Contact info management
    fun updateContactDetails(
        fullName: String,
        email: String,
        phoneNumber: String,
        secondaryPhone: String,
        emergencyContactName: String,
        emergencyContactPhone: String,
        bloodGroup: String,
        knownAllergies: String,
        preferredPaymentMethod: String,
        medicalNotes: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val updatedProfile = UserProfileEntity(
                id = "primary_user",
                fullName = fullName.trim(),
                email = email.trim(),
                phoneNumber = phoneNumber.trim(),
                secondaryPhone = secondaryPhone.trim(),
                emergencyContactName = emergencyContactName.trim(),
                emergencyContactPhone = emergencyContactPhone.trim(),
                bloodGroup = bloodGroup.trim(),
                knownAllergies = knownAllergies.trim(),
                preferredPaymentMethod = preferredPaymentMethod,
                medicalNotes = medicalNotes.trim()
            )
            repository.saveUserProfile(updatedProfile)
            userName.value = updatedProfile.fullName
            userPhone.value = updatedProfile.phoneNumber
            onSuccess()
        }
    }

    // Delivery addresses management
    fun saveDeliveryAddress(
        id: String?,
        title: String,
        recipientName: String,
        contactPhone: String,
        fullAddress: String,
        neighborhood: String,
        city: String,
        region: String,
        courierInstructions: String,
        isDefault: Boolean,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            if (id == null) {
                // Add new address
                repository.addDeliveryAddress(
                    title = title.trim(),
                    recipientName = recipientName.trim(),
                    contactPhone = contactPhone.trim(),
                    fullAddress = fullAddress.trim(),
                    neighborhood = neighborhood.trim(),
                    city = city.trim(),
                    region = region.trim(),
                    courierInstructions = courierInstructions.trim(),
                    isDefault = isDefault
                )
            } else {
                // Update existing address
                val address = DeliveryAddressEntity(
                    id = id,
                    title = title.trim(),
                    recipientName = recipientName.trim(),
                    contactPhone = contactPhone.trim(),
                    fullAddress = fullAddress.trim(),
                    neighborhood = neighborhood.trim(),
                    city = city.trim(),
                    region = region.trim(),
                    courierInstructions = courierInstructions.trim(),
                    isDefault = isDefault,
                    createdAt = System.currentTimeMillis()
                )
                repository.updateDeliveryAddress(address)
            }
            if (isDefault) {
                userDeliveryAddress.value = "${fullAddress.trim()}, ${neighborhood.trim()}, ${city.trim()}"
            }
            onSuccess()
        }
    }

    fun deleteDeliveryAddress(addressId: String) {
        viewModelScope.launch {
            repository.deleteDeliveryAddress(addressId)
        }
    }

    fun setDefaultDeliveryAddress(addressId: String) {
        viewModelScope.launch {
            repository.setDefaultAddress(addressId)
        }
    }

    private fun seedDefaultsIfEmpty() {
        viewModelScope.launch {
            repository.seedInitialUserHistoryIfEmpty()

            // add sample reminder
            repository.addReminder("Doliprane 1000mg", "1 comprimé", "08:00", "Après le petit-déjeuner")
            repository.addReminder("Amoxicilline 1g", "1 comprimé", "12:30", "Au milieu du déjeuner")
            repository.addReminder("Amoxicilline 1g", "1 comprimé", "20:00", "Pendant le dîner")

            // sample verified prescription
            repository.submitPrescription(
                patientName = "Mamadou Dramé",
                doctorName = "Dr. Sokhna Ndao (Médecin Généraliste)",
                prescriptionDate = "28 Août 2026",
                photoUri = "sample_prescription_rx",
                notes = "Ordonnance certifiée conforme par l'Ordre des Pharmaciens.",
                recognizedMedicines = "Amoxicilline 1g x 14 comp, Ventoline 100µg x 1 flacon"
            )
        }
    }
}
