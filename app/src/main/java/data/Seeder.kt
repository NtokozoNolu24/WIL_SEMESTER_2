package com.mzansiautorepairs.data

import com.mzansiautorepairs.auth.PasswordHasher

/** Inserts 10+ sample rows per table the first time the app runs (only if the table is empty). */
object Seeder {
    const val DEMO_PASSWORD = "Password123"

    suspend fun seedIfEmpty(db: AppDatabase) {
        if (db.userDao().count() == 0) {
            val hash = PasswordHasher.hash(DEMO_PASSWORD) // one hash reused so seeding stays fast
            val u = listOf(
                Triple("Thato Menetje", "thato@mzansiauto.co.za", "ADMIN"),
                Triple("Nomsa Dube", "nomsa@mzansiauto.co.za", "RECEPTIONIST"),
                Triple("Sipho Nkosi", "sipho@mzansiauto.co.za", "TECHNICIAN"),
                Triple("Thabo Mokoena", "thabo@mzansiauto.co.za", "TECHNICIAN"),
                Triple("Lerato Khumalo", "lerato@mzansiauto.co.za", "TECHNICIAN"),
                Triple("Pieter van Wyk", "pieter@mzansiauto.co.za", "ADMIN"),
                Triple("Ayanda Zulu", "ayanda@mzansiauto.co.za", "RECEPTIONIST"),
                Triple("Kabelo Sithole", "kabelo@mzansiauto.co.za", "TECHNICIAN"),
                Triple("Fatima Patel", "fatima@mzansiauto.co.za", "RECEPTIONIST"),
                Triple("Bongani Ndlovu", "bongani@mzansiauto.co.za", "TECHNICIAN")
            )
            db.userDao().insertAll(u.mapIndexed { i, (n, e, r) ->
                UserEntity("U%03d".format(i + 1), n, e, "082000%04d".format(1000 + i), r, hash)
            })
        }
        if (db.customerDao().count() == 0) db.customerDao().insertAll(listOf(
            CustomerEntity("C001", "John Dlamini", "0821234567", "john@mail.com", "12 Sandton Drive"),
            CustomerEntity("C002", "Thabo M.", "0712345678", "thabo@mail.com", "45 Rosebank Ave"),
            CustomerEntity("C003", "Sipho N.", "0839876543", "sipho@mail.com", "78 Midrand Rd"),
            CustomerEntity("C004", "Naledi Mahlangu", "0724561234", "naledi@mail.com", "9 Fourways Blvd"),
            CustomerEntity("C005", "Pravin Naidoo", "0835557777", "pravin@mail.com", "23 Bryanston Dr"),
            CustomerEntity("C006", "Zanele Mkhize", "0761239876", "zanele@mail.com", "5 Randburg Rd"),
            CustomerEntity("C007", "Johan Botha", "0827894561", "johan@mail.com", "31 Parkhurst St"),
            CustomerEntity("C008", "Lindiwe Cele", "0713216549", "lindiwe@mail.com", "64 Melville Ave"),
            CustomerEntity("C009", "Mpho Kgosana", "0846549871", "mpho@mail.com", "17 Kyalami Rd"),
            CustomerEntity("C010", "Aisha Khan", "0798521473", "aisha@mail.com", "88 Lonehill Way"),
            CustomerEntity("C011", "Ruan Pretorius", "0823691472", "ruan@mail.com", "2 Sunninghill Cres"),
            CustomerEntity("C012", "Nkosana Radebe", "0731597531", "nkosana@mail.com", "40 Morningside Dr")
        ))
        if (db.vehicleDao().count() == 0) db.vehicleDao().insertAll(listOf(
            VehicleEntity("CA 123-456", "Toyota", "Corolla", 2019, "C001"),
            VehicleEntity("GP 789-012", "VW", "Polo", 2021, "C002"),
            VehicleEntity("JHB 345-678", "Ford", "Ranger", 2018, "C003"),
            VehicleEntity("GP 111-222", "Hyundai", "i20", 2020, "C004"),
            VehicleEntity("GP 333-444", "Toyota", "Hilux", 2017, "C005"),
            VehicleEntity("GP 555-666", "Nissan", "NP200", 2016, "C006"),
            VehicleEntity("GP 777-888", "BMW", "320i", 2019, "C007"),
            VehicleEntity("GP 999-000", "Kia", "Picanto", 2022, "C008"),
            VehicleEntity("GP 246-810", "Renault", "Kwid", 2021, "C009"),
            VehicleEntity("GP 135-790", "Mazda", "CX-5", 2018, "C010"),
            VehicleEntity("GP 864-209", "Suzuki", "Swift", 2020, "C011"),
            VehicleEntity("GP 975-313", "Isuzu", "D-Max", 2019, "C012")
        ))
        if (db.technicianDao().count() == 0) db.technicianDao().insertAll(listOf(
            TechnicianEntity("T001", "Sipho Nkosi", "Engine & Diagnostics", "0821112222", 2, 15),
            TechnicianEntity("T002", "Thabo Mokoena", "Brakes & Suspension", "0823334444", 1, 20),
            TechnicianEntity("T003", "Lerato Khumalo", "Electrical Systems", "0825556666", 0, 10),
            TechnicianEntity("T004", "Kabelo Sithole", "Gearbox & Clutch", "0827778888", 1, 12),
            TechnicianEntity("T005", "Bongani Ndlovu", "Air Conditioning", "0829990000", 0, 8),
            TechnicianEntity("T006", "Sibusiso Dlamini", "Tyres & Alignment", "0821231234", 1, 18),
            TechnicianEntity("T007", "Palesa Molefe", "Diagnostics", "0824564567", 0, 9),
            TechnicianEntity("T008", "Andile Mthembu", "Bodywork", "0827897890", 2, 14),
            TechnicianEntity("T009", "Refilwe Mokoena", "Engine Rebuilds", "0821472583", 1, 22),
            TechnicianEntity("T010", "Dumisani Zungu", "General Servicing", "0823692581", 0, 11)
        ))
        if (db.serviceDao().count() == 0) db.serviceDao().insertAll(listOf(
            ServiceEntity("S001", "Vehicle Servicing", "Full annual vehicle inspection and service", 850.0, 90),
            ServiceEntity("S002", "Repairs", "General brake and engine repairs", 1200.0, 180),
            ServiceEntity("S003", "Diagnostics", "Computer diagnostic scan and fault finding", 450.0, 60),
            ServiceEntity("S004", "Maintenance", "Fluid top-ups, wheel alignment, filter swap", 600.0, 120),
            ServiceEntity("S005", "Oil Change", "Engine oil and filter replacement", 350.0, 45),
            ServiceEntity("S006", "Brake Pads", "Front or rear brake pad replacement", 700.0, 90),
            ServiceEntity("S007", "Wheel Alignment", "Four-wheel alignment and balancing", 400.0, 60),
            ServiceEntity("S008", "Aircon Regas", "Air conditioning regas and leak check", 650.0, 75),
            ServiceEntity("S009", "Battery Replacement", "Test and replace car battery", 900.0, 30),
            ServiceEntity("S010", "Clutch Repair", "Clutch plate and pressure plate replacement", 2500.0, 300)
        ))
        if (db.jobCardDao().count() == 0) db.jobCardDao().insertAll(listOf(
            job("JC1001", "C001", "John Dlamini", "CA 123-456", "Engine check", "T001", "Sipho Nkosi", "IN_PROGRESS", "2026-09-20", 350.0, 100.0),
            job("JC1002", "C002", "Thabo M.", "GP 789-012", "Brake repair", "T002", "Thabo Mokoena", "COMPLETED", "2026-09-15", 800.0, 400.0),
            job("JC1003", "C003", "Sipho N.", "JHB 345-678", "Oil change", "T001", "Sipho Nkosi", "PENDING", "2026-09-22", 300.0, 150.0),
            job("JC1004", "C001", "John Dlamini", "CA 123-456", "Suspension noise", "T002", "Thabo Mokoena", "IN_PROGRESS", "2026-09-23", 500.0, 250.0),
            job("JC1005", "C004", "Naledi Mahlangu", "GP 111-222", "Aircon not cooling", "T005", "Bongani Ndlovu", "COMPLETED", "2026-09-25", 400.0, 250.0),
            job("JC1006", "C005", "Pravin Naidoo", "GP 333-444", "Clutch slipping", "T004", "Kabelo Sithole", "IN_PROGRESS", "2026-09-27", 1500.0, 1000.0),
            job("JC1007", "C006", "Zanele Mkhize", "GP 555-666", "Battery replacement", "T003", "Lerato Khumalo", "COMPLETED", "2026-09-28", 150.0, 750.0),
            job("JC1008", "C007", "Johan Botha", "GP 777-888", "Full service", "T010", "Dumisani Zungu", "PENDING", "2026-10-01", 450.0, 400.0),
            job("JC1009", "C008", "Lindiwe Cele", "GP 999-000", "Wheel alignment", "T006", "Sibusiso Dlamini", "COMPLETED", "2026-10-03", 400.0, 0.0),
            job("JC1010", "C009", "Mpho Kgosana", "GP 246-810", "Check engine light", "T007", "Palesa Molefe", "CANCELLED", "2026-10-05", 450.0, 0.0),
            job("JC1011", "C010", "Aisha Khan", "GP 135-790", "Brake pads", "T002", "Thabo Mokoena", "IN_PROGRESS", "2026-10-08", 350.0, 350.0),
            job("JC1012", "C011", "Ruan Pretorius", "GP 864-209", "Oil change", "T010", "Dumisani Zungu", "PENDING", "2026-10-10", 150.0, 200.0)
        ))
        if (db.paymentDao().count() == 0) db.paymentDao().insertAll(listOf(
            pay("P001", "JC1002", "Thabo M.", 1200.0, "CARD", "PAID", "2026-09-15"),
            pay("P002", "JC1001", "John Dlamini", 450.0, "EFT", "PAID", "2026-09-20"),
            pay("P003", "JC1004", "John Dlamini", 350.0, "CASH", "PENDING", "2026-09-23"),
            pay("P004", "JC1005", "Naledi Mahlangu", 650.0, "CARD", "PAID", "2026-09-25"),
            pay("P005", "JC1006", "Pravin Naidoo", 1250.0, "EFT", "PENDING", "2026-09-27"),
            pay("P006", "JC1007", "Zanele Mkhize", 900.0, "CASH", "PAID", "2026-09-28"),
            pay("P007", "JC1008", "Johan Botha", 425.0, "CARD", "PENDING", "2026-10-01"),
            pay("P008", "JC1009", "Lindiwe Cele", 400.0, "EFT", "PAID", "2026-10-03"),
            pay("P009", "JC1010", "Mpho Kgosana", 450.0, "CARD", "REFUNDED", "2026-10-05"),
            pay("P010", "JC1011", "Aisha Khan", 350.0, "CASH", "PENDING", "2026-10-08"),
            pay("P011", "JC1003", "Sipho N.", 225.0, "EFT", "PENDING", "2026-09-22"),
            pay("P012", "JC1012", "Ruan Pretorius", 175.0, "CASH", "PENDING", "2026-10-10")
        ))
    }

    private fun job(id: String, cId: String, cName: String, reg: String, desc: String, tId: String,
                    tName: String, status: String, date: String, labour: Double, parts: Double) =
        JobCardEntity(id, cId, cName, reg, desc, tId, tName, status, date, labour, parts)

    private fun pay(id: String, jobId: String, name: String, amt: Double, method: String, status: String, date: String) =
        PaymentEntity(id, jobId, name, amt, method, status, date)
}