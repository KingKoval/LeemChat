package com.kingkoval.leemchat.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kingkoval.leemchat.R
import com.kingkoval.leemchat.adapter.MessageAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.iv_user_photo
import kotlinx.android.synthetic.main.activity_chat.tv_user_name
import com.kingkoval.leemchat.model.Message

class ChatActivity : AppCompatActivity() {

    var  messageList = ArrayList<Message>()

    var senderRoom: String? = null
    var receiverRoom: String? = null
    var senderUid: String? = null

    lateinit var dbRef: DatabaseReference
    lateinit var  messageAdapter: MessageAdapter

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        dbRef = FirebaseDatabase.getInstance().getReference("chats")

        messageAdapter = MessageAdapter(this, messageList)
        recycler_view_messages.layoutManager = LinearLayoutManager(this)
        recycler_view_messages.adapter = messageAdapter

        val profileImage = intent.getStringExtra("profileImage")
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        Glide.with(this).load(profileImage).into(iv_user_photo)
        tv_user_name.text = name


        senderUid = FirebaseAuth.getInstance().currentUser!!.uid

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid


        getMessageFromDatabase()

        et_message.setOnClickListener {
            recycler_view_messages.scrollToPosition(messageAdapter.itemCount-1)
        }

        ib_send.setOnClickListener {
            addMessageToDatabase()
            et_message.setText("")
        }

    }

    fun addMessageToDatabase(){
        val message = et_message.text.toString()
        val messageObject = Message(message, senderUid!!)

        dbRef.child(senderRoom!!).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                    dbRef.child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
    }

    fun getMessageFromDatabase(){
        dbRef.child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(dataSnapshot: DataSnapshot in snapshot.children){
                        val message = dataSnapshot.getValue(Message::class.java)

                        if(message != null){
                            messageList.add(message)
                        }
                    }

                    messageAdapter.notifyDataSetChanged()
                    recycler_view_messages.scrollToPosition(messageAdapter.itemCount-1)

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}