package com.example.coffeeshop.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.coffeeshop.adapter.SizeAdapter
import com.example.coffeeshop.databinding.ActivityDetailedBinding
import com.example.coffeeshop.helper.ManagmentCart
import com.example.coffeeshop.model.ItemsModel

class DetailedActivity : BaseActivity() {

    private var item: ItemsModel? = null
    private val binding: ActivityDetailedBinding by lazy {
        ActivityDetailedBinding.inflate(layoutInflater)
    }
    private lateinit var managementcart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        managementcart = ManagmentCart(this)
        
        item = intent.getParcelableExtra<ItemsModel>("object")
        
        if (item == null) {
            Toast.makeText(this, "Dữ liệu sản phẩm bị lỗi!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bundle()
        initSizeList()
    }

    private fun initSizeList() {
        val sizeList = ArrayList<String>().apply {
            add("1"); add("2"); add("3"); add("4")
        }

        binding.rvSizeList.adapter = SizeAdapter(this, sizeList)
        binding.rvSizeList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        item?.let {
            val imageUrl = it.getFirstImageUrl()
            Log.d("CoffeeApp", "Chi tiết đang tải ảnh: '$imageUrl'")

            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(100)))
                .placeholder(android.R.drawable.progress_horizontal)
                .fallback(com.example.coffeeshop.R.drawable.coffee)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        Log.e("CoffeeApp", "LỖI TẢI ẢNH CHI TIẾT: ${e?.message}")
                        return false
                    }
                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean = false
                })
                .error(com.example.coffeeshop.R.drawable.coffee)
                .into(binding.shapeableImageView)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bundle() {
        val currentItem = item ?: return
        
        binding.apply {
            titleTxt.text = currentItem.title
            descriptionTxt.text = currentItem.description
            priceTxt.text = "$" + currentItem.price
            ratingBar.rating = currentItem.rating.toFloat()

            addToCart.setOnClickListener {
                currentItem.numberInCart = numberItemTxt.text.toString().toIntOrNull() ?: 1
                managementcart.insertItems(currentItem)
                Toast.makeText(this@DetailedActivity, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
            }

            ivBack.setOnClickListener { finish() }

            plusCart.setOnClickListener {
                val count = numberItemTxt.text.toString().toIntOrNull() ?: 1
                numberItemTxt.text = (count + 1).toString()
            }

            minusCart.setOnClickListener {
                val count = numberItemTxt.text.toString().toIntOrNull() ?: 1
                if (count > 1) {
                    numberItemTxt.text = (count - 1).toString()
                }
            }
        }
    }
}
