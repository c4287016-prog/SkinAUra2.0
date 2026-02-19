package com.example.skinaura20

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val edtEmail = findViewById<EditText>(R.id.edtemail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val txtRegisterNow = findViewById<TextView>(R.id.txtRegisterNow)

        // ✅ Password Visibility Toggle Logic
        // Hum drawableEnd (index 2) par click detect karenge
        edtPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Aapke XML mein paddingEnd 14dp hai, usey dhyan mein rakhte hue:
                if (event.rawX >= (edtPassword.right - edtPassword.compoundDrawables[2].bounds.width() - edtPassword.paddingEnd)) {
                    if (isPasswordVisible) {
                        // 1. Password Hide Karo
                        edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        // Yahan R.drawable.eye use kiya hai jo aapke XML mein hai
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_admin_panel_settings_24, 0, R.drawable.eye, 0)
                    } else {
                        // 2. Password Show Karo
                        edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        // Agar aapke paas 'eye_off' icon hai toh wo yahan laga sakte hain
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_admin_panel_settings_24, 0, R.drawable.ic_eye_open, 0)
                    }
                    isPasswordVisible = !isPasswordVisible
                    edtPassword.setSelection(edtPassword.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        txtRegisterNow.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            checkUserRole(uid, progressBar, btnLogin)
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        btnLogin.isEnabled = true
                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkUserRole(uid: String, progressBar: ProgressBar, btnLogin: MaterialButton) {
        db.collection("admin").document(uid).get()
            .addOnSuccessListener { document ->
                progressBar.visibility = View.GONE
                if (document.exists()) {
                    Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Admin::class.java))
                } else {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Quiz::class.java))
                }
                finish()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
                Toast.makeText(this, "Firestore Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}