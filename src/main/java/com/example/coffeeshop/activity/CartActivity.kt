package com.example.coffeeshop.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.R
import com.example.coffeeshop.adapter.CartAdapter
import com.example.coffeeshop.databinding.ActivityCartBinding
import com.example.coffeeshop.helper.ChangeNumberItemsListener
import com.example.coffeeshop.helper.ManagmentCart
import com.example.coffeeshop.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CartActivity : BaseActivity() {

    lateinit var management: ManagmentCart
    private var tax: Double = 0.0
    private var deliveryFee: Double = 15.0
    private var totalFee: Double = 0.0
    private var itemTotal: Double = 0.0
    private val binding: ActivityCartBinding by lazy {
        ActivityCartBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        management = ManagmentCart(this)

        calculateCart()
        setVariable()
        initCartList()

    }

    private fun initCartList() {
        with(binding) {
            rvCartView.layoutManager =
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
            rvCartView.adapter = CartAdapter(
                management.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculateCart()
                    }

                })
        }
    }

    private fun setVariable() {
        binding.ivBack.setOnClickListener { finish() }
        binding.proceedCheckoutBtn.setOnClickListener { submitOrder() }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateCart() {
        val percentTax = 0.02
        tax = Math.round((management.getTotalFee() * percentTax) * 100) / 100.0
        totalFee = Math.round((management.getTotalFee() + tax + deliveryFee) * 100) / 100.0
        itemTotal = Math.round(management.getTotalFee() * 100) / 100.0

        with(binding) {
            subTotalPriceTxt.text = "$$itemTotal"
            totalTaxPriceTxt.text = "$$tax"
            deliveryPriceTxt.text = "$$deliveryFee"
            totalPriceTxt.text = "$$totalFee"
        }

    }

    private fun submitOrder() {
        val cartItems = management.getListCart()
        if (cartItems.isEmpty()) {
            android.widget.Toast.makeText(this, "Your cart is empty", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        val orderRef = FirebaseDatabase
            .getInstance("https://cafeapp-c7d63-default-rtdb.firebaseio.com/")
            .getReference("Orders")
            .push()

        val order = OrderModel(
            id = orderRef.key.orEmpty(),
            customerName = user?.displayName.orEmpty().ifBlank { user?.email.orEmpty().ifBlank { "Guest" } },
            customerEmail = user?.email.orEmpty(),
            items = cartItems,
            subtotal = itemTotal,
            tax = tax,
            delivery = deliveryFee,
            total = totalFee,
            status = "Pending",
            createdAt = System.currentTimeMillis()
        )

        orderRef.setValue(order).addOnSuccessListener {
            management.clearCart()
            calculateCart()
            initCartList()
            android.widget.Toast.makeText(this, "Order placed", android.widget.Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            android.widget.Toast.makeText(this, "Cannot place order", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
