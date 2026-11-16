
package com.mzansiautorepairs.auth

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mzansiautorepairs.UserRole
import com.mzansiautorepairs.data.AppDatabase
import com.mzansiautorepairs.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private val Context.sessionStore by preferencesDataStore(name = "session")

private val KEY_USER_ID = stringPreferencesKey("user_id")

// AUTHENTICATION RESULTS
sealed class AuthResult {

    data class Success(val user: UserEntity) : AuthResult()

    data class PendingApproval(val message: String) : AuthResult()

    data class Error(val message: String) : AuthResult()
}

// PENDING STAFF APPLICATION MODEL
data class PendingStaffApplication(
    val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val requestedRole: String
)

class AuthRepository(private val context: Context) {

    private val userDao = AppDatabase.get(context).userDao()

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val firestore = FirebaseFirestore.getInstance()

    companion object {

        private const val TAG = "AuthRepository"

        private const val USERS_COLLECTION = "users"

        private const val STATUS_PENDING = "PENDING"

        private const val STATUS_APPROVED = "APPROVED"

        private const val STATUS_REJECTED = "REJECTED"

        private const val ROLE_PENDING = "PENDING"

        private const val REGISTRATION_SUCCESS_MESSAGE =
            "Registration submitted successfully. " +
                    "Your account is awaiting administrator approval. " +
                    "You can sign in once your account has been approved."
    }

    // ------------------------------------------------
    // EMAIL AND PASSWORD REGISTRATION
    // ------------------------------------------------

    suspend fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        role: UserRole
    ): AuthResult = withContext(Dispatchers.IO) {

        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.filter { it.isDigit() }

        val validationError = validateRegistration(
            cleanName,
            cleanEmail,
            cleanPhone,
            password,
            confirmPassword
        )

        if (validationError != null) {
            return@withContext AuthResult.Error(validationError)
        }

        try {

            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(cleanEmail, password)
                .await()

            val firebaseUser = authResult.user
                ?: return@withContext AuthResult.Error(
                    "Account creation failed. Please try again."
                )

            try {

                createPendingProfile(
                    uid = firebaseUser.uid,
                    fullName = cleanName,
                    email = cleanEmail,
                    phone = cleanPhone,
                    requestedRole = role,
                    provider = "EMAIL"
                )

                firebaseAuth.signOut()

                AuthResult.PendingApproval(REGISTRATION_SUCCESS_MESSAGE)

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Failed to save pending user profile",
                    e
                )

                try {
                    firebaseUser.delete().await()
                } catch (deleteError: Exception) {

                    Log.e(
                        TAG,
                        "Could not delete incomplete Firebase account",
                        deleteError
                    )

                } finally {
                    firebaseAuth.signOut()
                }

                AuthResult.Error(
                    "Your profile could not be saved. Please try registering again."
                )
            }

        } catch (e: FirebaseAuthUserCollisionException) {

            AuthResult.Error(
                "An account with this email already exists."
            )

        } catch (e: FirebaseAuthWeakPasswordException) {

            AuthResult.Error(
                "Your password is too weak. Use at least 8 characters."
            )

        } catch (e: Exception) {

            Log.e(TAG, "Email registration failed", e)

            firebaseAuth.signOut()

            AuthResult.Error(readableError(e))
        }
    }

    // ------------------------------------------------
    // GOOGLE REGISTRATION
    // ------------------------------------------------

    suspend fun registerWithGoogle(
        fullName: String,
        email: String,
        phone: String,
        role: UserRole
    ): AuthResult = withContext(Dispatchers.IO) {

        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.filter { it.isDigit() }

        if (cleanName.isBlank()) {

            firebaseAuth.signOut()

            return@withContext AuthResult.Error(
                "Full name is required."
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {

            firebaseAuth.signOut()

            return@withContext AuthResult.Error(
                "Enter a valid email address."
            )
        }

        if (cleanPhone.length != 10 || !cleanPhone.startsWith("0")) {

            firebaseAuth.signOut()

            return@withContext AuthResult.Error(
                "Enter a valid South African phone number with exactly 10 digits, starting with 0."
            )
        }

        val firebaseUser = firebaseAuth.currentUser
            ?: return@withContext AuthResult.Error(
                "Your Google session has expired. Please sign in again."
            )

        if (firebaseUser.email?.trim()?.lowercase() != cleanEmail) {

            firebaseAuth.signOut()

            return@withContext AuthResult.Error(
                "The Google account does not match the supplied email."
            )
        }

        try {

            val profileReference = firestore
                .collection(USERS_COLLECTION)
                .document(firebaseUser.uid)

            val existingProfile = profileReference.get().await()

            if (existingProfile.exists()) {

                val status = existingProfile.getString("status")

                firebaseAuth.signOut()

                return@withContext if (status == STATUS_APPROVED) {

                    AuthResult.Error(
                        "This account is already registered. Please sign in."
                    )

                } else {

                    AuthResult.Error(
                        "This account has already been registered and is awaiting administrator approval."
                    )
                }
            }

            createPendingProfile(
                uid = firebaseUser.uid,
                fullName = cleanName,
                email = cleanEmail,
                phone = cleanPhone,
                requestedRole = role,
                provider = "GOOGLE"
            )

            firebaseAuth.signOut()

            AuthResult.PendingApproval(REGISTRATION_SUCCESS_MESSAGE)

        } catch (e: Exception) {

            Log.e(TAG, "Google registration failed", e)

            firebaseAuth.signOut()

            AuthResult.Error(
                "Google registration could not be completed. Please try again."
            )
        }
    }

    // ------------------------------------------------
    // LOOK UP AN EXISTING GOOGLE USER
    // ------------------------------------------------

    suspend fun findGoogleUser(email: String): UserEntity? =
        withContext(Dispatchers.IO) {

            val firebaseUser = firebaseAuth.currentUser
                ?: return@withContext null

            if (
                firebaseUser.email?.trim()?.lowercase() !=
                email.trim().lowercase()
            ) {

                firebaseAuth.signOut()

                return@withContext null
            }

            try {

                val snapshot = firestore
                    .collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                if (!snapshot.exists()) {

                    firebaseAuth.signOut()

                    return@withContext null
                }

                if (snapshot.getString("status") != STATUS_APPROVED) {

                    firebaseAuth.signOut()

                    return@withContext null
                }

                val user = snapshot.toLocalUser(firebaseUser.uid)

                if (user.role !in UserRole.entries.map { it.name }) {

                    firebaseAuth.signOut()

                    return@withContext null
                }

                cacheUser(user)

                saveSession(user.id)

                user

            } catch (e: Exception) {

                Log.e(TAG, "Google user lookup failed", e)

                firebaseAuth.signOut()

                null
            }
        }

    // ------------------------------------------------
    // EMAIL AND PASSWORD LOGIN
    // ------------------------------------------------

    suspend fun login(
        email: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {

        val cleanEmail = email.trim().lowercase()

        if (cleanEmail.isBlank() || password.isBlank()) {

            return@withContext AuthResult.Error(
                "Enter your email and password."
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {

            return@withContext AuthResult.Error(
                "Enter a valid email address."
            )
        }

        try {

            val result = firebaseAuth
                .signInWithEmailAndPassword(cleanEmail, password)
                .await()

            val firebaseUser = result.user
                ?: return@withContext AuthResult.Error(
                    "Login failed. Please try again."
                )

            val profile = firestore
                .collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!profile.exists()) {

                firebaseAuth.signOut()

                return@withContext AuthResult.Error(
                    "Your staff profile was not found. Please contact the administrator."
                )
            }

            val status = profile.getString("status")

            if (status != STATUS_APPROVED) {

                firebaseAuth.signOut()

                return@withContext AuthResult.Error(
                    if (status == STATUS_PENDING) {
                        "Your account is awaiting administrator approval."
                    } else {
                        "Your account is not active. Please contact the administrator."
                    }
                )
            }

            val user = profile.toLocalUser(firebaseUser.uid)

            if (user.role !in UserRole.entries.map { it.name }) {

                firebaseAuth.signOut()

                return@withContext AuthResult.Error(
                    "Your account has an invalid staff role. Contact the administrator."
                )
            }

            cacheUser(user)

            saveSession(user.id)

            AuthResult.Success(user)

        } catch (e: FirebaseAuthInvalidCredentialsException) {

            firebaseAuth.signOut()

            AuthResult.Error("Incorrect email or password.")

        } catch (e: Exception) {

            Log.e(TAG, "Login failed", e)

            firebaseAuth.signOut()

            AuthResult.Error(readableError(e))
        }
    }

    // ------------------------------------------------
    // SEND PASSWORD RESET EMAIL
    // ------------------------------------------------

    suspend fun sendPasswordResetEmail(email: String): String? =
        withContext(Dispatchers.IO) {

            val cleanEmail = email.trim().lowercase()

            if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {

                return@withContext "Enter a valid email address."
            }

            try {

                firebaseAuth
                    .sendPasswordResetEmail(cleanEmail)
                    .await()

                null

            } catch (e: Exception) {

                Log.e(TAG, "Password reset request failed", e)

                readableError(e)
            }
        }

    // ------------------------------------------------
    // LOGOUT
    // ------------------------------------------------

    suspend fun logout() {

        firebaseAuth.signOut()

        context.sessionStore.edit {
            it.remove(KEY_USER_ID)
        }
    }

    // ------------------------------------------------
    // GET CURRENT USER
    // ------------------------------------------------

    suspend fun getCurrentUser(): UserEntity? {

        val userId = context.sessionStore.data
            .map { it[KEY_USER_ID] }
            .first()
            ?: return null

        if (firebaseAuth.currentUser?.uid != userId) {
            return null
        }

        return withContext(Dispatchers.IO) {
            userDao.findById(userId)
        }
    }

    // ------------------------------------------------
    // CHECK LOGIN STATUS
    // ------------------------------------------------

    suspend fun isLoggedIn(): Boolean {

        val savedId = context.sessionStore.data
            .map { it[KEY_USER_ID] }
            .first()

        return firebaseAuth.currentUser?.uid != null &&
                firebaseAuth.currentUser?.uid == savedId
    }

    // ------------------------------------------------
    // CHECK ADMIN PERMISSION
    // ------------------------------------------------

    suspend fun isCurrentUserAdmin(): Boolean {

        val currentUser = firebaseAuth.currentUser
            ?: return false

        return try {

            val document = firestore
                .collection(USERS_COLLECTION)
                .document(currentUser.uid)
                .get()
                .await()

            document.exists() &&
                    document.getString("role") == UserRole.ADMIN.name &&
                    document.getString("status") == STATUS_APPROVED

        } catch (e: Exception) {

            Log.e(TAG, "Admin verification failed", e)

            false
        }
    }

    // ------------------------------------------------
    // GET PENDING STAFF APPLICATIONS
    // ------------------------------------------------

    suspend fun getPendingStaffApplications():
            List<PendingStaffApplication> =
        withContext(Dispatchers.IO) {

            if (!isCurrentUserAdmin()) {

                throw SecurityException(
                    "Only an approved administrator can view staff applications."
                )
            }

            val snapshot = firestore
                .collection(USERS_COLLECTION)
                .whereEqualTo("status", STATUS_PENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->

                val requestedRole =
                    document.getString("requestedRole")

                if (
                    requestedRole != UserRole.TECHNICIAN.name &&
                    requestedRole != UserRole.RECEPTIONIST.name
                ) {
                    return@mapNotNull null
                }

                PendingStaffApplication(
                    uid = document.id,
                    fullName = document.getString("fullName").orEmpty(),
                    email = document.getString("email").orEmpty(),
                    phone = document.getString("phone").orEmpty(),
                    requestedRole = requestedRole
                )
            }
        }

    // ------------------------------------------------
    // APPROVE A STAFF APPLICATION
    // ------------------------------------------------

    suspend fun approveStaffApplication(
        uid: String,
        technicianId: String? = null
    ) = withContext(Dispatchers.IO) {

        if (!isCurrentUserAdmin()) {

            throw SecurityException(
                "Only an approved administrator can approve staff."
            )
        }

        val reference = firestore
            .collection(USERS_COLLECTION)
            .document(uid)

        val application = reference.get().await()

        if (
            !application.exists() ||
            application.getString("status") != STATUS_PENDING
        ) {

            throw IllegalStateException(
                "This application is no longer pending."
            )
        }

        val requestedRole = application.getString("requestedRole")

        if (
            requestedRole != UserRole.TECHNICIAN.name &&
            requestedRole != UserRole.RECEPTIONIST.name
        ) {

            throw IllegalArgumentException(
                "Invalid staff role."
            )
        }

        if (
            requestedRole == UserRole.TECHNICIAN.name &&
            technicianId.isNullOrBlank()
        ) {

            throw IllegalArgumentException(
                "Please select an existing technician record before approval."
            )
        }

        val updates = mutableMapOf<String, Any>(
            "role" to requestedRole,
            "status" to STATUS_APPROVED,
            "approvedAt" to FieldValue.serverTimestamp()
        )

        if (requestedRole == UserRole.TECHNICIAN.name) {

            updates["technicianId"] = technicianId!!
        }

        reference.update(updates).await()
    }

    // ------------------------------------------------
    // REJECT A STAFF APPLICATION
    // ------------------------------------------------

    suspend fun rejectStaffApplication(uid: String) =
        withContext(Dispatchers.IO) {

            if (!isCurrentUserAdmin()) {

                throw SecurityException(
                    "Only an approved administrator can reject staff."
                )
            }

            val reference = firestore
                .collection(USERS_COLLECTION)
                .document(uid)

            val application = reference.get().await()

            if (
                !application.exists() ||
                application.getString("status") != STATUS_PENDING
            ) {

                throw IllegalStateException(
                    "This application is no longer pending."
                )
            }

            reference.update(
                mapOf(
                    "status" to STATUS_REJECTED,
                    "rejectedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

    // ------------------------------------------------
    // CREATE A FIRESTORE PROFILE
    // ------------------------------------------------

    private suspend fun createPendingProfile(
        uid: String,
        fullName: String,
        email: String,
        phone: String,
        requestedRole: UserRole,
        provider: String
    ) {

        val profile = hashMapOf<String, Any>(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "requestedRole" to requestedRole.name,
            "role" to ROLE_PENDING,
            "status" to STATUS_PENDING,
            "authProvider" to provider,
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore
            .collection(USERS_COLLECTION)
            .document(uid)
            .set(profile)
            .await()
    }

    // ------------------------------------------------
    // REGISTRATION VALIDATION
    // ------------------------------------------------

    private fun validateRegistration(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): String? {

        return when {

            fullName.isBlank() ->
                "Full name is required."

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Enter a valid email address."

            phone.length != 10 || !phone.startsWith("0") ->
                "Enter a valid South African phone number with exactly 10 digits, starting with 0."

            password.length < 8 ->
                "Password must be at least 8 characters."

            !password.any { it.isDigit() } ||
                    !password.any { it.isLetter() } ->
                "Password must contain letters and numbers."

            password != confirmPassword ->
                "Passwords do not match."

            else -> null
        }
    }

    // ------------------------------------------------
    // CONVERT FIRESTORE PROFILE TO ROOM USER
    // ------------------------------------------------

    private fun DocumentSnapshot.toLocalUser(
        uid: String
    ): UserEntity {

        return UserEntity(
            id = uid,
            fullName = getString("fullName").orEmpty(),
            email = getString("email").orEmpty(),
            phone = getString("phone").orEmpty(),
            role = getString("role") ?: ROLE_PENDING,
            passwordHash = ""
        )
    }

    // ------------------------------------------------
    // CACHE USER
    // ------------------------------------------------

    private suspend fun cacheUser(user: UserEntity) {

        userDao.insertAll(listOf(user))
    }

    // ------------------------------------------------
    // SAVE USER SESSION
    // ------------------------------------------------

    private suspend fun saveSession(userId: String) {

        context.sessionStore.edit {
            it[KEY_USER_ID] = userId
        }
    }

    // ------------------------------------------------
    // READABLE ERROR MESSAGES
    // ------------------------------------------------

    private fun readableError(exception: Exception): String {

        return when (exception) {

            is FirebaseAuthUserCollisionException ->
                "An account with this email already exists."

            is FirebaseAuthWeakPasswordException ->
                "Your password is too weak."

            else ->
                "Something went wrong. Check your internet connection and try again."
        }
    }
}
