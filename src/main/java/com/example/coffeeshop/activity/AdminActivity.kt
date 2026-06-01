package com.example.coffeeshop.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.adapter.AdminOrderAdapter
import com.example.coffeeshop.adapter.AdminProductAdapter
import com.example.coffeeshop.databinding.ActivityAdminBinding
import com.example.coffeeshop.model.ItemsModel
import com.example.coffeeshop.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminActivity : BaseActivity() {
    private companion object {
        const val TAG = "AdminActivity"
        const val DATABASE_URL = "https://cafeapp-c7d63-default-rtdb.firebaseio.com/"
    }

    private lateinit var binding: ActivityAdminBinding
    private val database = FirebaseDatabase.getInstance(DATABASE_URL)
    private val productRef = database.getReference("Items")
    private val orderRef = database.getReference("Orders")

    private val allProducts = mutableListOf<Pair<String, ItemsModel>>()
    private val allOrders = mutableListOf<OrderModel>()
    private lateinit var productAdapter: AdminProductAdapter
    private lateinit var orderAdapter: AdminOrderAdapter
    private var editingProductKey: String? = null
    private var productSearchQuery = ""
    private var orderSearchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }
        setupProducts()
        setupOrders()
        listenProducts()
        listenOrders()
    }

    private fun setupProducts() {
        productAdapter = AdminProductAdapter(mutableListOf(), ::fillProductForm, ::confirmDeleteProduct)
        binding.productsRv.layoutManager = LinearLayoutManager(this)
        binding.productsRv.adapter = productAdapter

        binding.productSearchEt.addTextChangedListener {
            productSearchQuery = it.toString().trim()
            productAdapter.updateItems(filteredProducts())
        }
        binding.saveProductBtn.setOnClickListener { saveProduct() }
        binding.clearProductBtn.setOnClickListener { clearProductForm() }
    }

    private fun setupOrders() {
        orderAdapter = AdminOrderAdapter(mutableListOf(), ::showStatusDialog, ::confirmDeleteOrder)
        binding.ordersRv.layoutManager = LinearLayoutManager(this)
        binding.ordersRv.adapter = orderAdapter

        binding.orderSearchEt.addTextChangedListener {
            orderSearchQuery = it.toString().trim()
            orderAdapter.updateItems(filteredOrders())
        }
    }

    private fun listenProducts() {
        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allProducts.clear()
                snapshot.children.forEach { child ->
                    child.getValue(ItemsModel::class.java)?.let { item ->
                        allProducts.add((child.key ?: "") to item)
                    }
                }
                productAdapter.updateItems(filteredProducts())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun listenOrders() {
        orderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allOrders.clear()
                snapshot.children.forEach { child ->
                    child.getValue(OrderModel::class.java)?.let { order ->
                        order.id = child.key ?: order.id
                        allOrders.add(order)
                    }
                }
                allOrders.sortByDescending { it.createdAt }
                orderAdapter.updateItems(filteredOrders())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveProduct() {
        val title = binding.productTitleEt.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Enter product name", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUrl = binding.productImageEt.text.toString().trim()
        val product = ItemsModel(
            title = title,
            description = binding.productDescriptionEt.text.toString().trim(),
            picUrl = if (imageUrl.isBlank()) arrayListOf() else arrayListOf(imageUrl),
            price = binding.productPriceEt.text.toString().toDoubleOrNull() ?: 0.0,
            rating = binding.productRatingEt.text.toString().toDoubleOrNull() ?: 0.0,
            extra = binding.productExtraEt.text.toString().trim(),
            categoryId = binding.productCategoryEt.text.toString().toIntOrNull() ?: 0
        )

        val key = editingProductKey
        val saveTask = if (key == null) {
            productRef.push().setValue(product)
        } else {
            productRef.child(key).setValue(product)
        }

        saveTask.addOnSuccessListener {
            Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show()
            clearProductForm()
        }.addOnFailureListener { error ->
            showFirebaseError("Cannot save product", error)
        }
    }

    private fun fillProductForm(key: String, item: ItemsModel) {
        editingProductKey = key
        binding.saveProductBtn.text = "Update product"
        binding.productTitleEt.setText(item.title)
        binding.productDescriptionEt.setText(item.description)
        binding.productImageEt.setText(item.picUrl.firstOrNull().orEmpty())
        binding.productPriceEt.setText(item.price.toString())
        binding.productRatingEt.setText(item.rating.toString())
        binding.productCategoryEt.setText(item.categoryId.toString())
        binding.productExtraEt.setText(item.extra)
    }

    private fun clearProductForm() {
        editingProductKey = null
        binding.saveProductBtn.text = "Add product"
        binding.productTitleEt.text?.clear()
        binding.productDescriptionEt.text?.clear()
        binding.productImageEt.text?.clear()
        binding.productPriceEt.text?.clear()
        binding.productRatingEt.text?.clear()
        binding.productCategoryEt.text?.clear()
        binding.productExtraEt.text?.clear()
    }

    private fun confirmDeleteProduct(key: String, item: ItemsModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete product")
            .setMessage("Delete ${item.title}?")
            .setPositiveButton("Delete") { _, _ ->
                productRef.child(key).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show()
                        if (editingProductKey == key) clearProductForm()
                    }
                    .addOnFailureListener { error ->
                        showFirebaseError("Cannot delete product", error)
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showStatusDialog(order: OrderModel) {
        val statuses = arrayOf("Pending", "Preparing", "Delivering", "Completed", "Cancelled")
        AlertDialog.Builder(this)
            .setTitle("Order status")
            .setItems(statuses) { _, which ->
                orderRef.child(order.id).child("status").setValue(statuses[which])
                    .addOnFailureListener { error ->
                        showFirebaseError("Cannot update order", error)
                    }
            }
            .show()
    }

    private fun confirmDeleteOrder(order: OrderModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete order")
            .setMessage("Delete order from ${order.customerName.ifBlank { "Guest" }}?")
            .setPositiveButton("Delete") { _, _ ->
                orderRef.child(order.id).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { error ->
                        showFirebaseError("Cannot delete order", error)
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun filteredProducts(): List<Pair<String, ItemsModel>> {
        if (productSearchQuery.isEmpty()) return allProducts
        return allProducts.filter { (_, product) ->
            product.title.contains(productSearchQuery, ignoreCase = true) ||
                product.description.contains(productSearchQuery, ignoreCase = true) ||
                product.extra.contains(productSearchQuery, ignoreCase = true)
        }
    }

    private fun filteredOrders(): List<OrderModel> {
        if (orderSearchQuery.isEmpty()) return allOrders
        return allOrders.filter { order ->
            order.customerName.contains(orderSearchQuery, ignoreCase = true) ||
                order.customerEmail.contains(orderSearchQuery, ignoreCase = true) ||
                order.status.contains(orderSearchQuery, ignoreCase = true) ||
                order.items.any { it.title.contains(orderSearchQuery, ignoreCase = true) }
        }
    }

    private fun showFirebaseError(action: String, error: Exception) {
        Log.e(TAG, action, error)
        Toast.makeText(this, "$action: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
