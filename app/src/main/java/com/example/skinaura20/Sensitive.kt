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

class Sensitive : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var progressBar: ProgressBar
    private var productList = ArrayList<ProductModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sensitive, container, false)

        recyclerView = view.findViewById(R.id.sensitiveRecyclerView)
        progressBar = view.findViewById(R.id.progressBarSensitive)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        fetchSensitiveProducts()
        return view
    }

    private fun fetchSensitiveProducts() {
        FirebaseFirestore.getInstance().collection("Products")
            .whereEqualTo("skinType", "Sensitive Skin") // Match with Admin Spinner
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
            }
    }
}