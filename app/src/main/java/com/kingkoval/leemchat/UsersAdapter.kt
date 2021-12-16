package com.kingkoval.leemchat

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_user_list.*

class UsersAdapter(val context: Context, val users: ArrayList<User>): RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = users[position]

        holder.tv_user_name.text = user.name
        holder.tv_last_sms.text = user.lastSms
        holder.tv_last_time_sms.text = user.timeLastSms
        //holder.tv_count_unread_sms.text = user.countUnreadSms
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val iv_user_photo: ImageView = itemView.findViewById(R.id.iv_user_photo)

        val tv_user_name: TextView = itemView.findViewById(R.id.tv_user_name)
        val tv_last_sms: TextView = itemView.findViewById(R.id.tv_last_sms)
        val tv_last_time_sms: TextView = itemView.findViewById(R.id.tv_time_last_sms)
        val tv_count_unread_sms: TextView = itemView.findViewById(R.id.tv_count_unread_sms)
    }
}