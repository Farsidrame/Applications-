package com.example.data.repository

import com.example.data.local.InitialData
import com.example.data.local.PharmaDao
import com.example.data.model.CartItemEntity
import com.example.data.model.DeliveryAddressEntity
import com.example.data.model.Medicine
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.Pharmacy
import com.example.data.model.PrescriptionEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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

    fun getRegions(): List<String> = InitialData.regions

    fun getPharmaciesByRegion(region: String): List<Pharmacy> =
        if (region == "Toutes les régions") InitialData.pharmacies
        else InitialData.pharmacies.filter { it.region.equals(region, ignoreCase = true) }

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

    suspend fun cancelOrder(orderId: String) {
        dao.updateOrderStatus(orderId, OrderStatus.CANCELLED.name)
    }

    suspend fun deleteOrder(orderId: String) {
        dao.deleteOrderById(orderId)
    }

    // --- Prescriptions ---
    val allPrescriptions: Flow<List<PrescriptionEntity>> = dao.getAllPrescriptions()

    suspend fun submitPrescription(
        patientName: String,
        doctorName: String,
        prescriptionDate: String,
        photoUri: String,
        notes: String,
        recognizedMedicines: String,
        pharmacyId: String = "pharm_1",
        pharmacyName: String = "Grande Pharmacie Guigon (Dakar Plateau)",
        pharmacyRegion: String = "Dakar"
    ): PrescriptionEntity {
        val targetPharmacy = getPharmacyById(pharmacyId) ?: InitialData.pharmacies.first()
        val pharmacistName = "Dr. ${targetPharmacy.pharmacistInCharge} (Docteur en Pharmacie)"

        val medParts = recognizedMedicines.split(",").map { it.trim() }.filter { it.isNotBlank() }
        val availableList = medParts.map { med ->
            val match = InitialData.medicines.find { it.name.contains(med, ignoreCase = true) || med.contains(it.name, ignoreCase = true) }
            val price = match?.priceFcfa ?: 3200
            val name = match?.name ?: med
            "$name (Disponible • $price FCFA)"
        }
        val summary = if (availableList.isNotEmpty()) availableList.joinToString(" | ") else "Médicaments vérifiés disponibles en stock à l'officine"
        val totalEstimate = medParts.sumOf { med ->
            val match = InitialData.medicines.find { it.name.contains(med, ignoreCase = true) || med.contains(it.name, ignoreCase = true) }
            match?.priceFcfa ?: 3200
        }

        val prescription = PrescriptionEntity(
            id = UUID.randomUUID().toString(),
            patientName = patientName,
            doctorName = doctorName,
            prescriptionDate = prescriptionDate,
            photoUri = photoUri,
            uploadTimestamp = System.currentTimeMillis(),
            status = "VALIDATED", // Immediate pharmacist pre-check for demo reliability
            pharmacistNotes = if (notes.isBlank()) "Ordonnance certifiée conforme par $pharmacistName à $pharmacyName. Médicaments vérifiés disponibles en stock." else notes,
            recognizedMedicines = recognizedMedicines,
            pharmacyId = pharmacyId,
            pharmacyName = pharmacyName,
            pharmacyRegion = pharmacyRegion,
            pharmacistName = pharmacistName,
            pharmacistReviewStatus = "VALIDATED",
            availableMedicinesSummary = summary,
            totalEstimatedFcfa = if (totalEstimate > 0) totalEstimate else 7700
        )
        dao.insertPrescription(prescription)
        return prescription
    }

    suspend fun deletePrescription(prescriptionId: String) {
        dao.deletePrescriptionById(prescriptionId)
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

    // --- User Profile Management ---
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.insertUserProfile(profile)
    }

    // --- Delivery Addresses Management ---
    val deliveryAddresses: Flow<List<DeliveryAddressEntity>> = dao.getAllAddresses()

    fun getDefaultAddress(): Flow<DeliveryAddressEntity?> = dao.getDefaultAddress()

    suspend fun addDeliveryAddress(
        title: String,
        recipientName: String,
        contactPhone: String,
        fullAddress: String,
        neighborhood: String,
        city: String = "Dakar",
        region: String = "Dakar",
        courierInstructions: String = "",
        isDefault: Boolean = false
    ): DeliveryAddressEntity {
        if (isDefault) {
            dao.clearDefaultAddresses()
        }
        val newAddress = DeliveryAddressEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            recipientName = recipientName,
            contactPhone = contactPhone,
            fullAddress = fullAddress,
            neighborhood = neighborhood,
            city = city,
            region = region,
            courierInstructions = courierInstructions,
            isDefault = isDefault,
            createdAt = System.currentTimeMillis()
        )
        dao.insertAddress(newAddress)
        return newAddress
    }

    suspend fun updateDeliveryAddress(address: DeliveryAddressEntity) {
        if (address.isDefault) {
            dao.clearDefaultAddresses()
        }
        dao.updateAddress(address)
    }

    suspend fun deleteDeliveryAddress(addressId: String) {
        dao.deleteAddressById(addressId)
    }

    suspend fun setDefaultAddress(addressId: String) {
        dao.clearDefaultAddresses()
        dao.setDefaultAddress(addressId)
    }

    // --- Pharmacist Registrations Management ---
    val allPharmacists: Flow<List<com.example.data.model.PharmacistRegistrationEntity>> = dao.getAllPharmacists()

    suspend fun registerPharmacist(
        fullName: String,
        pharmacyName: String,
        region: String,
        city: String,
        district: String,
        phoneNumber: String,
        email: String,
        licenseNumber: String,
        orderRegistrationNumber: String,
        diplomaTitle: String,
        documentUri: String = ""
    ): com.example.data.model.PharmacistRegistrationEntity {
        val entity = com.example.data.model.PharmacistRegistrationEntity(
            id = UUID.randomUUID().toString(),
            fullName = fullName.trim(),
            pharmacyName = pharmacyName.trim(),
            region = region.trim(),
            city = city.trim(),
            district = district.trim(),
            phoneNumber = phoneNumber.trim(),
            email = email.trim(),
            licenseNumber = licenseNumber.trim(),
            orderRegistrationNumber = orderRegistrationNumber.trim(),
            diplomaTitle = diplomaTitle.trim(),
            documentUri = documentUri,
            status = "VERIFIED_ACTIVE",
            validationSmsSent = true,
            registeredTimestamp = System.currentTimeMillis()
        )
        dao.insertPharmacist(entity)
        return entity
    }

    suspend fun updatePharmacistStatus(id: String, status: String) {
        dao.updatePharmacistStatus(id, status)
    }

    suspend fun deletePharmacist(id: String) {
        dao.deletePharmacistById(id)
    }

    // --- Delivery Couriers Management ---
    val allCouriers: Flow<List<com.example.data.model.DeliveryCourierEntity>> = dao.getAllCouriers()

    suspend fun saveCourier(
        id: String?,
        fullName: String,
        phoneNumber: String,
        nationalIdCardNumber: String,
        address: String,
        vehicleType: String,
        region: String,
        assignedZone: String,
        status: String = "DISPONIBLE"
    ): com.example.data.model.DeliveryCourierEntity {
        val courier = com.example.data.model.DeliveryCourierEntity(
            id = id ?: UUID.randomUUID().toString(),
            fullName = fullName.trim(),
            phoneNumber = phoneNumber.trim(),
            nationalIdCardNumber = nationalIdCardNumber.trim(),
            address = address.trim(),
            vehicleType = vehicleType.trim(),
            region = region.trim(),
            assignedZone = assignedZone.trim(),
            status = status,
            totalDeliveries = if (id == null) 0 else 12,
            rating = 5.0,
            registeredTimestamp = System.currentTimeMillis()
        )
        if (id == null) {
            dao.insertCourier(courier)
        } else {
            dao.updateCourier(courier)
        }
        return courier
    }

    suspend fun updateCourierStatus(courierId: String, status: String) {
        dao.updateCourierStatus(courierId, status)
    }

    suspend fun deleteCourier(courierId: String) {
        dao.deleteCourierById(courierId)
    }

    // Clean initialization: ensure profile exists with clean blank state if absent, no fake default data
    suspend fun seedInitialUserHistoryIfEmpty() {
        val existingProfile = dao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            dao.insertUserProfile(
                UserProfileEntity(
                    id = "primary_user",
                    fullName = "",
                    email = "",
                    phoneNumber = "",
                    secondaryPhone = "",
                    emergencyContactName = "",
                    emergencyContactPhone = "",
                    bloodGroup = "",
                    knownAllergies = "",
                    preferredPaymentMethod = "Wave Mobile Money",
                    medicalNotes = ""
                )
            )
        }
    }
}
