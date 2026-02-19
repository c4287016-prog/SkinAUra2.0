package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.adapter.OrderAdapter
import com.example.skinaura20.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyOrder : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: OrderAdapter
    private var list = mutableListOf<OrderModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_order, container, false)

        rv = view.findViewById(R.id.ordersRecyclerView)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = OrderAdapter(list)
        rv.adapter = adapter

        fetchOrders()
        return view
    }

    private fun fetchOrders() {
        val uId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("Orders")
            .whereEqualTo("userId", uId)
            .get()
            .addOnSuccessListener { snapshot ->
                list.clear()
                for (doc in snapshot) {
                    val data = doc.toObject(OrderModel::class.java)
                    list.add(data)
                }
                adapter.notifyDataSetChanged()

                if (list.isEmpty()) {
                    Toast.makeText(context, "No orders yet!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}