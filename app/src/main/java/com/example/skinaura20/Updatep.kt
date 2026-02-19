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
import com.example.skinaura20.adapter.UpdateAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.example.skinaura20.fragment.*

class Updatep : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var productList = ArrayList<ProductModel>()
    private lateinit var adapter: UpdateAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_updatep, container, false)

        recyclerView = view.findViewById(R.id.recyclerUpdate)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = UpdateAdapter(productList) { product ->
            // Dusre fragment par data bhejna
            val fragment = Update_product()
            val bundle = Bundle()
            bundle.putString("pId", product.id)
            bundle.putString("pName", product.name)
            bundle.putString("pPrice", product.price)
            bundle.putString("pDesc", product.description)
            bundle.putString("pImg", product.imageUrl)
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Admin Activity wala container ID use karein
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter
        fetchProducts()
        return view
    }

    private fun fetchProducts() {
        FirebaseFirestore.getInstance().collection("Products").get()
            .addOnSuccessListener { snapshot ->
                productList.clear()
                for (doc in snapshot) {
                    val data = doc.toObject(ProductModel::class.java)
                    data.id = doc.id
                    productList.add(data)
                }
                adapter.notifyDataSetChanged()
            }
    }
}