package com.example.faams

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import android.widget.Toast

class BiometricAuthUtil (private val context: Context, private val activity: FragmentActivity) {

    private val biometricManager: BiometricManager = from(context)
    private val executor = ContextCompat.getMainExecutor(context)
    private lateinit var onAuthenticationSuccess: () -> Unit

    private val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            // Handle error
            Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            // Handle success
            onAuthenticationSuccess()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            // Handle failure
            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun authenticate(onSuccess: () -> Unit) {
        this.onAuthenticationSuccess = onSuccess

        when (biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL)) {
            BIOMETRIC_SUCCESS -> {
                // Can authenticate
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for PDF Viewer")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Cancel")
                    .build()

                val biometricPrompt = BiometricPrompt(activity, executor, callback)
                biometricPrompt.authenticate(promptInfo)
            }
            BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(context, "No biometric features available on this device.", Toast.LENGTH_SHORT).show()
            }
            BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(context, "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show()
            }
            BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(context, "The user hasn't associated any biometric credentials with their account.", Toast.LENGTH_SHORT).show()
            }

        }
    }
}