package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.skinaura20.R
import com.google.firebase.auth.FirebaseAuth
import com.example.skinaura20.model.*
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetail : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val img = view.findViewById<ImageView>(R.id.detailImage)
        val nameTv = view.findViewById<TextView>(R.id.detailName)
        val priceTv = view.findViewById<TextView>(R.id.detailPrice)
        val descTv = view.findViewById<TextView>(R.id.detailDesc)
        val qtyTv = view.findViewById<TextView>(R.id.detailQty)
        val btnAdd = view.findViewById<Button>(R.id.btnAddToCart)

        // Data receive karna
        val pId = arguments?.getString("id")
        val pName = arguments?.getString("name")
        val pPrice = arguments?.getString("price")
        val pImg = arguments?.getString("image")
        val pDesc = arguments?.getString("desc")
        val pStockString = arguments?.getString("qty") ?: "0" // Firestore se stock qty
        val pStock = pStockString.toInt()

        // UI set karna
        nameTv.text = pName
        priceTv.text = "Price: ₹$pPrice"
        descTv.text = pDesc
        qtyTv.text = if (pStock > 0) "In Stock: $pStock" else "Out of Stock"

        // Agar stock 0 hai toh button disable kar do
        if (pStock <= 0) {
            btnAdd.isEnabled = false
            btnAdd.text = "Out of Stock"
        }

        Glide.with(requireContext())
            .load(pImg)
            .placeholder(R.drawable.placeholder_image)
            .into(img)

        btnAdd.setOnClickListener {
            if (pId != null) {
                checkAndAddToCart(pId, pName, pPrice, pImg, pStock)
            }
        }

        return view
    }

    private fun checkAndAddToCart(id: String, name: String?, price: String?, image: String?, maxStock: Int) {
        val userId = auth.currentUser?.uid ?: return

        // Hum Product ID ko hi Document ID bana rahe hain taaki duplicate na ho
        val cartRef = db.collection("Cart").document(userId).collection("Items").document(id)

        cartRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentQtyInCart = document.getLong("quantity")?.toInt() ?: 0

                // Stock limit check
                if (currentQtyInCart < maxStock) {
                    cartRef.update("quantity", currentQtyInCart + 1)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Updated quantity in cart! ➕", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Cannot add more! Only $maxStock items available.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Naya product add ho raha hai
                val cartItem = hashMapOf(
                    "productId" to id,
                    "name" to name,
                    "price" to price,
                    "image" to image,
                    "quantity" to 1
                )
                cartRef.set(cartItem).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Product added to Cart! ✅", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}