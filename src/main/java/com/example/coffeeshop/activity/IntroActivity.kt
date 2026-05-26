package com.example.coffeeshop.activity

import android.content.Intent
import android.os.Bundle
import com.example.coffeeshop.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Khởi tạo Binding một cách an toàn nhất
        try {
            binding = ActivityIntroBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.startBtn.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Nếu có lỗi nạp giao diện, vẫn cho hiện màn hình mặc định
            // setContentView(R.layout.activity_intro) 
        }
    }
}
