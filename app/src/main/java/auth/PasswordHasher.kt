package com.mzansiautorepairs.auth

import android.os.Build
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Salted PBKDF2 password hashing. Stored format:
 *   pbkdf2$<algorithm>$<iterations>$<saltBase64>$<hashBase64>
 * The algorithm is stored in the string so old hashes still verify.
 * (PBKDF2WithHmacSHA256 needs API 26+, the app's minSdk is 24, so we fall back to SHA1.)
 */
object PasswordHasher {
    private const val ITERATIONS = 65_536
    private const val KEY_BITS = 256
    private const val SALT_BYTES = 16

    private fun defaultAlgorithm() =
        if (Build.VERSION.SDK_INT >= 26) "PBKDF2WithHmacSHA256" else "PBKDF2WithHmacSHA1"

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val alg = defaultAlgorithm()
        val hash = derive(password, salt, ITERATIONS, alg)
        return "pbkdf2\$$alg\$$ITERATIONS\$${b64(salt)}\$${b64(hash)}"
    }

    fun verify(password: String, stored: String): Boolean {
        val p = stored.split("$")
        if (p.size != 5 || p[0] != "pbkdf2") return false
        return try {
            val expected = Base64.decode(p[4], Base64.NO_WRAP)
            val actual = derive(password, Base64.decode(p[3], Base64.NO_WRAP), p[2].toInt(), p[1])
            MessageDigest.isEqual(expected, actual) // constant-time compare
        } catch (e: Exception) {
            false
        }
    }

    private fun derive(password: String, salt: ByteArray, iterations: Int, alg: String): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS)
        return SecretKeyFactory.getInstance(alg).generateSecret(spec).encoded
    }

    private fun b64(b: ByteArray) = Base64.encodeToString(b, Base64.NO_WRAP)
}