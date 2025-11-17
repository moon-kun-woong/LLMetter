package org.llmetter.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class EncryptionUtil(
    @Value("\${app.encryption.key}")
    private val encryptionKey: String
) {
    companion object {
        private const val ALGORITHM = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12 // 96 bits
        private const val GCM_TAG_LENGTH = 128 // 128 bits
    }

    private val secretKey: SecretKeySpec = SecretKeySpec(
        MessageDigest.getInstance("SHA-256").digest(
            encryptionKey.toByteArray(StandardCharsets.UTF_8)
        ),
        "AES"
    )

    data class EncryptedData(
        val data: ByteArray,
        val iv: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is EncryptedData) return false

            if (!data.contentEquals(other.data)) return false
            if (!iv.contentEquals(other.iv)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = data.contentHashCode()
            result = 31 * result + iv.contentHashCode()
            return result
        }
    }

    /**
     * Encrypts the given data using AES-256-GCM
     * @param data The data to encrypt
     * @return EncryptedData containing the encrypted data and IV
     */
    fun encrypt(data: ByteArray): EncryptedData {
        // Generate random IV
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)

        // Initialize cipher
        val cipher = Cipher.getInstance(ALGORITHM)
        val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

        // Encrypt data
        val encryptedData = cipher.doFinal(data)

        return EncryptedData(encryptedData, iv)
    }

    /**
     * Decrypts the given encrypted data using AES-256-GCM
     * @param encryptedData The encrypted data
     * @param iv The initialization vector used during encryption
     * @return The decrypted data
     */
    fun decrypt(encryptedData: ByteArray, iv: ByteArray): ByteArray {
        // Initialize cipher
        val cipher = Cipher.getInstance(ALGORITHM)
        val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)

        // Decrypt data
        return cipher.doFinal(encryptedData)
    }
}
