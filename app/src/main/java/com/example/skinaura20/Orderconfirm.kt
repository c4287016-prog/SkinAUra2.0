package com.example.skinaura20.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.skinaura20.Homeactivity
import com.example.skinaura20.R

class Orderconfirm : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_orderconfirm, container, false)

        // Views initialize karein (ivSuccessCheck hata diya gaya hai)
        val btnContinue = view.findViewById<Button>(R.id.btnContinue)
        val mainContent = view.findViewById<LinearLayout>(R.id.mainContentLayout)

        // Initially content ko hide rakhein animation ke liye
        mainContent.visibility = View.INVISIBLE

        // 1 second ka delay taaki Lottie animation thodi play ho jaye pehle
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                mainContent.visibility = View.VISIBLE
                mainContent.alpha = 0f
                mainContent.animate().alpha(1f).setDuration(800).start()
            }
        }, 1000)

        // Continue Shopping button logic
        btnContinue.setOnClickListener {
            val intent = Intent(requireContext(), Homeactivity::class.java)
            // Sab purani activities clear karke Home par bhej dega
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}