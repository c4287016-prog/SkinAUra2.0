package com.example.skinaura20

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 🔥 LOGO ANIMATION
        val imgLogo = findViewById<ImageView>(R.id.imgLogo)
        val zoomAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        imgLogo.startAnimation(zoomAnim)

        Handler(Looper.getMainLooper()).postDelayed({

            val user = auth.currentUser
            if (user == null) {
                startActivity(Intent(this, Login::class.java))
                finish()
                return@postDelayed
            }

            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    when (doc.getString("role")) {
                        "admin" -> startActivity(Intent(this, Admin::class.java))
                        "user" -> startActivity(Intent(this, Homeactivity::class.java))
                        else -> startActivity(Intent(this, Login::class.java))
                    }
                    finish()
                }
                .addOnFailureListener {
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }

        }, 2000)
    }
}
