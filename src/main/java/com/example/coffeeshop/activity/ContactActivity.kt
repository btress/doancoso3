package com.example.coffeeshop.activity

import android.content.Intent
import android.os.Bundle
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivityContactBinding

class ContactActivity : BaseActivity() {
    private val binding: ActivityContactBinding by lazy {
        ActivityContactBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBottomMenu()
    }

    private fun initBottomMenu() {
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.bottomNavigation.selectedItemId = R.id.contact

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.contact -> {
                    // Already in contact
                    true
                }
                else -> false
            }
        }
    }
}
