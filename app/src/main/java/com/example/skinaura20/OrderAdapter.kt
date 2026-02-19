package com.example.skinaura20.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.model.OrderModel

class OrderAdapter(private val orderList: List<OrderModel>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderIdTv)
        val orderDate: TextView = view.findViewById(R.id.orderDateTv)
        val orderPrice: TextView = view.findViewById(R.id.orderPriceTv)
        val orderPayment: TextView = view.findViewById(R.id.orderPaymentTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.orderId.text = "Order ID: #${order.orderId}"
        holder.orderDate.text = "Date: ${order.date}"
        holder.orderPrice.text = "₹${order.totalPrice}"
        holder.orderPayment.text = "Payment: ${order.paymentMethod}"
    }

    override fun getItemCount() = orderList.size
}