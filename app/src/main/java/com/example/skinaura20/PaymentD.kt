package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.adapter.PaymentAdapter
import com.example.skinaura20.model.OrderModel
import com.google.firebase.firestore.FirebaseFirestore

class PaymentD : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PaymentAdapter
    private var orderList = ArrayList<OrderModel>()
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment_d, container, false)

        recyclerView = view.findViewById(R.id.paymentRecyclerView)
        progressBar = view.findViewById(R.id.paymentProgressBar)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PaymentAdapter(orderList)
        recyclerView.adapter = adapter

        fetchPayments()

        return view
    }

    private fun fetchPayments() {
        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()

        // ⚠️ Firestore collection check karein: "AllOrders" ya "Orders"
        db.collection("Orders")
            .get()
            .addOnSuccessListener { result ->
                orderList.clear()
                for (document in result) {
                    val order = document.toObject(OrderModel::class.java)
                    orderList.add(order)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE

                if (orderList.isEmpty()) {
                    Toast.makeText(requireContext(), "No payments found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}