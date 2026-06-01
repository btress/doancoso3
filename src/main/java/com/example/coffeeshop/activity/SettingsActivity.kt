package com.example.coffeeshop.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.coffeeshop.databinding.ActivitySettingsBinding
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        binding.adminBtn.setOnClickListener {
            showPasswordDialog()
        }
    }

    private fun showPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Admin Access")
        builder.setMessage("Enter Admin Password")

        val input = EditText(this)
        input.hint = "Password"
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val password = input.text.toString()
            checkAdminPassword(password)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun checkAdminPassword(enteredPassword: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("admin_password")

        ref.get().addOnSuccessListener { snapshot ->
            val correctPassword = snapshot.getValue(String::class.java) ?: "55555"
            if (enteredPassword == correctPassword) {
                startActivity(Intent(this, AdminActivity::class.java))
            } else {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            // Fallback to hardcoded password if firebase fails or node doesn't exist
            if (enteredPassword == "55555") {
                startActivity(Intent(this, AdminActivity::class.java))
            } else {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
