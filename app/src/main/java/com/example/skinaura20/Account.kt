package com.example.skinaura20.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.skinaura20.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.skinaura20.fragment.*
import com.example.skinaura20.Login


class Account: Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // ID Mapping
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val tvSkinType = view.findViewById<TextView>(R.id.tvSkinType)

        val btnMyRoutine = view.findViewById<LinearLayout>(R.id.btnMyRoutine)
        val btnMyCart = view.findViewById<LinearLayout>(R.id.btnMyCart)
        val btnMyOrders = view.findViewById<LinearLayout>(R.id.btnMyOrders)
        val btnLogout = view.findViewById<LinearLayout>(R.id.btnLogout)
        val tvEditProfile = view.findViewById<TextView>(R.id.tvEditProfile)

        // 1. Fetch User Data from Firestore
        fetchUserData(tvName, tvEmail, tvPhone, tvSkinType)

        // 2. Click Listeners
        btnMyRoutine.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, Routine())
            transaction.addToBackStack(null)
            transaction.commit()
        }
        // Edit Profile click listener
        tvEditProfile.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, EditProfile()) // EditProfile fragment load hoga
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnMyCart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Cart())
                .addToBackStack(null)
                .commit()
        }

        btnMyOrders.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, MyOrder())
            transaction.addToBackStack(null) // Back button dabane par wapas Profile par aane ke liye
            transaction.commit()
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    private fun fetchUserData(
        nameTv: TextView,
        emailTv: TextView,
        phoneTv: TextView,
        skinTv: TextView
    ) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // 🔥 MATCHED WITH YOUR SCREENSHOT KEYS:
                    val nameValue = document.getString("name") ?: "No Name"
                    val emailValue = document.getString("email") ?: "No Email"
                    val mobileValue = document.getString("mobile") ?: "No Number"
                    val skinValue = document.getString("skinType") ?: "Not Set"

                    // UI update
                    nameTv.text = nameValue
                    emailTv.text = "Email: $emailValue"
                    phoneTv.text = "Phone: $mobileValue"
                    skinTv.text = "Skin Type: $skinValue"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}