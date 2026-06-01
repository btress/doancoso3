package com.example.coffeeshop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.coffeeshop.activity.DetailedActivity
import com.example.coffeeshop.databinding.ViewholderPopularBinding
import com.example.coffeeshop.model.ItemsModel

class PopularAdapter(val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<PopularAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<ItemsModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularAdapter.ViewHolder {
        val binding = ViewholderPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PopularAdapter.ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = "$" + item.price.toString()
        holder.binding.ratingBar.rating = item.rating.toFloat()
        holder.binding.extraTxt.text = item.extra

        val imageUrl = item.getFirstImageUrl()
        
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .apply(RequestOptions().transform(CenterCrop()))
            .placeholder(android.R.drawable.progress_horizontal)
            .fallback(com.example.coffeeshop.R.drawable.coffee)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    // Hiện lỗi lên màn hình để bạn biết lý do (vd: SSL Error do sai ngày giờ)
                    holder.itemView.post {
                        if (position == 0) { 
                            Toast.makeText(holder.itemView.context, "Lỗi nạp ảnh: ${e?.message}", Toast.LENGTH_LONG).show()
                        }
                        Log.e("CoffeeApp", "Lỗi nạp ảnh cho ${item.title}: ${e?.message} | Link: $imageUrl")
                    }
                    return false
                }
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean = false
            })
            .error(com.example.coffeeshop.R.drawable.coffee)
            .into(holder.binding.shapeableImageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailedActivity::class.java)
            intent.putExtra("object", item)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ViewholderPopularBinding) :
        RecyclerView.ViewHolder(binding.root)
}
