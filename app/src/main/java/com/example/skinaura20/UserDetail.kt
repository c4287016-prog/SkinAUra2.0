package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.adapter.UserAdapter
import com.example.skinaura20.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class UserDetail : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private var userList = ArrayList<UserModel>()
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_detail, container, false)

        recyclerView = view.findViewById(R.id.usersRecyclerView)
        progressBar = view.findViewById(R.id.userProgressBar)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdapter(userList)
        recyclerView.adapter = adapter

        fetchUsers()

        return view
    }

    private fun fetchUsers() {
        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                for (document in result) {
                    val user = document.toObject(UserModel::class.java)
                    userList.add(user)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}