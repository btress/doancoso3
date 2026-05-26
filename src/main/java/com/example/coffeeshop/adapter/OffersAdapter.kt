package com.example.coffeeshop.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.coffeeshop.databinding.ViewholderOfferBinding
import com.example.coffeeshop.model.ItemsModel

class OffersAdapter(val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<OffersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersAdapter.ViewHolder {
        val binding = ViewholderOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OffersAdapter.ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = "$" + item.price.toString()

        val imageUrl = item.getFirstImageUrl()
        
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(30)))
            .placeholder(android.R.drawable.progress_horizontal)
            .fallback(com.example.coffeeshop.R.drawable.coffee)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    Log.e("CoffeeApp", "LỖI TẢI ẢNH OFFERS cho ${item.title}: ${e?.message} | Link: '$imageUrl'")
                    return false
                }
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .error(com.example.coffeeshop.R.drawable.coffee)
            .into(holder.binding.shapeableImageView)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ViewholderOfferBinding) :
        RecyclerView.ViewHolder(binding.root)
}
