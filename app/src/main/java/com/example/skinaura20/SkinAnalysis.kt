package com.example.skinaura20.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.skinaura20.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore

class SkinAnalysis : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_skin_analysis, container, false)

        pieChart = view.findViewById(R.id.skinPieChart)
        progressBar = view.findViewById(R.id.analysisProgressBar)

        setupPieChart()
        loadSkinData()

        return view
    }

    private fun setupPieChart() {
        pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            transparentCircleRadius = 61f
            centerText = "Skin Types"
            setCenterTextSize(20f)
        }
    }

    private fun loadSkinData() {
        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()

        db.collection("users").get().addOnSuccessListener { documents ->
            val skinCounts = mutableMapOf<String, Float>()

            for (doc in documents) {
                val type = doc.getString("skinType") ?: "Not Set"
                if (type.isNotEmpty()) {
                    skinCounts[type] = skinCounts.getOrDefault(type, 0f) + 1f
                }
            }

            val entries = ArrayList<PieEntry>()
            skinCounts.forEach { (type, count) ->
                entries.add(PieEntry(count, type))
            }

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 12f

            val data = PieData(dataSet)
            pieChart.data = data
            pieChart.invalidate() // Refresh chart

            progressBar.visibility = View.GONE
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}