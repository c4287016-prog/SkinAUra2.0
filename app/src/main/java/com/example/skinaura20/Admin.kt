package com.example.skinaura20

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.skinaura20.fragment.*

class Admin : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Views ko initialize karein
        val dashboardLayout = findViewById<View>(R.id.dashboardLayout)
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragment_container)

        // CardView IDs
        val btnUpload = findViewById<CardView>(R.id.addImage)
        val btnDelete = findViewById<CardView>(R.id.deleteImages)
        val btnUpdate = findViewById<CardView>(R.id.Update) // Aapki XML ID 'Update' hai

        // Baki cards (Inhe bhi click listeners de sakte hain zaroorat padne par)
        val btnUsers = findViewById<CardView>(R.id.checkUsers)
        val btnPayments = findViewById<CardView>(R.id.payments)
        val btnStock = findViewById<CardView>(R.id.stockAnalysis)
        val btnskin = findViewById<CardView>(R.id.skin)

        // ✅ 1. Click Listeners for Fragments
        btnUpload.setOnClickListener { loadFragmentFullScreen(Uploadproduct()) }
        btnDelete.setOnClickListener { loadFragmentFullScreen(Deletep()) }

        // Update Fragment Load karne ke liye line add ki hai
        btnUpdate.setOnClickListener { loadFragmentFullScreen(Updatep()) }
           btnUsers.setOnClickListener { loadFragmentFullScreen(UserDetail()) }
           btnStock.setOnClickListener { loadFragmentFullScreen(StockAnalysis()) }
        btnskin.setOnClickListener { loadFragmentFullScreen(SkinAnalysis()) }
        btnPayments.setOnClickListener { loadFragmentFullScreen(PaymentD()) }
        // ✅ 2. Back Button Logic (Modern Approach)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()

                    // Listener jo check karega ki fragment back ho gaya, tab dashboard dikhao
                    supportFragmentManager.addOnBackStackChangedListener {
                        if (supportFragmentManager.backStackEntryCount == 0) {
                            dashboardLayout.visibility = View.VISIBLE
                            fragmentContainer.visibility = View.GONE
                        }
                    }
                } else {
                    // Dashboard par back dabane par Login screen par le jayein
                    val intent = Intent(this@Admin, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        })
    }

    // ✅ 3. Fragment Load karne ka Common Function
    private fun loadFragmentFullScreen(fragment: Fragment) {
        findViewById<View>(R.id.dashboardLayout).visibility = View.GONE
        findViewById<View>(R.id.fragment_container).visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}