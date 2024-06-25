
package com.example.faams


/*
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.faams.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit
import android.util.Log
import com.google.firebase.FirebaseException



class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val phoneNumber = binding.phoneEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && phoneNumber.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val user = firebaseAuth.currentUser
                                savePhoneNumber(user?.uid, phoneNumber)
                                user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                    if (verifyTask.isSuccessful) {

                                        Toast.makeText(
                                            this,
                                            "Registration successful. Please check your email for verification.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            this,
                                            verifyTask.exception.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                try {
                                    throw task.exception!!
                                } catch (e: FirebaseAuthUserCollisionException) {
                                    Toast.makeText(
                                        this,
                                        "This email address is already in use. Please use another email or login.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePhoneNumber(userId: String?, phoneNumber: String) {
        userId?.let {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("Users")
            usersRef.child(it).child("phoneNumber").setValue(phoneNumber)
                .addOnSuccessListener {
                    Toast.makeText(this, "Phone number saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save phone number: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}*/
/*
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.faams.R
import com.example.faams.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val phoneNumber = binding.phoneEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && phoneNumber.isNotEmpty()) {
                if (pass == confirmPass) {
                    registerUser(email, pass, phoneNumber)
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, pass: String, phoneNumber: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Registration successful. Please check your email for verification.",
                                Toast.LENGTH_LONG
                            ).show()
                            // Wait for email verification
                            checkEmailVerification(user, phoneNumber)
                        } else {
                            Toast.makeText(
                                this,
                                verifyTask.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            this,
                            "This email address is already in use. Please use another email or login.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun checkEmailVerification(user: FirebaseUser, phoneNumber: String) {
        user.reload().addOnCompleteListener {
            if (user.isEmailVerified) {
                sendPhoneVerification(phoneNumber)
            } else {
                Toast.makeText(
                    this,
                    "Please verify your email first.",
                    Toast.LENGTH_SHORT
                ).show()
                // Retry email verification after some time
                user.reload().addOnSuccessListener {
                    checkEmailVerification(user, phoneNumber)
                }
            }
        }
    }

    private fun sendPhoneVerification(phoneNumber: String) {
        val formattedPhoneNumber = if (!phoneNumber.startsWith("+")) "+91$phoneNumber" else phoneNumber
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(formattedPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto verification
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@RegisterActivity, "Phone number verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    this@RegisterActivity.verificationId = verificationId
                    showOtpDialog()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun showOtpDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_enter_otp, null)
        val otpEt = dialogView.findViewById<EditText>(R.id.etOtp)
        val verifyOtpBtn = dialogView.findViewById<Button>(R.id.btnVerify)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter OTP")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        verifyOtpBtn.setOnClickListener {
            val otp = otpEt.text.toString().trim()
            if (otp.isNotEmpty()) {
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(credential)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    savePhoneNumber(user?.uid, user?.phoneNumber)
                } else {
                    Toast.makeText(this, "Verification failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun savePhoneNumber(userId: String?, phoneNumber: String?) {
        userId?.let {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("Users")
            usersRef.child(it).child("phoneNumber").setValue(phoneNumber)
                .addOnSuccessListener {
                    Toast.makeText(this, "Phone number saved successfully", Toast.LENGTH_SHORT).show()
                    // Show registration success message
                    showRegistrationSuccess()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save phone number: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showRegistrationSuccess() {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
        // Redirect to login page
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()  // Optional: Finish the current activity so the user cannot go back to it
    }
}*/
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.faams.R
import com.example.faams.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var verificationId: String
    private var isEmailVerified = false
    private var isPhoneVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val phoneNumber = binding.phoneEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && phoneNumber.isNotEmpty()) {
                if (pass == confirmPass) {
                    registerUser(email, pass, phoneNumber)
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, pass: String, phoneNumber: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Registration successful. Please check your email for verification.",
                                Toast.LENGTH_LONG
                            ).show()
                            // Wait for email verification
                            checkEmailVerification(user, phoneNumber)
                        } else {
                            Toast.makeText(
                                this,
                                verifyTask.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            this,
                            "This email address is already in use. Please use another email or login.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun checkEmailVerification(user: FirebaseUser, phoneNumber: String) {
        user.reload().addOnCompleteListener {
            if (user.isEmailVerified) {
                isEmailVerified = true
                sendPhoneVerification(phoneNumber)
            } else {
                Toast.makeText(
                    this,
                    "Please verify your email first.",
                    Toast.LENGTH_SHORT
                ).show()
                // Retry email verification after some time
                user.reload().addOnSuccessListener {
                    checkEmailVerification(user, phoneNumber)
                }
            }
        }
    }

    private fun sendPhoneVerification(phoneNumber: String) {
        val formattedPhoneNumber = if (!phoneNumber.startsWith("+")) "+91$phoneNumber" else phoneNumber
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(formattedPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto verification
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@RegisterActivity, "Phone number verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    this@RegisterActivity.verificationId = verificationId
                    showOtpDialog()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun showOtpDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_enter_otp, null)
        val otpEt = dialogView.findViewById<EditText>(R.id.etOtp)
        val verifyOtpBtn = dialogView.findViewById<Button>(R.id.btnVerify)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter OTP")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        verifyOtpBtn.setOnClickListener {
            val otp = otpEt.text.toString().trim()
            if (otp.isNotEmpty()) {
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(credential)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    isPhoneVerified = true
                    savePhoneNumber(user?.uid, user?.phoneNumber)
                } else {
                    Toast.makeText(this, "Verification failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun savePhoneNumber(userId: String?, phoneNumber: String?) {
        userId?.let {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("Users")
            usersRef.child(it).child("phoneNumber").setValue(phoneNumber)
                .addOnSuccessListener {
                    if (isEmailVerified && isPhoneVerified) {
                        showRegistrationSuccess()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save phone number: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showRegistrationSuccess() {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
        // Redirect to login page
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()  // Optional: Finish the current activity so the user cannot go back to it
    }
}








