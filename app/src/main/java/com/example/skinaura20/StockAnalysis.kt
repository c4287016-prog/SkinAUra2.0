package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.adapter.StockAdapter
import com.example.skinaura20.model.ProductModel
import com.google.firebase.firestore.FirebaseFirestore

class StockAnalysis : Fragment() {

    private lateinit var stockRecyclerView: RecyclerView
    private lateinit var adapter: StockAdapter
    private var allProductsList = ArrayList<ProductModel>() // Saare products dikhayenge
    private lateinit var progressBar: ProgressBar
    private lateinit var txtTotalProducts: TextView
    private lateinit var txtLowStockCount: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stock_analysis, container, false)

        stockRecyclerView = view.findViewById(R.id.stockRecyclerView)
        progressBar = view.findViewById(R.id.stockProgressBar)
        txtTotalProducts = view.findViewById(R.id.txtTotalProducts)
        txtLowStockCount = view.findViewById(R.id.txtLowStockCount)

        stockRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = StockAdapter(allProductsList)
        stockRecyclerView.adapter = adapter

        analyzeStock()

        return view
    }

    private fun analyzeStock() {
        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()

        // Collection name "Products" check kar lena Firestore mein same hai ya nahi
        db.collection("Products")
            .get()
            .addOnSuccessListener { result ->
                allProductsList.clear()
                var totalItems = 0
                var lowStockCounter = 0

                for (document in result) {
                    val product = document.toObject(ProductModel::class.java)
                    totalItems++

                    val stock = product.productStock?.toIntOrNull() ?: 0
                    if (stock <= 10) { // 10 ya usse kam ko low stock maante hain
                        lowStockCounter++
                    }

                    allProductsList.add(product)
                }

                // Sorting: Low stock wale products sabse upar dikhenge
                allProductsList.sortBy { it.productStock?.toIntOrNull() ?: 0 }

                txtTotalProducts.text = totalItems.toString()
                txtLowStockCount.text = lowStockCounter.toString()

                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}