package com.example.skinaura20.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.skinaura20.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etAltMobile: EditText
    private lateinit var etPass: EditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etName = view.findViewById(R.id.etEditName)
        etEmail = view.findViewById(R.id.etEditEmail)
        etMobile = view.findViewById(R.id.etEditMobile)
        etAltMobile = view.findViewById(R.id.etEditAltMobile)
        etPass = view.findViewById(R.id.etEditPassword)
        btnSave = view.findViewById(R.id.btnUpdateProfile)

        val userId = auth.currentUser?.uid ?: return view

        loadCurrentUserData(userId)

        btnSave.setOnClickListener {
            updateUserProfile(userId)
        }

        return view
    }

    private fun loadCurrentUserData(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    etName.setText(doc.getString("name") ?: "")
                    etEmail.setText(doc.getString("email") ?: "")
                    etMobile.setText(doc.getString("mobile") ?: "")
                    etAltMobile.setText(doc.getString("altMobile") ?: "")
                }
            }
    }

    private fun updateUserProfile(userId: String) {
        val newName = etName.text.toString().trim()
        val newMobile = etMobile.text.toString().trim()
        val newAltMobile = etAltMobile.text.toString().trim()
        val newPassword = etPass.text.toString().trim()

        // Empty Map banayein jisme wahi data jayega jo user ne type kiya hai
        val updates = mutableMapOf<String, Any>()

        // Sirf wahi update karo jo user ne change kiya hai/bhara hai
        if (newName.isNotEmpty()) updates["name"] = newName
        if (newMobile.isNotEmpty()) updates["mobile"] = newMobile
        if (newAltMobile.isNotEmpty()) updates["altMobile"] = newAltMobile

        // Password update sirf tabhi jab user ne kuch type kiya ho
        if (newPassword.isNotEmpty() && newPassword.length >= 6) {
            auth.currentUser?.updatePassword(newPassword)
                ?.addOnFailureListener { e ->
                    Log.e("EDIT_PROFILE", "Password Update Failed: ${e.message}")
                }
        } else if (newPassword.isNotEmpty() && newPassword.length < 6) {
            Toast.makeText(context, "Password should be 6+ chars", Toast.LENGTH_SHORT).show()
            return
        }


        if (updates.isNotEmpty()) {
            db.collection("users").document(userId).update(updates)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile Updated! ✨", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Agar kuch bhi change nahi kiya toh bas wapas chale jao
            parentFragmentManager.popBackStack()
        }
    }
}