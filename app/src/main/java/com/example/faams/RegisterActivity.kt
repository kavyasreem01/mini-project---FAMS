
package com.example.faams

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
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val user = firebaseAuth.currentUser
                            savePhoneNumber(user?.uid, phoneNumber)
                            user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {

                                    Toast.makeText(this, "Registration successful. Please check your email for verification.", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, verifyTask.exception.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthUserCollisionException) {
                                Toast.makeText(this, "This email address is already in use. Please use another email or login.", Toast.LENGTH_LONG).show()
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
}

