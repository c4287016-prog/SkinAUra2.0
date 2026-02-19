package com.example.skinaura20.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skinaura20.model.*
import com.example.skinaura20.R
import com.google.firebase.firestore.FirebaseFirestore

class DeleteAdapter(private val list: ArrayList<ProductModel>) :
    RecyclerView.Adapter<DeleteAdapter.DeleteViewHolder>() {

    class DeleteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imageProduct)
        val name = view.findViewById<TextView>(R.id.textName)
        val quantityText = view.findViewById<TextView>(R.id.textQuantity)
        val editQty = view.findViewById<EditText>(R.id.editDeleteQty)
        val btnDelete = view.findViewById<Button>(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.delete_item, parent, false)
        return DeleteViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeleteViewHolder, position: Int) {
        val item = list[position]

        holder.name.text = item.name
        // Maan lijiye aapke model mein 'productQty' naam ka field hai
        holder.quantityText.text = "Available: ${item.productStock ?: 0}"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.img)

        holder.btnDelete.setOnClickListener {
            val inputQty = holder.editQty.text.toString()

            if (inputQty.isEmpty()) {
                Toast.makeText(holder.itemView.context, "Please enter quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val qtyToDelete = inputQty.toInt()
            val currentQty = item.productStock?.toInt() ?: 0
            val db = FirebaseFirestore.getInstance()

            if (qtyToDelete >= currentQty) {
                // Pura product delete kar do
                db.collection("Products").document(item.id!!).delete().addOnSuccessListener {
                    list.removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(holder.itemView.context, "Product Removed", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Sirf quantity kam karo
                val newQty = (currentQty - qtyToDelete).toString()
                db.collection("Products").document(item.id!!).update("productQty", newQty).addOnSuccessListener {
                    item.productStock = newQty
                    notifyItemChanged(position)
                    holder.editQty.text.clear()
                    Toast.makeText(holder.itemView.context, "Quantity Updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size
}