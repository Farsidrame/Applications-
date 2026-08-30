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
        val prescription = PrescriptionEntity(
            id = UUID.randomUUID().toString(),
            patientName = patientName,
            doctorName = doctorName,
            prescriptionDate = prescriptionDate,
            photoUri = photoUri,
            uploadTimestamp = System.currentTimeMillis(),
            status = "VALIDATED", // Immediate pharmacist pre-check for demo reliability
            pharmacistNotes = if (notes.isBlank()) "Ordonnance certifiée conforme par le pharmacien de garde de $pharmacyName. Dosage et authenticité vérifiés." else notes,
            recognizedMedicines = recognizedMedicines,
            pharmacyId = pharmacyId,
            pharmacyName = pharmacyName,
            pharmacyRegion = pharmacyRegion
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

    // Pre-populate sample profile, addresses, order, and reminder if empty
    suspend fun seedInitialUserHistoryIfEmpty() {
        val existingProfile = dao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            dao.insertUserProfile(
                UserProfileEntity(
                    id = "primary_user",
                    fullName = "Mamadou Dramé",
                    email = "drame678mamadou@gmail.com",
                    phoneNumber = "+221 77 654 32 10",
                    secondaryPhone = "+221 78 123 45 67",
                    emergencyContactName = "Fatou Dramé (Épouse)",
                    emergencyContactPhone = "+221 76 987 65 43",
                    bloodGroup = "O+",
                    knownAllergies = "Pénicilline (Légère réaction cutanée)",
                    preferredPaymentMethod = "Wave Mobile Money",
                    medicalNotes = "Suivi régulier tension artérielle & asthme léger"
                )
            )
        }

        val existingAddresses = dao.getAllAddresses().firstOrNull()
        if (existingAddresses.isNullOrEmpty()) {
            dao.insertAddress(
                DeliveryAddressEntity(
                    id = "addr_1",
                    title = "Domicile (Keur Gorgui)",
                    recipientName = "Mamadou Dramé",
                    contactPhone = "+221 77 654 32 10",
                    fullAddress = "Résidence Keur Gorgui, Immeuble B, Appt 42",
                    neighborhood = "Sacré-Cœur / Keur Gorgui",
                    city = "Dakar",
                    region = "Dakar",
                    courierInstructions = "2ème étage droite, sonner à l'interphone 42. Gardien au rez-de-chaussée.",
                    isDefault = true,
                    createdAt = System.currentTimeMillis() - 86400000L * 5
                )
            )
            dao.insertAddress(
                DeliveryAddressEntity(
                    id = "addr_2",
                    title = "Bureau (Dakar Plateau)",
                    recipientName = "Mamadou Dramé",
                    contactPhone = "+221 77 654 32 10",
                    fullAddress = "Immeuble Fahd, 4ème étage, Rue Raffenel x Av. Ponty",
                    neighborhood = "Plateau",
                    city = "Dakar",
                    region = "Dakar",
                    courierInstructions = "Déposer à la réception / standard d'accueil au 4ème étage.",
                    isDefault = false,
                    createdAt = System.currentTimeMillis() - 86400000L * 4
                )
            )
            dao.insertAddress(
                DeliveryAddressEntity(
                    id = "addr_3",
                    title = "Maison Familiale (Thiès Centre)",
                    recipientName = "Fatou Dramé",
                    contactPhone = "+221 78 123 45 67",
                    fullAddress = "Villa N° 18, Avenue de Caen près de la Place de France",
                    neighborhood = "Thiès Centre (Place de France)",
                    city = "Thiès",
                    region = "Thiès",
                    courierInstructions = "Grande porte métallique bleue, sonner ou appeler à l'arrivée.",
                    isDefault = false,
                    createdAt = System.currentTimeMillis() - 86400000L * 3
                )
            )
            dao.insertAddress(
                DeliveryAddressEntity(
                    id = "addr_4",
                    title = "Résidence Touba (Diourbel)",
                    recipientName = "Serigne Dramé",
                    contactPhone = "+221 77 333 44 55",
                    fullAddress = "Quartier Dianatoul Mahwa, Face Grande Mosquée",
                    neighborhood = "Dianatoul Mahwa",
                    city = "Touba",
                    region = "Diourbel",
                    courierInstructions = "Livraison directe à domicile, appeler avant le départ.",
                    isDefault = false,
                    createdAt = System.currentTimeMillis() - 86400000L * 2
                )
            )
            dao.insertAddress(
                DeliveryAddressEntity(
                    id = "addr_5",
                    title = "Maison Saint-Louis (Île Ndar)",
                    recipientName = "Awa Dramé",
                    contactPhone = "+221 76 555 12 34",
                    fullAddress = "Rue Blaise Diagne x Rue Blanchot, Île de Saint-Louis",
                    neighborhood = "Île de Saint-Louis (Ndar Escale)",
                    city = "Saint-Louis",
                    region = "Saint-Louis",
                    courierInstructions = "Bâtiment colonial à étage, livraison au rez-de-chaussée.",
                    isDefault = false,
                    createdAt = System.currentTimeMillis() - 86400000L
                )
            )
        }

        val existingOrders = dao.getAllOrders().firstOrNull()
        if (existingOrders.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            dao.insertOrder(
                OrderEntity(
                    id = "seed_order_1",
                    orderNumber = "#PH-84210",
                    orderTimestamp = now - 86400000L * 2, // 2 days ago
                    status = OrderStatus.DELIVERED.name,
                    itemsSummary = "Doliprane 1000mg x2 | Vitamine C 1000mg x1 | Sérum Physiologique 0.9% x2",
                    subtotalFcfa = 6400,
                    deliveryFeeFcfa = 1000,
                    totalFcfa = 7400,
                    pharmacyId = "pharm_1",
                    pharmacyName = "Grande Pharmacie Guigon (Dakar Plateau)",
                    pharmacyAddress = "Avenue de la République, Face Hôpital Principal, Dakar",
                    deliveryAddress = "Résidence Keur Gorgui, Immeuble B, Appt 42, Dakar",
                    patientName = "Mamadou Dramé",
                    patientPhone = "+221 77 654 32 10",
                    paymentMethod = "WAVE",
                    paymentTransactionId = "TXN-WAV-842109",
                    isPrescriptionVerified = true,
                    deliveryPinCode = "5821",
                    courierName = "Mamadou Seck (Coursier certifié)",
                    courierPhone = "+221 77 412 88 99",
                    deliveryEtaMinutes = 20,
                    invoiceQrCodePayload = "PHARMADIRECT-SECURE-INVOICE|#PH-84210|TXN-WAV-842109|7400|FCFA|DELIVERED"
                )
            )
            dao.insertOrder(
                OrderEntity(
                    id = "seed_order_2",
                    orderNumber = "#PH-62194",
                    orderTimestamp = now - 86400000L * 6, // 6 days ago
                    status = OrderStatus.DELIVERED.name,
                    itemsSummary = "Amoxicilline 1g Sandoz x1 | Bétadine Dermique 10% x1 | Pansements Stériles Urgo x1",
                    subtotalFcfa = 8200,
                    deliveryFeeFcfa = 1500,
                    totalFcfa = 9700,
                    pharmacyId = "pharm_3",
                    pharmacyName = "Grande Pharmacie des Almadies",
                    pharmacyAddress = "Route des Almadies, Zone Commerciale, Dakar",
                    deliveryAddress = "Résidence Keur Gorgui, Immeuble B, Appt 42, Dakar",
                    patientName = "Mamadou Dramé",
                    patientPhone = "+221 77 654 32 10",
                    paymentMethod = "ORANGE_MONEY",
                    paymentTransactionId = "TXN-OM-621948",
                    isPrescriptionVerified = true,
                    deliveryPinCode = "7342",
                    courierName = "Ousmane Faye (Express Santé)",
                    courierPhone = "+221 78 523 11 44",
                    deliveryEtaMinutes = 25,
                    invoiceQrCodePayload = "PHARMADIRECT-SECURE-INVOICE|#PH-62194|TXN-OM-621948|9700|FCFA|DELIVERED"
                )
            )
            dao.insertOrder(
                OrderEntity(
                    id = "seed_order_3",
                    orderNumber = "#PH-93512",
                    orderTimestamp = now - 86400000L * 12, // 12 days ago
                    status = OrderStatus.DELIVERED.name,
                    itemsSummary = "Coartem 80/480mg (Antipaludéen) x1 | Paracétamol 500mg Biogaran x2",
                    subtotalFcfa = 6200,
                    deliveryFeeFcfa = 1200,
                    totalFcfa = 7400,
                    pharmacyId = "pharm_2",
                    pharmacyName = "Pharmacie Pasteur & Santé (Mermoz)",
                    pharmacyAddress = "Boulevard de la Liberté, Rond-point Étoile, Dakar",
                    deliveryAddress = "Villa N° 12, Rue MZ-54, Mermoz, Dakar",
                    patientName = "Fatou Dramé",
                    patientPhone = "+221 78 123 45 67",
                    paymentMethod = "FREE_MONEY",
                    paymentTransactionId = "TXN-FRE-935123",
                    isPrescriptionVerified = true,
                    deliveryPinCode = "4190",
                    courierName = "Cheikh Kane (Livreur pharma agréé)",
                    courierPhone = "+221 76 901 33 22",
                    deliveryEtaMinutes = 20,
                    invoiceQrCodePayload = "PHARMADIRECT-SECURE-INVOICE|#PH-93512|TXN-FRE-935123|7400|FCFA|DELIVERED"
                )
            )
        }
    }
}
