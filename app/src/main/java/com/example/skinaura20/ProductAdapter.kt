package com.example.skinaura20.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skinaura20.fragment.ProductDetail
import com.example.skinaura20.model.*
import com.example.skinaura20.R

class ProductAdapter
    (private val list: ArrayList<ProductModel>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Layout IDs match with product_item_layout.xml
        val img = view.findViewById<ImageView>(R.id.productImage)
        val name = view.findViewById<TextView>(R.id.productName)
        val price = view.findViewById<TextView>(R.id.productPrice)
        val desc = view.findViewById<TextView>(R.id.productDescription)
        val comboBadge = view.findViewById<TextView>(R.id.comboBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Aapka naya layout file name yahan use ho raha hai
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.name.text = data.name
        holder.price.text = "₹${data.price}"
        holder.desc.text = data.description

        // ⭐ Combo Badge Logic: Agar isPremium true hai toh badge dikhao
        if (data.isPremium == true) {
            holder.comboBadge.visibility = View.VISIBLE
        } else {
            holder.comboBadge.visibility = View.GONE
        }

        Glide.with(holder.itemView.context)
            .load(data.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val fragment = ProductDetail()
            val bundle = Bundle()

            // Product details pass karna Detail Fragment ke liye
            bundle.putString("id", data.id)
            bundle.putString("name", data.name)
            bundle.putString("price", data.price)
            bundle.putString("image", data.imageUrl)
            bundle.putString("desc", data.description)
            bundle.putString("qty", data.productStock)
            // Premium status bhi bhej dete hain agar detail mein badge dikhana ho
            bundle.putBoolean("isPremium", data.isPremium ?: false)

            fragment.arguments = bundle

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = list.size
}