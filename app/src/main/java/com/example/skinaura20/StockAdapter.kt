package com.example.skinaura20.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.model.ProductModel

class StockAdapter(private val stockList: ArrayList<ProductModel>) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    class StockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.stockProductName)
        val stockValue: TextView = view.findViewById(R.id.txtStockValue)
        val status: TextView = view.findViewById(R.id.txtStockStatus)
        val progressBar: ProgressBar = view.findViewById(R.id.stockProgressBar) // ✅ Progress Bar Added
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        // Dhyaan rahe ki layout ka naam 'item_stock' hi ho
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val product = stockList[position]
        val stock = product.productStock?.toIntOrNull() ?: 0
        val maxStock = 100 // Aap ise apne hisaab se change kar sakte hain (Target Stock)

        holder.name.text = product.name
        holder.stockValue.text = "$stock/$maxStock units"

        // Percentage Calculate karein
        val percentage = (stock * 100) / maxStock
        holder.progressBar.progress = percentage

        // Logic for Colors and Status
        when {
            stock == 0 -> {
                holder.status.text = "OUT OF STOCK"
                holder.status.setTextColor(Color.RED)
                holder.progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
            }
            stock <= 10 -> { // Critical Zone (Red)
                holder.status.text = "CRITICAL: REFILL NOW"
                holder.status.setTextColor(Color.RED)
                holder.progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
            }
            stock <= 40 -> { // Warning Zone (Yellow/Orange)
                holder.status.text = "LOW STOCK: RE-ORDER SOON"
                holder.status.setTextColor(Color.parseColor("#FF9800"))
                holder.progressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FF9800"))
            }
            else -> { // Safe Zone (Green)
                holder.status.text = "STOCK SAFE"
                holder.status.setTextColor(Color.parseColor("#388E3C"))
                holder.progressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#388E3C"))
            }
        }
    }

    override fun getItemCount() = stockList.size
}