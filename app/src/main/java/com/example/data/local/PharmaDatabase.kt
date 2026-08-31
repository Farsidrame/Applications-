package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.DeliveryAddressEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PrescriptionEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.UserProfileEntity

@Database(
    entities = [
        CartItemEntity::class,
        OrderEntity::class,
        PrescriptionEntity::class,
        ReminderEntity::class,
        UserProfileEntity::class,
        DeliveryAddressEntity::class,
        com.example.data.model.PharmacistRegistrationEntity::class,
        com.example.data.model.DeliveryCourierEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class PharmaDatabase : RoomDatabase() {

    abstract fun pharmaDao(): PharmaDao

    companion object {
        @Volatile
        private var INSTANCE: PharmaDatabase? = null

        fun getDatabase(context: Context): PharmaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PharmaDatabase::class.java,
                    "pharmadirect_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
