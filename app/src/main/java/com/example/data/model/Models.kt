package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

enum class OrderStatus(val label: String, val stepIndex: Int) {
    PENDING_PAYMENT("En attente de paiement", 0),
    PAID_CONFIRMED("Paiement sécurisé validé", 1),
    PHARMACIST_PREPARING("Préparation & Contrôle Pharmacien", 2),
    SEALED_DISPATCHED("Colis scellé & Attribué au coursier", 3),
    OUT_FOR_DELIVERY("En cours de livraison express", 4),
    DELIVERED("Livré avec succès", 5),
    CANCELLED("Annulée", -1)
}

enum class PaymentMethod(val displayName: String, val feeDescription: String) {
    ORANGE_MONEY("Orange Money", "Paiement direct sans frais"),
    WAVE("Wave Mobile Money", "Paiement instantané 0%"),
    MTN_MOMO("MTN MoMo", "Paiement mobile sécurisé"),
    CREDIT_CARD("Carte Bancaire (Visa / Mastercard)", "Paiement sécurisé 3D-Secure"),
    ESCROW_WALLET("Garantie Sécurisée (Fonds bloqués jusqu'à réception)", "Sécurité maximale anti-fraude")
}

enum class PrescriptionStatus(val label: String) {
    PENDING_VALIDATION("En cours de vérification par le pharmacien"),
    VALIDATED("Validée & Prête pour commande"),
    REJECTED("Non conforme ou illisible"),
    ORDER_CREATED("Commande effectuée")
}

data class Pharmacy(
    val id: String,
    val name: String,
    val address: String,
    val district: String,
    val city: String = "Dakar",
    val region: String = "Dakar",
    val distanceKm: Double,
    val isDutyPharmacy: Boolean, // Pharmacie de garde
    val rating: Double,
    val reviewCount: Int,
    val phoneNumber: String,
    val openingHours: String,
    val isCertified: Boolean,
    val estimatedDeliveryMinutes: Int,
    val deliveryFeeFcfa: Int,
    val pharmacistInCharge: String,
    val latitude: Double = 14.6937,
    val longitude: Double = -17.4441
) : Serializable

data class Medicine(
    val id: String,
    val name: String,
    val brand: String,
    val dci: String, // Molecule
    val category: String,
    val dosageForm: String, // Comprimé, Sirop, Gélule, etc.
    val dosageStrength: String, // 1000mg, 500mg...
    val priceFcfa: Int,
    val requiresPrescription: Boolean,
    val description: String,
    val posology: String,
    val contraindications: String,
    val stockQuantity: Int,
    val pharmacyId: String,
    val isPopular: Boolean = false
) : Serializable

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicineId: String,
    val medicineName: String,
    val pharmacyId: String,
    val pharmacyName: String,
    val priceFcfa: Int,
    val quantity: Int,
    val requiresPrescription: Boolean,
    val dosageForm: String,
    val dosageStrength: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val orderNumber: String,
    val orderTimestamp: Long,
    val status: String,
    val itemsSummary: String,
    val subtotalFcfa: Int,
    val deliveryFeeFcfa: Int,
    val totalFcfa: Int,
    val pharmacyId: String,
    val pharmacyName: String,
    val pharmacyAddress: String,
    val deliveryAddress: String,
    val patientName: String,
    val patientPhone: String,
    val paymentMethod: String,
    val paymentTransactionId: String,
    val isPrescriptionVerified: Boolean,
    val deliveryPinCode: String,
    val courierName: String,
    val courierPhone: String,
    val deliveryEtaMinutes: Int,
    val invoiceQrCodePayload: String
)

data class PrescribedMedicineItem(
    val medicineName: String,
    val dosage: String,
    val quantity: Int = 1,
    val priceFcfa: Int = 3200,
    val isAvailable: Boolean = true,
    val availabilityNote: String = "En stock à l'officine",
    val matchedMedicineId: String? = null
) : Serializable

@Entity(tableName = "prescriptions")
data class PrescriptionEntity(
    @PrimaryKey
    val id: String,
    val patientName: String,
    val doctorName: String,
    val prescriptionDate: String,
    val photoUri: String,
    val uploadTimestamp: Long,
    val status: String,
    val pharmacistNotes: String,
    val recognizedMedicines: String,
    val pharmacyId: String = "pharm_1",
    val pharmacyName: String = "Grande Pharmacie Guigon (Dakar)",
    val pharmacyRegion: String = "Dakar",
    val pharmacistName: String = "Dr. Aminata Fall (Docteur en Pharmacie)",
    val pharmacistReviewStatus: String = "VALIDATED", // AWAITING_REVIEW, VALIDATED, PARTIALLY_AVAILABLE
    val availableMedicinesSummary: String = "Amoxicilline 1g Sandoz (Disponible • 3 200 FCFA) | Ventoline 100µg Inhalateur (Disponible • 4 500 FCFA)",
    val totalEstimatedFcfa: Int = 7700
)

@Entity(tableName = "medication_reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicineName: String,
    val dosage: String,
    val time: String,
    val instructions: String,
    val isActive: Boolean = true
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "primary_user",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val secondaryPhone: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val bloodGroup: String = "",
    val knownAllergies: String = "",
    val preferredPaymentMethod: String = "Wave Mobile Money",
    val medicalNotes: String = ""
)

@Entity(tableName = "pharmacist_registrations")
data class PharmacistRegistrationEntity(
    @PrimaryKey
    val id: String,
    val fullName: String,
    val pharmacyName: String,
    val region: String,
    val city: String,
    val district: String,
    val phoneNumber: String,
    val email: String,
    val licenseNumber: String,
    val orderRegistrationNumber: String,
    val diplomaTitle: String,
    val documentUri: String = "",
    val status: String = "VERIFIED_ACTIVE", // "PENDING_VALIDATION", "VERIFIED_ACTIVE"
    val validationSmsSent: Boolean = true,
    val registeredTimestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "delivery_couriers")
data class DeliveryCourierEntity(
    @PrimaryKey
    val id: String,
    val fullName: String,
    val phoneNumber: String,
    val nationalIdCardNumber: String, // CNI / NIN
    val address: String,
    val vehicleType: String = "Moto", // "Moto", "Scooter électrique", "Vélo cargo", "Véhicule frigorifique"
    val region: String = "Dakar",
    val assignedZone: String = "Dakar & Banlieue",
    val status: String = "DISPONIBLE", // "DISPONIBLE", "EN_COURSE", "HORS_LIGNE"
    val totalDeliveries: Int = 0,
    val rating: Double = 5.0,
    val registeredTimestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "delivery_addresses")
data class DeliveryAddressEntity(
    @PrimaryKey
    val id: String,
    val title: String, // e.g. "Domicile", "Bureau", "Villa Mermoz"
    val recipientName: String,
    val contactPhone: String,
    val fullAddress: String,
    val neighborhood: String,
    val city: String = "Dakar",
    val region: String = "Dakar",
    val courierInstructions: String = "",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class LiveOrderEvent(
    val timestamp: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean = false
)

data class CourierChatMessage(
    val id: String,
    val sender: String, // "courier" or "user"
    val senderName: String,
    val text: String,
    val timestamp: String
)

data class LiveCourierTelemetry(
    val progress: Float = 0.65f, // 0.0 to 1.0 along the Dakar trajectory
    val currentStreet: String = "Avenue Cheikh Anta Diop, près Université UCAD",
    val distanceRemainingMeters: Int = 850,
    val etaSeconds: Int = 380, // ~6 minutes
    val speedKmh: Int = 32,
    val temperatureCelsius: Double = 4.8,
    val coldChainCompliant: Boolean = true,
    val courierName: String = "Mamadou Ndiaye",
    val courierPhone: String = "+221 77 412 89 00",
    val courierVehicle: String = "Scooter Électrique • DK-4281-AZ",
    val courierRating: Double = 4.95,
    val courierDeliveriesCount: Int = 482,
    val courierPhotoUrl: String = "",
    val liveTrackingActive: Boolean = true
)

data class UserGpsLocation(
    val latitude: Double = 14.6937,
    val longitude: Double = -17.4441,
    val addressName: String = "Résidence Keur Gorgui, Dakar",
    val district: String = "Sacré-Cœur / Keur Gorgui",
    val isAutoDetected: Boolean = true
) : Serializable

data class SmsDeliveryNotification(
    val id: String,
    val orderId: String,
    val orderNumber: String,
    val sender: String = "PharmaDirect-SN",
    val recipientPhone: String,
    val messageText: String,
    val timestamp: String,
    val pharmacyName: String,
    val isRead: Boolean = false
) : Serializable

enum class PharmacySortOption(val label: String, val shortLabel: String) {
    PROXIMITY("Plus proches (GPS)", "Distance"),
    RATING("Mieux notées", "Note"),
    SPEED("Livraison la plus rapide", "Délai"),
    FEE("Frais de livraison min.", "Frais")
}

