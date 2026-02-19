package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.model.*
import com.example.skinaura20.R
import com.example.skinaura20.adapter.DeleteAdapter
import com.google.firebase.firestore.FirebaseFirestore

class Deletep : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeleteAdapter
    private var productList = ArrayList<ProductModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_deletep, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewDelete)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = DeleteAdapter(productList)
        recyclerView.adapter = adapter

        fetchData()

        return view
    }

    private fun fetchData() {
        FirebaseFirestore.getInstance().collection("Products")
            .get()
            .addOnSuccessListener { snapshot ->
                productList.clear()
                for (doc in snapshot) {
                    val data = doc.toObject(ProductModel::class.java)
                    data.id = doc.id // Document ID set karna zaroori hai
                    productList.add(data)
                }
                adapter.notifyDataSetChanged()
            }
    }
}