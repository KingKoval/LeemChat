package com.kingkoval.leemchat.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kingkoval.leemchat.R
import com.kingkoval.leemchat.adapter.MessageAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.iv_user_photo
import kotlinx.android.synthetic.main.activity_chat.tv_user_name
import com.kingkoval.leemchat.model.Message
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.send_message_item.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    var  messageList = ArrayList<Message>()
    var  messageListReceiver = ArrayList<Message>()

    var senderRoom: String? = null
    var receiverRoom: String? = null
    var senderUid: String? = null
    var receiverUid: String? = null

    lateinit var dbRef: DatabaseReference
    lateinit var  messageAdapter: MessageAdapter

    lateinit var imageUri: Uri

    val resultLauncherUploadImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode == RESULT_OK && result.data != null){
            //checkUserPhoto = true
            Log.i("IMAGE!!!", "Image was selected")
            imageUri = result.data!!.data!!


            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            }

            ib_send.setOnClickListener {
                iv_send_image.background = BitmapDrawable(bitmap)
            }

        } else{
            //checkUserPhoto = false
        }

    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        dbRef = FirebaseDatabase.getInstance().getReference("chats")

        senderUid = FirebaseAuth.getInstance().currentUser!!.uid

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        messageAdapter = MessageAdapter(
            this,
            messageList,
            messageListReceiver,
            senderRoom!!,
            receiverRoom!!
        )

        recycler_view_messages.layoutManager = LinearLayoutManager(this)
        recycler_view_messages.adapter = messageAdapter


        val profileImage = intent.getStringExtra("profileImage")
        val name = intent.getStringExtra("name")

        receiverUid = intent.getStringExtra("uid")

        Glide.with(this).load(profileImage).into(iv_user_photo)
        tv_user_name.text = name

        getMessageFromDatabase()

        val lm: LinearLayoutManager = recycler_view_messages.layoutManager as LinearLayoutManager
        lm.stackFromEnd = true

        et_message.setOnClickListener {
            recycler_view_messages.scrollToPosition(messageAdapter.itemCount-1)
        }

//        ib_image.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//
//            resultLauncherUploadImage.launch(intent)
//        }

        ib_send.setOnClickListener {
            addMessageToDatabase()
            et_message.setText("")
        }

    }

    fun addMessageToDatabase(){
        val message = et_message.text.toString()
        val timeStamp: Map<String, String> = ServerValue.TIMESTAMP

        val messageObject = Message(message, senderUid!!, timeStamp)

//        val chatRoom = receiverUid + FirebaseAuth.getInstance().currentUser!!.uid
//        val query: Query = FirebaseDatabase.getInstance().getReference("chats")
//            .child(chatRoom).child("messages").orderByKey().limitToLast(1)
//
//        query.addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(dataSnapshot: DataSnapshot in snapshot.children){
//                    val message = dataSnapshot.getValue(Message::class.java)
//
////                    dbRef.child(receiverUid.toString()).
////                    dbRef.child(senderUid.toString()).child("last_time_sms").setValue(timeStamp)
////

        FirebaseDatabase.getInstance().getReference("users")
            .child(receiverUid.toString()).child("last_time_sms").setValue(timeStamp)

        FirebaseDatabase.getInstance().getReference("users")
            .child(senderUid.toString()).child("last_time_sms").setValue(timeStamp)
////
////                    dbRef.child(receiverUid.toString()).up
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })

        if(!message.trim().isEmpty()) {
            dbRef.child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    dbRef.child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }

        }
    }

    fun getMessageFromDatabase(){
        dbRef.child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(dataSnapshot: DataSnapshot in snapshot.children){
                        val messageData = dataSnapshot.getValue(Message::class.java)

                        if(messageData != null){
                            messageList.add(
                                Message(
                                    message = messageData.message,
                                    senderUid = messageData.senderUid,
                                    timeMessage = messageData.timeMessage,
                                    messageKey = dataSnapshot.key.toString()
                                )
                            )

                            Log.i("smsky", dataSnapshot.key.toString())
                        }
                    }

                    messageAdapter.notifyDataSetChanged()

                    recycler_view_messages.scrollToPosition(messageAdapter.itemCount-1)

                }
                override fun onCancelled(error: DatabaseError) { }
            })

        dbRef.child(receiverRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(dataSnapshot: DataSnapshot in snapshot.children){
                        val messageData = dataSnapshot.getValue(Message::class.java)

                        if(messageData != null){
                            messageListReceiver.add(
                                Message(
                                    message = messageData.message,
                                    senderUid = messageData.senderUid,
                                    timeMessage = messageData.timeMessage,
                                    messageKey = dataSnapshot.key.toString()
                                )
                            )
                        }
                    }

                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) { }
            })
    }
}