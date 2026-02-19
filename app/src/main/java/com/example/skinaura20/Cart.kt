package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.adapter.CartAdapter
import com.example.skinaura20.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Cart : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTv: TextView
    private lateinit var emptyCartTv: TextView
    private lateinit var checkoutBtn: Button
    private lateinit var progressBar: ProgressBar

    private var cartList = ArrayList<ProductModel>()
    private lateinit var adapter: CartAdapter
    private var finalTotalAmount: Int = 0

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        initViews(view)
        setupRecyclerView()
        getCartData()

        checkoutBtn.setOnClickListener {
            if (cartList.isNotEmpty()) {
                openCheckoutFragment()
            } else {
                Toast.makeText(requireContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.cartRecyclerView)
        totalPriceTv = view.findViewById(R.id.totalPriceTv)
        emptyCartTv = view.findViewById(R.id.emptyCartTv)
        checkoutBtn = view.findViewById(R.id.checkoutBtn)
        progressBar = view.findViewById(R.id.cartProgressBar)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CartAdapter(requireContext(), cartList) {
            calculateTotal()
        }
        recyclerView.adapter = adapter
    }

    private fun getCartData() {
        val userId = auth.currentUser?.uid ?: return
        progressBar.visibility = View.VISIBLE

        db.collection("Cart").document(userId).collection("Items")
            .addSnapshotListener { snapshot, error ->
                if (!isAdded) return@addSnapshotListener

                progressBar.visibility = View.GONE
                if (error != null) return@addSnapshotListener

                cartList.clear()
                if (snapshot != null && !snapshot.isEmpty) {
                    emptyCartTv.visibility = View.GONE
                    for (doc in snapshot) {
                        val product = doc.toObject(ProductModel::class.java)
                        product.id = doc.id
                        product.quantity = doc.getLong("quantity")?.toInt() ?: 1
                        cartList.add(product)
                    }
                    adapter.notifyDataSetChanged()
                    calculateTotal()
                } else {
                    emptyCartTv.visibility = View.VISIBLE
                    totalPriceTv.text = "Total: ₹0"
                    finalTotalAmount = 0
                    cartList.clear()
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun calculateTotal() {
        var total = 0
        for (item in cartList) {
            val price = item.price?.replace("₹", "")?.trim()?.toIntOrNull() ?: 0
            total += price * item.quantity
        }
        finalTotalAmount = total
        totalPriceTv.text = "Total: ₹$finalTotalAmount"
    }

    private fun openCheckoutFragment() {
        val fragment = Address()
        val bundle = Bundle()

        // 🔥 FIXED KEY: "totalPrice" use kiya taaki Address.kt se match ho
        bundle.putString("totalPrice", finalTotalAmount.toString())

        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}