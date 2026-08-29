package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CartItemEntity
import com.example.data.model.DeliveryAddressEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PrescriptionEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PharmaDao {

    // --- Cart ---
    @Query("SELECT * FROM cart_items ORDER BY id ASC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE medicineId = :medicineId LIMIT 1")
    suspend fun getCartItemByMedicineId(medicineId: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY orderTimestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderById(id: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: String)

    // --- Prescriptions ---
    @Query("SELECT * FROM prescriptions ORDER BY uploadTimestamp DESC")
    fun getAllPrescriptions(): Flow<List<PrescriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(prescription: PrescriptionEntity)

    @Query("UPDATE prescriptions SET status = :status, pharmacistNotes = :notes WHERE id = :id")
    suspend fun updatePrescriptionStatus(id: String, status: String, notes: String)

    @Query("DELETE FROM prescriptions WHERE id = :id")
    suspend fun deletePrescriptionById(id: String)

    // --- Reminders ---
    @Query("SELECT * FROM medication_reminders ORDER BY id ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Query("DELETE FROM medication_reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Int)

    @Query("UPDATE medication_reminders SET isActive = :isActive WHERE id = :id")
    suspend fun toggleReminder(id: Int, isActive: Boolean)

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 'primary_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)

    // --- Delivery Addresses ---
    @Query("SELECT * FROM delivery_addresses ORDER BY isDefault DESC, createdAt DESC")
    fun getAllAddresses(): Flow<List<DeliveryAddressEntity>>

    @Query("SELECT * FROM delivery_addresses WHERE id = :id LIMIT 1")
    suspend fun getAddressById(id: String): DeliveryAddressEntity?

    @Query("SELECT * FROM delivery_addresses WHERE isDefault = 1 LIMIT 1")
    fun getDefaultAddress(): Flow<DeliveryAddressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: DeliveryAddressEntity)

    @Update
    suspend fun updateAddress(address: DeliveryAddressEntity)

    @Query("DELETE FROM delivery_addresses WHERE id = :id")
    suspend fun deleteAddressById(id: String)

    @Query("UPDATE delivery_addresses SET isDefault = 0")
    suspend fun clearDefaultAddresses()

    @Query("UPDATE delivery_addresses SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultAddress(id: String)
}
