package com.example.data.repository

import com.example.data.local.InitialData
import com.example.data.local.PharmaDao
import com.example.data.model.CartItemEntity
import com.example.data.model.Medicine
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.Pharmacy
import com.example.data.model.PrescriptionEntity
import com.example.data.model.ReminderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class PharmaRepository(private val dao: PharmaDao) {

    // --- Static & Live In-Memory Master Data ---
    fun getAllPharmacies(): List<Pharmacy> = InitialData.pharmacies

    fun getPharmacyById(pharmacyId: String): Pharmacy? =
        InitialData.pharmacies.find { it.id == pharmacyId }

    fun getAllMedicines(): List<Medicine> = InitialData.medicines

    fun getMedicineById(medicineId: String): Medicine? =
        InitialData.medicines.find { it.id == medicineId }

    fun getMedicinesByPharmacy(pharmacyId: String): List<Medicine> =
        InitialData.medicines.filter { it.pharmacyId == pharmacyId }

    fun getCategories(): List<String> = InitialData.categories

    // --- Cart Management ---
    val cartItems: Flow<List<CartItemEntity>> = dao.getAllCartItems()

    suspend fun addToCart(medicine: Medicine, pharmacy: Pharmacy, quantity: Int = 1) {
        val existing = dao.getCartItemByMedicineId(medicine.id)
        if (existing != null) {
            dao.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            val item = CartItemEntity(
                medicineId = medicine.id,
                medicineName = medicine.name,
                pharmacyId = pharmacy.id,
                pharmacyName = pharmacy.name,
                priceFcfa = medicine.priceFcfa,
                quantity = quantity,
                requiresPrescription = medicine.requiresPrescription,
                dosageForm = medicine.dosageForm,
                dosageStrength = medicine.dosageStrength
            )
            dao.insertCartItem(item)
        }
    }

    suspend fun updateCartQuantity(cartItemId: Int, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteCartItemById(cartItemId)
        } else {
            // we can retrieve and update
        }
    }

    suspend fun removeCartItem(cartItemId: Int) {
        dao.deleteCartItemById(cartItemId)
    }

    suspend fun updateItemQuantity(item: CartItemEntity, delta: Int) {
        val newQty = item.quantity + delta
        if (newQty <= 0) {
            dao.deleteCartItemById(item.id)
        } else {
            dao.updateCartItem(item.copy(quantity = newQty))
        }
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    // --- Orders Management ---
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()

    fun getOrderById(orderId: String): Flow<OrderEntity?> = dao.getOrderById(orderId)

    suspend fun createOrder(
        items: List<CartItemEntity>,
        pharmacy: Pharmacy,
        deliveryAddress: String,
        patientName: String,
        patientPhone: String,
        paymentMethod: PaymentMethod,
        isPrescriptionProvided: Boolean = false
    ): OrderEntity {
        val subtotal = items.sumOf { it.priceFcfa * it.quantity }
        val deliveryFee = pharmacy.deliveryFeeFcfa
        val total = subtotal + deliveryFee

        val orderNum = "#PH-" + (10000..99999).random()
        val orderId = UUID.randomUUID().toString()
        val pinCode = (1000..9999).random().toString()
        val txId = "TXN-" + paymentMethod.name.take(3) + "-" + System.currentTimeMillis().toString().takeLast(6)

        val couriers = listOf(
            Pair("Mamadou Seck (Coursier certifié)", "+221 77 412 88 99"),
            Pair("Ousmane Faye (Express Santé)", "+221 78 523 11 44"),
            Pair("Cheikh Kane (Livreur pharma agréé)", "+221 76 901 33 22")
        ).random()

        val itemsSummary = items.joinToString(" | ") { "${it.medicineName} x${it.quantity}" }

        val order = OrderEntity(
            id = orderId,
            orderNumber = orderNum,
            orderTimestamp = System.currentTimeMillis(),
            status = OrderStatus.PAID_CONFIRMED.name,
            itemsSummary = itemsSummary,
            subtotalFcfa = subtotal,
            deliveryFeeFcfa = deliveryFee,
            totalFcfa = total,
            pharmacyId = pharmacy.id,
            pharmacyName = pharmacy.name,
            pharmacyAddress = pharmacy.address,
            deliveryAddress = deliveryAddress,
            patientName = patientName,
            patientPhone = patientPhone,
            paymentMethod = paymentMethod.name,
            paymentTransactionId = txId,
            isPrescriptionVerified = isPrescriptionProvided || items.none { it.requiresPrescription },
            deliveryPinCode = pinCode,
            courierName = couriers.first,
            courierPhone = couriers.second,
            deliveryEtaMinutes = pharmacy.estimatedDeliveryMinutes,
            invoiceQrCodePayload = "PHARMADIRECT-SECURE-INVOICE|$orderNum|$txId|$total|FCFA|VERIFIED"
        )

        dao.insertOrder(order)
        dao.clearCart()
        return order
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        dao.updateOrderStatus(orderId, status.name)
    }

    // --- Prescriptions ---
    val allPrescriptions: Flow<List<PrescriptionEntity>> = dao.getAllPrescriptions()

    suspend fun submitPrescription(
        patientName: String,
        doctorName: String,
        prescriptionDate: String,
        photoUri: String,
        notes: String,
        recognizedMedicines: String
    ): PrescriptionEntity {
        val prescription = PrescriptionEntity(
            id = UUID.randomUUID().toString(),
            patientName = patientName,
            doctorName = doctorName,
            prescriptionDate = prescriptionDate,
            photoUri = photoUri,
            uploadTimestamp = System.currentTimeMillis(),
            status = "VALIDATED", // Immediate pharmacist pre-check for demo reliability
            pharmacistNotes = if (notes.isBlank()) "Ordonnance certifiée conforme par le pharmacien de garde. Dosage vérifié." else notes,
            recognizedMedicines = recognizedMedicines
        )
        dao.insertPrescription(prescription)
        return prescription
    }

    // --- Medication Reminders ---
    val allReminders: Flow<List<ReminderEntity>> = dao.getAllReminders()

    suspend fun addReminder(name: String, dosage: String, time: String, instructions: String) {
        dao.insertReminder(
            ReminderEntity(
                medicineName = name,
                dosage = dosage,
                time = time,
                instructions = instructions,
                isActive = true
            )
        )
    }

    suspend fun deleteReminder(id: Int) {
        dao.deleteReminderById(id)
    }

    suspend fun toggleReminder(id: Int, isActive: Boolean) {
        dao.toggleReminder(id, isActive)
    }

    // Pre-populate sample order and reminder if empty
    suspend fun seedInitialUserHistoryIfEmpty() {
        // can be called on init
    }
}
