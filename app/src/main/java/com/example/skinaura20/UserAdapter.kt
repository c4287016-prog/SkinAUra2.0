package com.example.skinaura20.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skinaura20.R
import com.example.skinaura20.model.UserModel

class UserAdapter(private val userList: ArrayList<UserModel>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.userName)
        val email: TextView = view.findViewById(R.id.userEmail)
        val dob: TextView = view.findViewById(R.id.userDOB)
        val skin: TextView = view.findViewById(R.id.userSkinType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.name.text = user.name
        holder.email.text = user.email


        // DOB mapping (Firestore field: 'dob')
        holder.dob.text = "DOB: ${if (user.dob.isNullOrEmpty()) "N/A" else user.dob}"

        // SkinType mapping (Firestore field: 'skinType')
        holder.skin.text = user.skinType?.uppercase() ?: "NOT SPECIFIED"
    }

    override fun getItemCount() = userList.size
}