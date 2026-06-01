package com.example.coffeeshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivityProfileBinding
import com.example.coffeeshop.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://cafeapp-c7d63-default-rtdb.firebaseio.com/")

        loadUserInfo()
        setupBottomNavigation()

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupBottomNavigation() {
        // Đặt tab Profile là tab đang chọn
        binding.bottomNavigation.selectedItemId = R.id.profile
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.profile -> true
                R.id.contact -> {
                    startActivity(Intent(this, ContactActivity::class.java))
                    finish()
                    true
                }
                else -> {
                    false
                }
            }
        }
        
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun loadUserInfo() {
        val uid = auth.currentUser?.uid ?: return
        binding.progressBar.visibility = View.VISIBLE

        database.getReference("Users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.progressBar.visibility = View.GONE
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        binding.nameTv.text = user.name
                        binding.emailTv.text = user.email
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@ProfileActivity, "Không thể tải thông tin", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
