package com.example.coffeeshop.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.databinding.ViewholderAdminProductBinding
import com.example.coffeeshop.model.ItemsModel

class AdminProductAdapter(
    private val products: MutableList<Pair<String, ItemsModel>>,
    private val onEdit: (String, ItemsModel) -> Unit,
    private val onDelete: (String, ItemsModel) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newProducts: List<Pair<String, ItemsModel>>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderAdminProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (key, product) = products[position]
        holder.binding.productTitleTxt.text = product.title.ifBlank { "Untitled product" }
        holder.binding.productPriceTxt.text = "$${product.price}"
        holder.binding.productDescriptionTxt.text = product.description
        holder.binding.productMetaTxt.text = "Category ${product.categoryId} • Rating ${product.rating} • ${product.extra}"
        holder.binding.editProductBtn.setOnClickListener { onEdit(key, product) }
        holder.binding.deleteProductBtn.setOnClickListener { onDelete(key, product) }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(val binding: ViewholderAdminProductBinding) :
        RecyclerView.ViewHolder(binding.root)
}
