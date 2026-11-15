package com.mzansiautorepairs.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String,          // stores the UserRole name, e.g. "ADMIN"
    val passwordHash: String   // never the plain password
)
@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val address: String
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val registration: String,
    val make: String,
    val model: String,
    val year: Int,
    val ownerId: String        // the customer's id
)

@Entity(tableName = "technicians")
data class TechnicianEntity(
    @PrimaryKey val id: String,
    val name: String,
    val specialty: String,
    val phone: String,
    val activeJobs: Int,
    val completedJobs: Int
)

@Entity(tableName = "job_cards")
data class JobCardEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val customerName: String,
    val vehicleReg: String,
    val description: String,
    val technicianId: String,
    val technicianName: String,
    val status: String,        // stores the JobStatus name, e.g. "PENDING"
    val createdDate: String,
    val labourCost: Double,
    val partsCost: Double
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val durationMinutes: Int
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val jobCardId: String,
    val customerName: String,
    val amount: Double,
    val method: String,        // stores the PaymentMethod name, e.g. "CARD"
    val status: String,        // stores the PaymentStatus name, e.g. "PAID"
    val date: String
)

