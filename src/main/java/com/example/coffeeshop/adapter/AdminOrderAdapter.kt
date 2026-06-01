package com.example.coffeeshop.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.databinding.ViewholderAdminOrderBinding
import com.example.coffeeshop.model.OrderModel

class AdminOrderAdapter(
    private val orders: MutableList<OrderModel>,
    private val onStatus: (OrderModel) -> Unit,
    private val onDelete: (OrderModel) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newOrders: List<OrderModel>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderAdminOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        val itemText = order.items.joinToString { "${it.title} x${it.numberInCart}" }
        holder.binding.orderCustomerTxt.text = order.customerName.ifBlank { "Guest" }
        holder.binding.orderTotalTxt.text = "$${order.total}"
        holder.binding.orderItemsTxt.text = itemText.ifBlank { "No items" }
        holder.binding.orderStatusTxt.text = "${order.status} • ${order.customerEmail.ifBlank { "No email" }}"
        holder.binding.statusOrderBtn.setOnClickListener { onStatus(order) }
        holder.binding.deleteOrderBtn.setOnClickListener { onDelete(order) }
    }

    override fun getItemCount(): Int = orders.size

    inner class ViewHolder(val binding: ViewholderAdminOrderBinding) :
        RecyclerView.ViewHolder(binding.root)
}
