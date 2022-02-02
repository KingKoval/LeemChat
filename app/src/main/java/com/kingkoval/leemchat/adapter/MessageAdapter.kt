package com.kingkoval.leemchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.kingkoval.leemchat.R

import com.kingkoval.leemchat.R.layout.send_message_item
import com.kingkoval.leemchat.R.layout.receive_message_item
import com.kingkoval.leemchat.model.Message

class MessageAdapter(val contex: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val SEND = 1
    val RECEIVE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == SEND)
            return SendMessageViewHolder(LayoutInflater.from(contex).inflate(R.layout.send_message_item, parent, false))
        else{
            return ReceiveMessageViewHolder(LayoutInflater.from(contex).inflate(R.layout.receive_message_item, parent, false))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = messageList[position]

        if(holder.javaClass == SendMessageViewHolder::class.java){
            val viewHolder = holder as SendMessageViewHolder

            holder.tv_send_message.text = message.message
        } else{
            val viewHolder = holder as ReceiveMessageViewHolder

            holder.tv_receive_message.text = message.message
        }

    }

    override fun getItemViewType(position: Int): Int {

        val message = messageList[position]

        if(FirebaseAuth.getInstance().currentUser!!.uid != message.senderId)
            return RECEIVE
        else
            return SEND
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SendMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_send_message = itemView.findViewById<TextView>(R.id.tv_send_message)
    }

    class ReceiveMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_receive_message = itemView.findViewById<TextView>(R.id.tv_receive_message)
    }
}