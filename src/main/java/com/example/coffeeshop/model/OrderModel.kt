package com.example.coffeeshop.model

data class OrderModel(
    var id: String = "",
    var customerName: String = "",
    var customerEmail: String = "",
    var items: ArrayList<ItemsModel> = ArrayList(),
    var subtotal: Double = 0.0,
    var tax: Double = 0.0,
    var delivery: Double = 0.0,
    var total: Double = 0.0,
    var status: String = "Pending",
    var createdAt: Long = 0L
)
