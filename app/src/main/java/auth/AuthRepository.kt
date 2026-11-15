package com.mzansiautorepairs.auth

import android.content.Context
import android.util.Patterns
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mzansiautorepairs.UserRole
import com.mzansiautorepairs.data.AppDatabase
import com.mzansiautorepairs.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

private val Context.sessionStore by preferencesDataStore(name = "session")
private val KEY_USER_ID = stringPreferencesKey("user_id")

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class PendingApproval(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(private val context: Context) {

    private val userDao = AppDatabase.get(context).userDao()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val STATUS_PENDING = "PENDING"
        private const val STATUS_APPROVED = "APPROVED"
        private const val ROLE_PENDING = "PENDING"
    }

    // EMAIL AND PASSWORD REGISTRATION
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
        val cleanPhone = phone.trim()

        val validationError = validateRegistration(
            cleanName,
            cleanEmail,
            cleanPhone,
            password,
            confirmPassword,
            role
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

                // Registration does not automatically grant access.
                firebaseAuth.signOut()

                AuthResult.Error(
                    "Registration submitted successfully. " +
                            "Your account is awaiting administrator approval. " +
                            "You can sign in once your account has been approved."
                )

            } catch (e: Exception) {
                // Avoid leaving an authentication account without a profile.
                try {
                    firebaseUser.delete().await()
                } catch (_: Exception) {
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
            AuthResult.Error(readableError(e))
        }
    }

    // GOOGLE REGISTRATION
    // GoogleAuthHelper must sign the user into Firebase before this is called.
    suspend fun registerWithGoogle(
        fullName: String,
        email: String,
        phone: String,
        role: UserRole
    ): AuthResult = withContext(Dispatchers.IO) {

        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()

        if (cleanName.isBlank()) {
            return@withContext AuthResult.Error("Full name is required.")
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return@withContext AuthResult.Error("Enter a valid email address.")
        }

        if (cleanPhone.filter { it.isDigit() }.length < 10) {
            return@withContext AuthResult.Error("Enter a valid phone number.")
        }

        if (role == UserRole.ADMIN) {
            return@withContext AuthResult.Error(
                "Administrator accounts must be created through authorised setup."
            )
        }

        val firebaseUser = firebaseAuth.currentUser
            ?: return@withContext AuthResult.Error(
                "Your Google session has expired. Please sign in again."
            )

        if (firebaseUser.email?.trim()?.lowercase() != cleanEmail) {
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
                val existingUser = existingProfile.toLocalUser(firebaseUser.uid)
                val status = existingProfile.getString("status")

                firebaseAuth.signOut()

                return@withContext if (status == STATUS_APPROVED) {
                    AuthResult.Error(
                        "This account is already registered. Please sign in."
                    )
                } else {
                    AuthResult.Error(
                        "Your account is awaiting administrator approval."
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

            AuthResult.PendingApproval(
                "Registration submitted successfully. Your account is awaiting administrator approval. You can sign in once your account has been approved."
            )

        } catch (e: Exception) {
            firebaseAuth.signOut()
            AuthResult.Error(
                "Google registration could not be completed. Please try again."
            )
        }
    }

    // LOOK UP AN EXISTING GOOGLE USER
    suspend fun findGoogleUser(email: String): UserEntity? =
        withContext(Dispatchers.IO) {

            val firebaseUser = firebaseAuth.currentUser ?: return@withContext null

            if (firebaseUser.email?.trim()?.lowercase() != email.trim().lowercase()) {
                return@withContext null
            }

            try {
                val snapshot = firestore
                    .collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                if (!snapshot.exists()) {
                    return@withContext null
                }

                if (snapshot.getString("status") != STATUS_APPROVED) {
                    return@withContext null
                }

                val user = snapshot.toLocalUser(firebaseUser.uid)
                cacheUser(user)
                saveSession(user.id)
                user

            } catch (_: Exception) {
                null
            }
        }

    // EMAIL AND PASSWORD LOGIN
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
            AuthResult.Error("Incorrect email or password.")
        } catch (e: Exception) {
            AuthResult.Error(readableError(e))
        }
    }

    // SEND PASSWORD RESET EMAIL
    // Returns null when successful, otherwise an error message.
    suspend fun sendPasswordResetEmail(email: String): String? =
        withContext(Dispatchers.IO) {

            val cleanEmail = email.trim().lowercase()

            if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                return@withContext "Enter a valid email address."
            }

            try {
                firebaseAuth.sendPasswordResetEmail(cleanEmail).await()
                null
            } catch (e: Exception) {
                readableError(e)
            }
        }

    // LOGOUT
    suspend fun logout() {
        firebaseAuth.signOut()
        context.sessionStore.edit {
            it.remove(KEY_USER_ID)
        }
    }

    // CURRENT USER
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

    suspend fun isLoggedIn(): Boolean {
        val savedId = context.sessionStore.data
            .map { it[KEY_USER_ID] }
            .first()

        return firebaseAuth.currentUser?.uid != null &&
                firebaseAuth.currentUser?.uid == savedId
    }

    // CREATE A FIRESTORE PROFILE WITH NO ACTIVE ROLE
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

        firestore.collection(USERS_COLLECTION)
            .document(uid)
            .set(profile)
            .await()
    }

    private fun validateRegistration(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        role: UserRole
    ): String? {

        return when {
            fullName.isBlank() ->
                "Full name is required."

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Enter a valid email address."

            phone.filter { it.isDigit() }.length < 10 ->
                "Enter a valid phone number."

            password.length < 8 ->
                "Password must be at least 8 characters."

            !password.any { it.isDigit() } ||
                    !password.any { it.isLetter() } ->
                "Password must contain letters and numbers."

            password != confirmPassword ->
                "Passwords do not match."

            role == UserRole.ADMIN ->
                "Administrator accounts must be created through authorised setup."

            else -> null
        }
    }

    // CONVERT FIRESTORE PROFILE TO THE EXISTING ROOM USER MODEL
    private fun com.google.firebase.firestore.DocumentSnapshot.toLocalUser(
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

    private suspend fun cacheUser(user: UserEntity) {
        userDao.insertAll(listOf(user))
    }

    private suspend fun saveSession(userId: String) {
        context.sessionStore.edit {
            it[KEY_USER_ID] = userId
        }
    }

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
