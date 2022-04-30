package com.kingkoval.leemchat.adapter

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.ActionBar.LayoutParams.WRAP_CONTENT
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.marginRight
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kingkoval.leemchat.R
import com.kingkoval.leemchat.activity.ChatActivity
import com.kingkoval.leemchat.model.Friend
import com.kingkoval.leemchat.model.Message
import org.w3c.dom.Text

class FriendsAdapter(val activity: Activity, val context: Context, val friends: ArrayList<Friend>): RecyclerView.Adapter<FriendsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_friend_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val friend = friends[position]

        Glide.with(context).load(friend.profileImage).into(holder.iv_friend_photo)
        holder.tv_friend_name.text = friend.name


        val currentUser = FirebaseAuth.getInstance().currentUser
        //get and set last message
        val chatRoom = friend.uid + currentUser!!.uid
        val query: Query = FirebaseDatabase.getInstance().getReference("chats")
            .child(chatRoom).child("messages").orderByKey().limitToLast(1)

        query.addValueEventListener(object: ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnapshot: DataSnapshot in snapshot.children){
                    val message = dataSnapshot.getValue(Message::class.java)

                    if(message != null){
                        Log.i("message in Main", "message is not null")
                    } else {
                        Log.i("message in Main", "powna sraka")
                    }

                    Log.i("message in Main", message!!.message)

                    if(FirebaseAuth.getInstance().currentUser!!.uid == message.senderUid){
                        holder.tv_last_sms.text = message.message
                    } else if(friend.uid == message.senderUid){
                        val param = holder.tv_you.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(0)

                        holder.tv_you.layoutParams = param
                        holder.tv_you.text = ""
                        holder.tv_last_sms.text = message.message
                    }

                    FirebaseDatabase.getInstance().getReference("users")
                        .child(currentUser.uid)
                        .child("friends")
                        .child(friend.uid)
                        .child("last_time_sms")
                        .setValue(message.timeMessage as Long)

                    holder.tv_last_time_sms.text = message.formatForUsersList(message.convertTime(message.timeMessage as Long))

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("last message!", error.message)
            }

        })


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("profileImage", friend.profileImage)
            Log.i("ChatSUKA", friend.profileImage)
            intent.putExtra("name", friend.name)
            intent.putExtra("uid", friend.uid)


            val options = ActivityOptions.makeSceneTransitionAnimation(
                context as Activity?,
                Pair(holder.card_view_friend_photo, "user_photo")
            )

            context.startActivity(intent, options.toBundle())
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val iv_friend_photo: ImageView = itemView.findViewById(R.id.iv_user_photo)

        val tv_friend_name: TextView = itemView.findViewById(R.id.tv_user_name)
        val tv_you: TextView = itemView.findViewById(R.id.tv_you)
        val tv_last_sms: TextView = itemView.findViewById(R.id.tv_last_sms)
        val tv_last_time_sms: TextView = itemView.findViewById(R.id.tv_time_last_sms)

        val card_view_friend_photo: CardView = itemView.findViewById(R.id.card_view_user_photo)
//        val tv_count_unread_sms: TextView = itemView.findViewById(R.id.tv_count_unread_sms)
    }
}