package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.skinaura20.R
import com.example.skinaura20.fragment.*

class Category : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Aapka updated XML layout inflate ho raha hai
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        // 1. Moisturizer Card Click
        view.findViewById<LinearLayout>(R.id.btnMoisturizer).setOnClickListener {
            openSubCategory("Moisturizer")
        }

        // 2. Sunscreen Card Click
        view.findViewById<LinearLayout>(R.id.btnSunscreen).setOnClickListener {
            openSubCategory("Sunscreen")
        }

        // 3. Serum Card Click
        view.findViewById<LinearLayout>(R.id.btnSerum).setOnClickListener {
            openSubCategory("Serum")
        }

        // 4. Cleanser Card Click
        view.findViewById<LinearLayout>(R.id.btnCleanser).setOnClickListener {
            openSubCategory("Cleanser")
        }

        return view
    }



    private fun openSubCategory(type: String) {
        val fragment = ProductType()
        val bundle = Bundle()


        bundle.putString("pType", type)
        fragment.arguments = bundle


        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}