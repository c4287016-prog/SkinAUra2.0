package com.example.skinaura20.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skinaura20.R
import com.example.skinaura20.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartAdapter(
    val context: Context,
    val list: ArrayList<ProductModel>,
    val onTotalChanged: () -> Unit // Total price update karne ke liye callback
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.cartProductImg)
        val name = view.findViewById<TextView>(R.id.cartProductName)
        val price = view.findViewById<TextView>(R.id.cartProductPrice)
        val qtyText = view.findViewById<TextView>(R.id.cartProductQty)
        val plusBtn = view.findViewById<ImageView>(R.id.plusBtn)
        val minusBtn = view.findViewById<ImageView>(R.id.minusBtn)
        val deleteBtn = view.findViewById<ImageView>(R.id.cartDeleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = list[position]

        holder.name.text = item.name
        holder.price.text = "₹${item.price}"
        holder.qtyText.text = item.quantity.toString()

        Glide.with(context).load(item.imageUrl).into(holder.img)

        // Delete Logic
        holder.deleteBtn.setOnClickListener {
            if (userId != null && item.id != null) {
                db.collection("Cart").document(userId).collection("Items")
                    .document(item.id!!).delete().addOnSuccessListener {
                        Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Plus Logic
        holder.plusBtn.setOnClickListener {
            updateQuantity(item.id!!, item.quantity + 1)
        }

        // Minus Logic
        holder.minusBtn.setOnClickListener {
            if (item.quantity > 1) {
                updateQuantity(item.id!!, item.quantity - 1)
            } else {
                Toast.makeText(context, "Use delete button to remove", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateQuantity(pId: String, newQty: Int) {
        if (userId != null) {
            db.collection("Cart").document(userId).collection("Items")
                .document(pId).update("quantity", newQty)
        }
    }

    override fun getItemCount(): Int = list.size
}