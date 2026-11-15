package com.mzansiautorepairs

enum class UserRole {
    ADMIN,
    TECHNICIAN,
    RECEPTIONIST
}

enum class JobStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

enum class PaymentMethod {
    CASH,
    CARD,
    EFT
}

enum class PaymentStatus {
    PAID,
    PENDING,
    REFUNDED
}

data class Customer(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val address: String
)

data class Vehicle(
    val registration: String,
    val make: String,
    val model: String,
    val year: Int,
    val ownerId: String
)

data class Technician(
    val id: String,
    val name: String,
    val specialty: String,
    val phone: String,
    val activeJobs: Int,
    val completedJobs: Int
)

data class JobCard(
    val id: String,
    val customerId: String,
    val customerName: String,
    val vehicleReg: String,
    val description: String,
    val technicianId: String,
    val technicianName: String,
    var status: JobStatus,
    val createdDate: String,
    var labourCost: Double,
    var partsCost: Double
) {
    val totalCost: Double
        get() = labourCost + partsCost
}

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val durationMinutes: Int
)

data class Payment(
    val id: String,
    val jobCardId: String,
    val customerName: String,
    val amount: Double,
    val method: PaymentMethod,
    val status: PaymentStatus,
    val date: String
)

data class ServiceRecord(
    val id: String,
    val vehicleReg: String,
    val customerName: String,
    val serviceDate: String,
    val description: String,
    val cost: Double,
    val status: String
)
