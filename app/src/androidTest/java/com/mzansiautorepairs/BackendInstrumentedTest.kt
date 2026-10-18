package com.mzansiautorepairs

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mzansiautorepairs.auth.PasswordHasher
import com.mzansiautorepairs.data.AppDatabase
import com.mzansiautorepairs.data.Seeder
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Member 2: authentication + database tests. Run on a device/emulator. */
@RunWith(AndroidJUnit4::class)
class BackendInstrumentedTest {
    private lateinit var db: AppDatabase

    @Before fun setUp() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java).build()
    }

    @After fun tearDown() = db.close()

    @Test fun hashIsNotPlaintextAndVerifies() {
        val hash = PasswordHasher.hash("Secret123")
        assertNotEquals("Secret123", hash)
        assertTrue(PasswordHasher.verify("Secret123", hash))
        assertFalse(PasswordHasher.verify("Wrong123", hash))
    }

    @Test fun samePasswordGivesDifferentHashes() {
        assertNotEquals(PasswordHasher.hash("Secret123"), PasswordHasher.hash("Secret123"))
    }

    @Test fun everyTableHasAtLeastTenSeedRows() = runBlocking {
        Seeder.seedIfEmpty(db)
        assertTrue(db.userDao().count() >= 10)
        assertTrue(db.customerDao().count() >= 10)
        assertTrue(db.vehicleDao().count() >= 10)
        assertTrue(db.technicianDao().count() >= 10)
        assertTrue(db.jobCardDao().count() >= 10)
        assertTrue(db.serviceDao().count() >= 10)
        assertTrue(db.paymentDao().count() >= 10)
    }

    @Test fun seededUserCanLogInWithDemoPassword() = runBlocking {
        Seeder.seedIfEmpty(db)
        val user = db.userDao().findByEmail("THATO@mzansiauto.co.za") // case-insensitive lookup
        assertNotNull(user)
        assertTrue(PasswordHasher.verify(Seeder.DEMO_PASSWORD, user!!.passwordHash))
    }

    @Test fun jobStatusUpdatePersists() = runBlocking {
        Seeder.seedIfEmpty(db)
        db.jobCardDao().updateStatus("JC1003", "COMPLETED")
        assertEquals("COMPLETED", db.jobCardDao().getAll().first { it.id == "JC1003" }.status)
    }
}