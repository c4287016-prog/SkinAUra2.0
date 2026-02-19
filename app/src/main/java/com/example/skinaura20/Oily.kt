package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.model.*
import com.example.skinaura20.R
import com.example.skinaura20.adapter.ProductAdapter
import com.google.firebase.firestore.FirebaseFirestore

class Oily : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var progressBar: ProgressBar
    private var productList = ArrayList<ProductModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_oily, container, false)

        // Views Bind karein
        recyclerView = view.findViewById(R.id.oilyRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        // RecyclerView Setup (Grid Layout with 2 Columns)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        fetchOilyProducts()

        return view
    }

    private fun fetchOilyProducts() {
        val db = FirebaseFirestore.getInstance()

        // Firestore Query: Sirf Oily Skin wale products lao
        db.collection("Products")
            .whereEqualTo("skinType", "Oily Skin")
            .get()
            .addOnSuccessListener { snapshot ->
                productList.clear()
                for (doc in snapshot) {
                    val data = doc.toObject(ProductModel::class.java)
                    // ID set karna mat bhulna (Detail page ke liye zaroori hai)
                    data.id = doc.id
                    productList.add(data)
                }

                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE

                if (productList.isEmpty()) {
                    Toast.makeText(requireContext(), "No products found in Oily category", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}