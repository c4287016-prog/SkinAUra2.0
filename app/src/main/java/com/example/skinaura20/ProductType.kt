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

class ProductType : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var progressBar: ProgressBar
    private var list = ArrayList<ProductModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hum oily wala XML hi use kar rahe hain kyunki usme pehle se RecyclerView set hai
        val view = inflater.inflate(R.layout.fragment_product_type, container, false)

        recyclerView = view.findViewById(R.id.productTypeRecyclerView)
        progressBar = view.findViewById(R.id.productTypeProgressBar)

        // RecyclerView setup (2 items ek row mein)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(list)
        recyclerView.adapter = adapter

        // Step 1: CategoryFragment se bheja gaya 'pType' (Cleanser, Serum etc.) nikalna
        val categoryName = arguments?.getString("pType")

        if (categoryName != null) {
            fetchProducts(categoryName)
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "No category selected", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchProducts(type: String) {
        val db = FirebaseFirestore.getInstance()

        // Step 2: Firestore se sirf wahi products mangwana jo select huye hain
        db.collection("Products")
            .whereEqualTo("productType", type) // Yeh field aapke Firestore database mein honi chahiye
            .get()
            .addOnSuccessListener { snapshot ->
                list.clear()
                for (doc in snapshot) {
                    val data = doc.toObject(ProductModel::class.java)
                    data.id = doc.id
                    list.add(data)
                }

                // UI update karna
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE

                if (list.isEmpty()) {
                    Toast.makeText(requireContext(), "No products found for $type", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}