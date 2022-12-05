package com.example.zadanie.utils

import com.example.zadanie.utils.Config.PASS_SALT
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


class PasswordUtils {
    companion object{
        private val salt =  PASS_SALT.toByteArray()

        fun hash(password: String): ByteArray {
            val spec = PBEKeySpec(password.toCharArray(), salt, 1000, 256)
            try {
                val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                return skf.generateSecret(spec).encoded
            } catch (e: NoSuchAlgorithmException) {
                throw AssertionError("Error while hashing a password: " + e.message, e)
            } catch (e: InvalidKeySpecException) {
                throw AssertionError("Error while hashing a password: " + e.message, e)
            } finally {
                spec.clearPassword()
            }
        }
    }
}