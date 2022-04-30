package com.kingkoval.leemchat.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kingkoval.leemchat.R
import com.kingkoval.leemchat.SearchFriendsFragment
import com.kingkoval.leemchat.model.Friend
import com.kingkoval.leemchat.model.User

class UsersAdapter(val activity: Activity, val context: Context, val users: ArrayList<User>) : RecyclerView.Adapter<UsersAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsersAdapter.MyViewHolder, position: Int) {
        val user = users[position]

        if(user == null)
            Log.i("pidr1", "hujnia")


        Glide.with(context).load(user.profileImage).into(holder.iv_user_photo)

        holder.tv_user_name.text = user.name
        holder.tv_email_user.text = user.email

        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

//        holder.btn_add_friend.visibility = View.VISIBLE

        dbRef.child(currentUser).child("friends").addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for(dataSnapshot: DataSnapshot in snapshot.children){
                        val friend = dataSnapshot.getValue(Friend::class.java)

                        if(friend!!.uid.isNotEmpty()) {
                            if (user.uid == friend!!.uid) {
                                holder.btn_add_friend.visibility = View.INVISIBLE
//                                Log.i("TAGIL", "NAAAAAA")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("DEF", error.message)
                }

            }
        )

        holder.btn_add_friend.setOnClickListener {
            addFriend(holder, dbRef, currentUser, user)
        }
    }

    fun addFriend(
        holder: UsersAdapter.MyViewHolder,
        dbRef: DatabaseReference,
        currentUser: String,
        user: User
    ){
            dbRef
                .child(currentUser)
                .child("friends")
                .child(user.uid)
                .setValue(Friend(user.profileImage, user.name, user.uid, 0))

            dbRef.child(currentUser).get().addOnSuccessListener {
                val value = it.getValue(User::class.java)

                dbRef
                    .child(user.uid)
                    .child("friends")
                    .child(value!!.uid)
                    .setValue(Friend(value!!.profileImage, value.name, value.uid, 0))
            }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val iv_user_photo: ImageView = itemView.findViewById(R.id.iv_user_photo)

        val tv_user_name: TextView = itemView.findViewById(R.id.tv_user_name)
        val tv_email_user: TextView = itemView.findViewById(R.id.tv_email_user)
        val btn_add_friend: ImageButton = itemView.findViewById(R.id.btn_add_friend)

        val linear_layout_user: LinearLayout = itemView.findViewById(R.id.linear_layout_user)
    }
}