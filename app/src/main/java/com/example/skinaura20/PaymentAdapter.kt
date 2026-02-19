package com.example.skinaura20.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.model.OrderModel

class PaymentAdapter(private val orderList: ArrayList<OrderModel>) :
    RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    class PaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.txtOrderId)
        val method: TextView = view.findViewById(R.id.txtPaymentMethod)
        val status: TextView = view.findViewById(R.id.txtPaymentStatus)
        val amount: TextView = view.findViewById(R.id.txtAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val order = orderList[position]

        holder.orderId.text = "Order ID: #${order.orderId}"
        holder.method.text = "Method: ${order.paymentMethod}"
        holder.amount.text = "₹${order.totalPrice}"

        // 🔥 Logic: UPI = Complete, COD = Pending
        if (order.paymentMethod.equals("UPI", ignoreCase = true)) {
            holder.status.text = "Status: Complete"
            holder.status.setTextColor(Color.parseColor("#388E3C")) // Green
        } else {
            holder.status.text = "Status: Pending"
            holder.status.setTextColor(Color.parseColor("#D32F2F")) // Red
        }
    }

    override fun getItemCount() = orderList.size
}