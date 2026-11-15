package com.mzansiautorepairs.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mzansiautorepairs.UserRole
import com.mzansiautorepairs.data.AppDatabase
import com.mzansiautorepairs.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

private val Context.sessionStore by preferencesDataStore(name = "session")
private val KEY_USER_ID = stringPreferencesKey("user_id")

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(private val context: Context) {
    private val userDao = AppDatabase.get(context).userDao()

    suspend fun register(
        fullName: String, email: String, phone: String,
        password: String, confirmPassword: String, role: UserRole
    ): AuthResult {
        return withContext(Dispatchers.IO) {
            val cleanEmail = email.trim().lowercase()
            when {
                fullName.isBlank() -> AuthResult.Error("Full name is required")
                !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() ->
                    AuthResult.Error("Enter a valid email address")

                phone.filter { it.isDigit() }.length < 10 -> AuthResult.Error("Enter a valid phone number")
                password.length < 8 -> AuthResult.Error("Password must be at least 8 characters")
                !password.any { it.isDigit() } || !password.any { it.isLetter() } ->
                    AuthResult.Error("Password must contain letters and numbers")

                password != confirmPassword -> AuthResult.Error("Passwords do not match")
                userDao.findByEmail(cleanEmail) != null ->
                    AuthResult.Error("An account with this email already exists")

                else -> {
                    val user = UserEntity(
                        id = "U" + UUID.randomUUID().toString().take(8),
                        fullName = fullName.trim(),
                        email = cleanEmail,
                        phone = phone.trim(),
                        role = role.name,
                        passwordHash = PasswordHasher.hash(password)
                    )
                    userDao.insert(user)
                    saveSession(user.id)
                    AuthResult.Success(user)
                }
            }
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            if (email.isBlank() || password.isBlank()) return@withContext AuthResult.Error("Enter your email and password")
            val user = userDao.findByEmail(email.trim())
            // Same message for "no such user" and "wrong password" so accounts can't be probed.
            if (user == null || !PasswordHasher.verify(password, user.passwordHash)) {
                AuthResult.Error("Incorrect email or password")
            } else {
                saveSession(user.id)
                AuthResult.Success(user)
            }
        }
    }

    suspend fun logout() { context.sessionStore.edit { it.remove(KEY_USER_ID) } }

    suspend fun isLoggedIn(): Boolean = context.sessionStore.data.map { it[KEY_USER_ID] }.first() != null

    private suspend fun saveSession(userId: String) {
        context.sessionStore.edit { it[KEY_USER_ID] = userId }
    }
}