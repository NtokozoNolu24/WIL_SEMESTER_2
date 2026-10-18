package com.mzansiautorepairs.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class, CustomerEntity::class, VehicleEntity::class,
        TechnicianEntity::class, JobCardEntity::class, ServiceEntity::class,
        PaymentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun customerDao(): CustomerDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun technicianDao(): TechnicianDao
    abstract fun jobCardDao(): JobCardDao
    abstract fun serviceDao(): ServiceDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mzansi_auto_repairs.db"
                ).build().also { instance = it }
            }
    }
}