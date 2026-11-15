package com.mzansiautorepairs

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mzansiautorepairs.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Data holder used by every screen. Kept as "MockData" so Member 1's screens don't have to change,
 * but the lists are now loaded from, and saved to, the Room database (see data/AppDatabase.kt).
 * Call MockData.init(context) once at app start (MainActivity does this).
 */
object MockData {
    val customers: SnapshotStateList<Customer> = mutableStateListOf()
    val vehicles: SnapshotStateList<Vehicle> = mutableStateListOf()
    val technicians: SnapshotStateList<Technician> = mutableStateListOf()
    val jobCards: SnapshotStateList<JobCard> = mutableStateListOf()
    val services: SnapshotStateList<Service> = mutableStateListOf()
    val payments: SnapshotStateList<Payment> = mutableStateListOf()

    private val io = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var db: AppDatabase? = null

    /** Opens the database, seeds it on first run, then fills the lists the UI observes. */
    suspend fun init(context: Context) {
        val database = AppDatabase.get(context)
        db = database
        Seeder.seedIfEmpty(database)

        val c = database.customerDao().getAll().map { Customer(it.id, it.name, it.phone, it.email, it.address) }
        val v = database.vehicleDao().getAll().map { Vehicle(it.registration, it.make, it.model, it.year, it.ownerId) }
        val t = database.technicianDao().getAll().map { Technician(it.id, it.name, it.specialty, it.phone, it.activeJobs, it.completedJobs) }
        val j = database.jobCardDao().getAll().map {
            JobCard(it.id, it.customerId, it.customerName, it.vehicleReg, it.description, it.technicianId,
                it.technicianName, JobStatus.valueOf(it.status), it.createdDate, it.labourCost, it.partsCost)
        }
        val s = database.serviceDao().getAll().map { Service(it.id, it.name, it.description, it.price, it.durationMinutes) }
        val p = database.paymentDao().getAll().map {
            Payment(it.id, it.jobCardId, it.customerName, it.amount, PaymentMethod.valueOf(it.method),
                PaymentStatus.valueOf(it.status), it.date)
        }
        customers.replaceAll(c); vehicles.replaceAll(v); technicians.replaceAll(t)
        jobCards.replaceAll(j); services.replaceAll(s); payments.replaceAll(p)
    }

    private fun <T> SnapshotStateList<T>.replaceAll(items: List<T>) { clear(); addAll(items) }

    fun addCustomer(customer: Customer) {
        customers.add(customer)
        io.launch { db?.customerDao()?.upsert(CustomerEntity(customer.id, customer.name, customer.phone, customer.email, customer.address)) }
    }

    fun addVehicle(v: Vehicle) {
        vehicles.add(v)
        io.launch { db?.vehicleDao()?.upsert(VehicleEntity(v.registration, v.make, v.model, v.year, v.ownerId)) }
    }

    fun createJobCard(j: JobCard) {
        jobCards.add(j)
        io.launch {
            db?.jobCardDao()?.upsert(JobCardEntity(j.id, j.customerId, j.customerName, j.vehicleReg, j.description,
                j.technicianId, j.technicianName, j.status.name, j.createdDate, j.labourCost, j.partsCost))
        }
    }

    fun updateJobCardStatus(jobCardId: String, newStatus: JobStatus) {
        val index = jobCards.indexOfFirst { it.id == jobCardId }
        if (index != -1) {
            jobCards[index] = jobCards[index].copy(status = newStatus)
            io.launch { db?.jobCardDao()?.updateStatus(jobCardId, newStatus.name) }
        }
    }

    fun assignTechnicianToJobCard(jobCardId: String, technicianId: String, technicianName: String = "") {
        val resolvedName = technicianName.ifBlank { technicians.find { it.id == technicianId }?.name ?: "" }
        val index = jobCards.indexOfFirst { it.id == jobCardId }
        if (index != -1) {
            jobCards[index] = jobCards[index].copy(technicianId = technicianId, technicianName = resolvedName)
            io.launch { db?.jobCardDao()?.assignTechnician(jobCardId, technicianId, resolvedName) }
        }
    }
}

fun addCustomer(customer: Customer) = MockData.addCustomer(customer)
fun createJobCard(jobCard: JobCard) = MockData.createJobCard(jobCard)
fun updateJobCardStatus(jobCardId: String, newStatus: JobStatus) = MockData.updateJobCardStatus(jobCardId, newStatus)
fun assignTechnicianToJobCard(jobCardId: String, technicianId: String, technicianName: String = "") =
    MockData.assignTechnicianToJobCard(jobCardId, technicianId, technicianName)