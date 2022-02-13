package com.kingkoval.leemchat.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kingkoval.leemchat.R
import com.kingkoval.leemchat.activity.ChatActivity
import com.kingkoval.leemchat.model.Message
import com.kingkoval.leemchat.model.User

class UsersAdapter(val activity: Activity, val context: Context, val users: ArrayList<User>): RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = users[position]

        Glide.with(context).load(user.profileImage).into(holder.iv_user_photo)
        holder.tv_user_name.text = user.name


        //get and set last message
        val chatRoom = user.uid + FirebaseAuth.getInstance().currentUser!!.uid
        val query: Query = FirebaseDatabase.getInstance().getReference("chats")
            .child(chatRoom).child("messages").orderByKey().limitToLast(1)

        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnapshot: DataSnapshot in snapshot.children){
                    val message = dataSnapshot.getValue(Message::class.java)

                    if(message != null){
                        Log.i("message in Main", "message is not null")
                    } else {
                        Log.i("message in Main", "powna sraka")
                    }

                    Log.i("message in Main", message!!.message)

                    if(FirebaseAuth.getInstance().currentUser!!.uid.equals(message.senderUid)){
                        holder.tv_last_sms.text = "You: " + message.message
                    } else if(user.uid == message.senderUid){
                        holder.tv_last_sms.text = message.message
                    }

                    holder.tv_last_time_sms.text = message.formatForUsersList(message.convertTime(message.timeMessage as Long))

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("last message!", error.message)
            }

        })


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("profileImage", user.profileImage)
            Log.i("ChatSUKA", user.profileImage)
            intent.putExtra("name", user.name)
            intent.putExtra("uid", user.uid)

            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val iv_user_photo: ImageView = itemView.findViewById(R.id.iv_user_photo)

        val tv_user_name: TextView = itemView.findViewById(R.id.tv_user_name)
        val tv_last_sms: TextView = itemView.findViewById(R.id.tv_last_sms)
        val tv_last_time_sms: TextView = itemView.findViewById(R.id.tv_time_last_sms)
//        val tv_count_unread_sms: TextView = itemView.findViewById(R.id.tv_count_unread_sms)
    }
}