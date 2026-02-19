package com.example.skinaura20

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Result : AppCompatActivity() {

    private lateinit var skinResultText: TextView
    private lateinit var quoteText: TextView
    private lateinit var btnHome: MaterialButton

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        skinResultText = findViewById(R.id.skin_result)
        quoteText = findViewById(R.id.tv_quote)
        btnHome = findViewById(R.id.btn_home)

        // Quiz activity se result get karna
        val skinType = intent.getStringExtra("SKIN_RESULT") ?: "NORMAL SKIN"
        skinResultText.text = "Your Skin Type is: $skinType"

        setQuoteAccordingToSkin(skinType)

        btnHome.setOnClickListener {
            // Home par jaane se pehle database mein save karenge
            saveSkinTypeToFirestore(skinType)
        }
    }

    private fun saveSkinTypeToFirestore(skinType: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Data map taiyar karein
            val userUpdate = hashMapOf(
                "skinType" to skinType,
                "quizCompleted" to true
            )

            // Firestore mein 'users' collection ke andar update karein
            // SetOptions.merge() ka use kiya hai taaki purana data delete na ho
            db.collection("users").document(userId)
                .set(userUpdate, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()

                    // Homeactivity par bhejein aur skinType pass karein
                    val intent = Intent(this, Homeactivity::class.java)
                    intent.putExtra("OPEN_FRAGMENT", skinType)

                    // Ye flags purani saari activities (Quiz, etc.) ko clear kar denge
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    // Fail hone par bhi Home par bhej dete hain taaki user stuck na ho
                    startActivity(Intent(this, Homeactivity::class.java))
                    finish()
                }
        } else {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun setQuoteAccordingToSkin(skinType: String) {
        val quote = when (skinType) {
            "OILY SKIN" -> "Oily skin shines with natural glow. Balance it, don’t fight it."
            "DRY SKIN" -> "Dry skin craves love. Hydration is your best friend."
            "COMBINATION SKIN" -> "Your skin is unique. Balance is your beauty."
            "NORMAL SKIN" -> "Normal skin is a blessing. Maintain it with gentle care."
            else -> "Beautiful skin begins the moment you decide to take care of it."
        }
        quoteText.text = quote
    }
}