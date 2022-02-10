package com.kingkoval.leemchat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

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
            holder.tv_time_send_message.text = convertTime(message.timeMessage!! as Long)
        } else{
            val viewHolder = holder as ReceiveMessageViewHolder

            holder.tv_receive_message.text = message.message
            holder.tv_time_receive_message.text = convertTime(message.timeMessage!! as Long)
        }

    }

    override fun getItemViewType(position: Int): Int {

        val message = messageList[position]

        if(FirebaseAuth.getInstance().currentUser!!.uid != message.senderUid)
            return RECEIVE
        else
            return SEND
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    @SuppressLint("NewApi")
    fun convertTime(time: Long): String{
        val i = Instant.ofEpochMilli(time)
        val zone = ZoneId.of(ZonedDateTime.now().zone.id)
        Log.i("ZONE123", zone.toString())

        val date = ZonedDateTime.ofInstant(i, zone)

        val now = ZonedDateTime.now().toInstant().toEpochMilli()


//        Log.i("1day", Instant.)
        Log.i("difference!", "now = $now")
        Log.i("difference!", "i = ${i.toEpochMilli()}")
        Log.i("difference!", "dif = " +(now - i.toEpochMilli()).toString())

        if((now - date.toInstant().toEpochMilli()) < 86400000){
            Log.i("difference!", "now = $now")
            Log.i("difference!", "i = ${date.toInstant().toEpochMilli()}")
            Log.i("difference!", "dif = " +(now - date.toInstant().toEpochMilli()).toString())
            return date.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            return date.format(DateTimeFormatter.ofPattern("d LLL HH:mm"))
        }


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