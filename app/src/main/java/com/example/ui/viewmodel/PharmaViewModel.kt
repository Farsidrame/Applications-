package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.InitialData
import com.example.data.local.PharmaDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.Medicine
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.Pharmacy
import com.example.data.model.PrescriptionEntity
import com.example.data.model.ReminderEntity
import com.example.data.repository.PharmaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
    }

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tous")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

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

    // Categories
    val categories: List<String> = repository.getCategories()

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

    // Filtered Pharmacies
    val filteredPharmacies: StateFlow<List<Pharmacy>> = combine(
        _searchQuery,
        _dutyOnlyFilter
    ) { query, dutyOnly ->
        var list = repository.getAllPharmacies()
        if (dutyOnly) {
            list = list.filter { it.isDutyPharmacy }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.district.lowercase().contains(q) ||
                it.address.lowercase().contains(q)
            }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.getAllPharmacies()
    )

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

    // Advance Order Status Simulator (Live tracking experience)
    fun advanceOrderStatus(orderId: String) {
        viewModelScope.launch {
            val currentOrder = _activeOrder.value ?: return@launch
            val nextStatus = when (currentOrder.status) {
                OrderStatus.PAID_CONFIRMED.name -> OrderStatus.PHARMACIST_PREPARING
                OrderStatus.PHARMACIST_PREPARING.name -> OrderStatus.SEALED_DISPATCHED
                OrderStatus.SEALED_DISPATCHED.name -> OrderStatus.OUT_FOR_DELIVERY
                OrderStatus.OUT_FOR_DELIVERY.name -> OrderStatus.DELIVERED
                else -> OrderStatus.DELIVERED
            }
            repository.updateOrderStatus(orderId, nextStatus)
            _activeOrder.value = currentOrder.copy(status = nextStatus.name)
        }
    }

    // Prescriptions
    fun submitPrescription(
        patientName: String,
        doctorName: String,
        prescriptionDate: String,
        photoUri: String,
        notes: String,
        recognizedMedicines: String
    ) {
        viewModelScope.launch {
            val rx = repository.submitPrescription(
                patientName = patientName,
                doctorName = doctorName,
                prescriptionDate = prescriptionDate,
                photoUri = photoUri,
                notes = notes,
                recognizedMedicines = recognizedMedicines
            )
            attachPrescriptionToCart("Ordonnance Dr. $doctorName ($prescriptionDate)")
        }
    }

    fun orderDirectlyFromPrescription(prescription: PrescriptionEntity) {
        viewModelScope.launch {
            // Auto add prescribed medicines to cart
            val amox = repository.getMedicineById("med_2")
            val ventoline = repository.getMedicineById("med_9")
            val pharmacy = repository.getPharmacyById("pharm_1") ?: InitialData.pharmacies.first()

            if (amox != null) repository.addToCart(amox, pharmacy, 1)
            if (ventoline != null) repository.addToCart(ventoline, pharmacy, 1)

            attachPrescriptionToCart("Ordonnance #${prescription.id.take(6)} - Dr. ${prescription.doctorName}")
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

    private fun seedDefaultsIfEmpty() {
        viewModelScope.launch {
            // Check if prescriptions/reminders exist, seed realistic sample if empty
            val currentReminders = repository.getAllMedicines()
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
