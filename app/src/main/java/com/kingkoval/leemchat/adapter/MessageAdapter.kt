package com.kingkoval.leemchat.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.kingkoval.leemchat.R

import com.kingkoval.leemchat.model.Message
import kotlin.collections.ArrayList

class MessageAdapter(val contex: Context,
                     val messageList: ArrayList<Message>,
                     val messageListReceiver: ArrayList<Message>,
                     val senderRoom: String,
                     val receiverRoom: String
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val SEND = 1
    val RECEIVE = 2

    val height = contex.resources.displayMetrics.heightPixels

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

//        conte
        if(viewType == SEND)
            return SendMessageViewHolder(LayoutInflater.from(contex).inflate(R.layout.send_message_item, parent, false))
        else{
            return ReceiveMessageViewHolder(LayoutInflater.from(contex).inflate(R.layout.receive_message_item, parent, false))
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = messageList[position]
        val messageReceiver = messageListReceiver[position]


        if(holder.javaClass == SendMessageViewHolder::class.java){
            val viewHolder = holder as SendMessageViewHolder

            holder.itemView.setOnClickListener {
                val location = IntArray(2)

                it.getLocationInWindow(location)

                Log.i("click btn send", location[0].toString() + " = y: " + location[1].toString())

                showPopupMenu(holder.tv_send_message, holder, location[1], message, messageReceiver)
            }

            holder.tv_send_message.text = message.message
            holder.tv_time_send_message.text = message.formatForChat(message.convertTime(message.timeMessage as Long))
        } else{
            val viewHolder = holder as ReceiveMessageViewHolder

            holder.itemView.setOnClickListener {
                val location = IntArray(2)

                it.getLocationInWindow(location)

                Log.i("click btn receive", location[0].toString() + " = y: " + location[1].toString())

                showPopupMenu(holder.tv_receive_message, holder, location[1], message, messageReceiver)
            }

            holder.tv_receive_message.text = message.message
            holder.tv_time_receive_message.text = message.formatForChat(message.convertTime(message.timeMessage as Long))
        }

    }

    @SuppressLint("ResourceType", "NewApi", "UseCompatLoadingForDrawables")
    fun showPopupMenu(textView: TextView, holder: RecyclerView.ViewHolder, y: Int, message: Message, messageReceiver: Message){
        val view = LayoutInflater.from(contex).inflate(R.layout.popup_menu_message, null)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val popupMenu = PopupWindow(contex)

        val copy = view.findViewById<LinearLayout>(R.id.linear_layout_copy)
        val edit = view.findViewById<LinearLayout>(R.id.linear_layout_edit)
        val delete = view.findViewById<LinearLayout>(R.id.linear_layout_delete)

        val width = contex.resources.displayMetrics.widthPixels

        val coordinates = IntArray(2)
        textView.getLocationInWindow(coordinates)

        popupMenu.contentView = view
        popupMenu.isOutsideTouchable = true

        popupMenu.elevation = 20f
        popupMenu.isFocusable = true


        if(holder.javaClass == SendMessageViewHolder::class.java) {

            if(y > (height*2)/3){
                popupMenu.setBackgroundDrawable(contex.getDrawable(R.drawable.bg_menu_sender_down))

                if(textView.width > width/2)
                    popupMenu.showAsDropDown(textView, 0, -view.measuredHeight)
                else
                    popupMenu.showAsDropDown(textView, -textView.width*2, -view.measuredHeight)
            } else{
                popupMenu.setBackgroundDrawable(contex.getDrawable(R.drawable.bg_menu_sender))

                if(textView.width > width/2)
                    popupMenu.showAsDropDown(textView)
                else
                    popupMenu.showAsDropDown(textView, -textView.width*2, 0)
            }

        }else {

            if(y > (height*2)/3){
                popupMenu.setBackgroundDrawable(contex.getDrawable(R.drawable.bg_menu_receiver_down))

                if(textView.width > width/2)
                    popupMenu.showAsDropDown(textView, width/2, -view.measuredHeight)
                else
                    popupMenu.showAsDropDown(textView, textView.width/2, -view.measuredHeight)
            } else{
                popupMenu.setBackgroundDrawable(contex.getDrawable(R.drawable.bg_menu_receiver))

                if(textView.width > width/2)
                    popupMenu.showAsDropDown(textView, width/2, 0)
                else
                    popupMenu.showAsDropDown(textView, textView.width/2, 0)
            }

        }

        copy.setOnClickListener {
            val clipboardManager = contex.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(
                    textView.text,
                    textView.text
                )
            )

            val toast = Toast.makeText(contex, contex.getText(R.string.copy), Toast.LENGTH_SHORT/2)

            toast.view!!.setPadding(-20)
            toast.view!!.findViewById<TextView>(android.R.id.message).typeface = ResourcesCompat.getFont(contex, R.font.nunito_regular)
            toast.view!!.findViewById<TextView>(android.R.id.message).setTextColor(R.color.grey)

            toast.show()

            popupMenu.dismiss()
        }

        delete.setOnClickListener {
            if(message.message == messageReceiver.message){
//                FirebaseDatabase.getInstance().getReference("chats").child(senderRoom).child(message.messageKey).removeValue()
//                FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom).child(messageReceiver.messageKey).removeValue()

                Toast.makeText(contex, "Message deleted", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        val message = messageList[position]

        return if(FirebaseAuth.getInstance().currentUser!!.uid != message.senderUid)
            RECEIVE
        else
            SEND
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SendMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_send_message = itemView.findViewById<TextView>(R.id.tv_send_message)
        val tv_time_send_message = itemView.findViewById<TextView>(R.id.tv_time_send_message)
    }

    class ReceiveMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_receive_message = itemView.findViewById<TextView>(R.id.tv_receive_message)
        val tv_time_receive_message = itemView.findViewById<TextView>(R.id.tv_time_receive_message)
    }
}