package com.example.skinaura20

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Toggle states for password visibility
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Views Initialization based on your XML
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtMobile = findViewById<EditText>(R.id.edtMobile)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val edtConfirmPassword = findViewById<EditText>(R.id.edtConfirmPassword)
        val edtDOB = findViewById<EditText>(R.id.edtDOB)
        val btnSignup = findViewById<MaterialButton>(R.id.btnSignup)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val txtLoginNow = findViewById<TextView>(R.id.txtLoginNow)

        // --- PASSWORD EYE TOGGLE LOGIC ---

        edtPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (edtPassword.right - edtPassword.compoundDrawables[2].bounds.width() - edtPassword.paddingEnd)) {
                    if (isPasswordVisible) {
                        edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_admin_panel_settings_24, 0, R.drawable.eye, 0)
                    } else {
                        edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_admin_panel_settings_24, 0, R.drawable.eye, 0) // Yahan agar eye_off ho toh wo laga sakte hain
                    }
                    isPasswordVisible = !isPasswordVisible
                    edtPassword.setSelection(edtPassword.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        edtConfirmPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (edtConfirmPassword.right - edtConfirmPassword.compoundDrawables[2].bounds.width() - edtConfirmPassword.paddingEnd)) {
                    if (isConfirmPasswordVisible) {
                        edtConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        edtConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_admin_panel_settings_24, 0, R.drawable.eye, 0)
                    } else {
                        edtConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        edtConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_admin_panel_settings_24, 0, R.drawable.ic_eye_open, 0)
                    }
                    isConfirmPasswordVisible = !isConfirmPasswordVisible
                    edtConfirmPassword.setSelection(edtConfirmPassword.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // --- DATE OF BIRTH PICKER ---

        edtDOB.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d -> edtDOB.setText("$d/${m + 1}/$y") },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // --- LOGIN NAVIGATION ---

        txtLoginNow.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        // --- SIGNUP LOGIC ---

        btnSignup.setOnClickListener {
            val name = edtUsername.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val mobile = edtMobile.text.toString().trim()
            val password = edtPassword.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()
            val dob = edtDOB.text.toString().trim()

            // Basic Validation
            if (name.isEmpty()) { edtUsername.error = "Name required"; return@setOnClickListener }
            if (email.isEmpty()) { edtEmail.error = "Email required"; return@setOnClickListener }
            if (mobile.length != 10) { edtMobile.error = "Enter 10 digit mobile"; return@setOnClickListener }
            if (password.length < 6) { edtPassword.error = "Min 6 characters"; return@setOnClickListener }
            if (password != confirmPassword) { edtConfirmPassword.error = "Password mismatch"; return@setOnClickListener }
            if (dob.isEmpty()) { edtDOB.error = "Select Date of Birth"; return@setOnClickListener }

            progressBar.visibility = View.VISIBLE
            btnSignup.visibility = View.INVISIBLE // Button hide kar dena taaki double click na ho

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val userId = auth.currentUser!!.uid
                    val userMap = hashMapOf(
                        "uid" to userId,
                        "name" to name,
                        "email" to email,
                        "mobile" to mobile,
                        "dob" to dob,
                        "role" to "user",
                        "skinType" to "" // Initial empty skin type
                    )

                    db.collection("users").document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            auth.signOut() // User ko direct login nahi karwa rahe, security ke liye
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, "Account Created! Please Login.", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            progressBar.visibility = View.GONE
                            btnSignup.visibility = View.VISIBLE
                            Toast.makeText(this, "Firestore Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    btnSignup.visibility = View.VISIBLE
                    Toast.makeText(this, "Auth Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}