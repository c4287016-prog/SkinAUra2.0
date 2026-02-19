package com.example.skinaura20.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skinaura20.model.*
import com.example.skinaura20.R

class UpdateAdapter(
    private val list: ArrayList<ProductModel>,
    private val onEditClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder>() {

    class UpdateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.itemImage)
        val name = view.findViewById<TextView>(R.id.itemName)
        val price = view.findViewById<TextView>(R.id.itemPrice)
        val stock = view.findViewById<TextView>(R.id.itemStock) // Stock TextView mapping
        val btnEdit = view.findViewById<ImageView>(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.update_item, parent, false)
        return UpdateViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpdateViewHolder, position: Int) {
        val product = list[position]

        // Binding Data
        holder.name.text = product.name
        holder.price.text = "Price: ₹${product.price}"

        // 🔥 Stock quantity yahan show hogi
        holder.stock.text = "Stock: ${product.productStock ?: "0"}"

        // Glide image loading with safety
        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .into(holder.img)

        // Edit button logic
        holder.btnEdit.setOnClickListener {
            onEditClick(product)
        }
    }

    override fun getItemCount(): Int = list.size
}