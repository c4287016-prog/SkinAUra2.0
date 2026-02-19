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
import com.example.skinaura20.adapter.*
import com.example.skinaura20.model.*
import com.example.skinaura20.R
import com.google.firebase.firestore.FirebaseFirestore

class Dry : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var progressBar: ProgressBar
    private var productList = ArrayList<ProductModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dry, container, false)

        // Bind Views
        recyclerView = view.findViewById(R.id.dryRecyclerView)
        progressBar = view.findViewById(R.id.progressBarDry)

        // Setup RecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        fetchDryProducts()

        return view
    }

    private fun fetchDryProducts() {
        val db = FirebaseFirestore.getInstance()

        // Filter: Sirf "Dry Skin" wale products
        db.collection("Products")
            .whereEqualTo("skinType", "Dry Skin")
            .get()
            .addOnSuccessListener { snapshot ->
                productList.clear()
                for (doc in snapshot) {
                    val data = doc.toObject(ProductModel::class.java)
                    data.id = doc.id
                    productList.add(data)
                }

                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE

                if (productList.isEmpty()) {
                    Toast.makeText(requireContext(), "No products for Dry Skin yet", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}