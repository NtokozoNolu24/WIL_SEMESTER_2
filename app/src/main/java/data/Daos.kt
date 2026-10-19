package com.mzansiautorepairs.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun findById(userId: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(c: CustomerEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CustomerEntity>)
    @Query("SELECT * FROM customers") suspend fun getAll(): List<CustomerEntity>
    @Query("SELECT COUNT(*) FROM customers") suspend fun count(): Int
}

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(v: VehicleEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<VehicleEntity>)
    @Query("SELECT * FROM vehicles") suspend fun getAll(): List<VehicleEntity>
    @Query("SELECT COUNT(*) FROM vehicles") suspend fun count(): Int
}

@Dao
interface TechnicianDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<TechnicianEntity>)
    @Query("SELECT * FROM technicians") suspend fun getAll(): List<TechnicianEntity>
    @Query("SELECT COUNT(*) FROM technicians") suspend fun count(): Int
}

@Dao
interface JobCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(j: JobCardEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<JobCardEntity>)
    @Query("SELECT * FROM job_cards ORDER BY id") suspend fun getAll(): List<JobCardEntity>
    @Query("UPDATE job_cards SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)
    @Query("UPDATE job_cards SET technicianId = :techId, technicianName = :techName WHERE id = :id")
    suspend fun assignTechnician(id: String, techId: String, techName: String)
    @Query("SELECT COUNT(*) FROM job_cards") suspend fun count(): Int
}

@Dao
interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<ServiceEntity>)
    @Query("SELECT * FROM services") suspend fun getAll(): List<ServiceEntity>
    @Query("SELECT COUNT(*) FROM services") suspend fun count(): Int
}

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PaymentEntity>)
    @Query("SELECT * FROM payments") suspend fun getAll(): List<PaymentEntity>
    @Query("SELECT COUNT(*) FROM payments") suspend fun count(): Int
}
