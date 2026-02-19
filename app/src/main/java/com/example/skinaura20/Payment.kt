package com.example.skinaura20.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.skinaura20.R
import com.airbnb.lottie.LottieAnimationView

class Payment : Fragment() {

    private var tickCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        val timerTv = view.findViewById<TextView>(R.id.timerTv)
        val statusTv = view.findViewById<TextView>(R.id.statusTv)
        val instructionTv = view.findViewById<TextView>(R.id.instructionTv)
        val loader = view.findViewById<ProgressBar>(R.id.paymentLoader)
        val successLottie = view.findViewById<LottieAnimationView>(R.id.successLottie)

        // Simulated Payment Timer (5 seconds simulation)
        val timer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tickCount++

                // Timer calculation
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerTv.text = String.format("%02d:%02d", minutes, seconds)

                // Updating Status Messages for Real feel
                when (tickCount) {
                    1 -> statusTv.text = "Waiting for payment..."
                    2 -> statusTv.text = "Verifying with your bank..."
                    3 -> statusTv.text = "Payment authorized..."
                    4 -> statusTv.text = "Finalizing order details..."
                }

                // Trigger Success after 5 seconds
                if (tickCount >= 5) {
                    this.cancel()
                    processSuccess(loader, timerTv, instructionTv, successLottie, statusTv, view)
                }
            }
            override fun onFinish() {}
        }

        timer.start()
        return view
    }

    private fun processSuccess(
        loader: ProgressBar,
        timerTv: TextView,
        instructionTv: TextView,
        successLottie: LottieAnimationView,
        statusTv: TextView,
        view: View
    ) {
        // UI cleanup
        loader.visibility = View.GONE
        timerTv.visibility = View.GONE
        instructionTv.visibility = View.GONE

        // Lottie Animation setup
        successLottie.visibility = View.VISIBLE
        successLottie.setAnimation(R.raw.done_p)
        successLottie.playAnimation()

        // Success Text
        statusTv.text = "Payment Received Successfully! ✅"
        statusTv.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))

        // Redirection with safety check
        view.postDelayed({
            if (isAdded) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, Orderconfirm())
                    .commit()
                Toast.makeText(requireContext(), "Order Placed Successfully! 🎉", Toast.LENGTH_SHORT).show()
            }
        }, 3500) // 3.5 seconds delay so user can see the Lottie animation properly
    }
}