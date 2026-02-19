package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.adapter.ProductAdapter
import com.example.skinaura20.model.ProductModel
import com.google.firebase.firestore.FirebaseFirestore

class Premiuma : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    // Adapter ArrayList leta hai, isliye yahan ArrayList define kiya
    private var productList = ArrayList<ProductModel>()
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_premiuma, container, false)

        // Bind Views
        recyclerView = view.findViewById(R.id.premiumRecyclerView)
        progressBar = view.findViewById(R.id.premiumProgressBar)
        emptyText = view.findViewById(R.id.emptyTextView)

        // Setup RecyclerView (2 columns combos ke liye perfect hain)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        fetchPremiumCombos()

        return view
    }

    private fun fetchPremiumCombos() {
        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()

        // ⭐ Filter: Sirf isPremium = true wale items hi aayenge
        db.collection("Products")
            .whereEqualTo("isPremium", true)
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (doc in documents) {
                    val product = doc.toObject(ProductModel::class.java)
                    productList.add(product)
                }

                // UI update logic
                if (productList.isEmpty()) {
                    emptyText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}