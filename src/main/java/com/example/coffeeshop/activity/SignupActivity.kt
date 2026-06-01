package com.example.coffeeshop.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.coffeeshop.databinding.ActivitySignupBinding
import com.example.coffeeshop.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://cafeapp-c7d63-default-rtdb.firebaseio.com/")

        binding.signupBtn.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        val user = UserModel(uid, name, email)
                        
                        database.getReference("Users").child(uid).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                binding.progressBar.visibility = View.GONE
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                } else {
                                    Log.e("AuthError", "Database error: ${dbTask.exception?.message}")
                                    Toast.makeText(this, "Failed to save user info", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        binding.progressBar.visibility = View.GONE
                        val exception = task.exception
                        Log.e("AuthError", "Signup failed", exception)
                        
                        val errorMessage = when (exception) {
                            is FirebaseAuthUserCollisionException -> "Email already exists"
                            is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                            else -> "Error: ${exception?.localizedMessage}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.goToLoginTv.setOnClickListener {
            finish()
        }
    }
}
