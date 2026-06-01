package com.example.coffeeshop.activity

import android.content.Intent
import android.os.Bundle
import com.example.coffeeshop.databinding.ActivityIntroBinding
import com.google.firebase.auth.FirebaseAuth

class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityIntroBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.startBtn.setOnClickListener {
                val auth = FirebaseAuth.getInstance()
                if (auth.currentUser != null) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
