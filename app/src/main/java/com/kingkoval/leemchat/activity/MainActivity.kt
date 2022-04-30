package com.kingkoval.leemchat.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.kingkoval.leemchat.R
import com.kingkoval.leemchat.SearchFriendsFragment
import com.kingkoval.leemchat.model.User
import com.kingkoval.leemchat.adapter.FriendsAdapter
import com.kingkoval.leemchat.model.Friend
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    var usersList = ArrayList<Friend>()
    lateinit var auth: FirebaseAuth

    var check = false

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        fab_search_friends.setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, SearchFriendsFragment())
                    .addToBackStack(null)
                    .commit()
        }

        recycler_view_users.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)


        setCurrUserData()
//        getFriendsList(usersList)



//        usersAdapter.notifyDataSetChanged().run {
//            Log.i("psyun", "ahujeno")
//            usersList.sortedByDescending { it.name }
//            usersAdapter = FriendsAdapter(this@MainActivity, this@MainActivity, usersList)
//            recycler_view_users.adapter = usersAdapter
//        }

//        usersList.sortedByDescending { it.name }



        val dbRef = FirebaseDatabase.getInstance().getReference("users")
            .child(auth.currentUser!!.uid)
            .child("friends")


        dbRef.orderByChild("last_time_sms").addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                usersList.clear()

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val friend = dataSnapshot.getValue(Friend::class.java)

                    if (friend != null)
                        usersList.add(friend)

                }

                usersList.reverse()

                var usersAdapter = FriendsAdapter(this@MainActivity, this@MainActivity, usersList)

                usersAdapter.notifyDataSetChanged()

                recycler_view_users.adapter = usersAdapter

                if(usersList.size <= 0)
                    linear_layout_warning.visibility = View.VISIBLE
                else
                    linear_layout_warning.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        popup_menu.setOnClickListener {
            showPopupMenu()
        }

    }



    fun setCurrUserData(){
        FirebaseDatabase.getInstance().getReference("users")
            .child(auth.currentUser!!.uid).get().addOnSuccessListener {
                val value = it.getValue(User::class.java)

                Glide.with(this).load(value!!.profileImage).into(iv_user_photo)
                tv_user_name.text = value.name
            }
    }


    fun getFriendsList(friendsList1: ArrayList<Friend>){
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
            .child(auth.currentUser!!.uid)
            .child("friends")


        dbRef.addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                friendsList1.clear()

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val friend = dataSnapshot.getValue(Friend::class.java)

                    if (friend != null) {
                        friendsList1.add(friend)
                        Log.i("pisyun", friend.name)
                        sortArray(friendsList1)
                    }

                }

                if(friendsList1.size <= 0)
                    linear_layout_warning.visibility = View.VISIBLE
                else
                    linear_layout_warning.visibility = View.INVISIBLE
//                usersList.clear()

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    fun sortArray(usersList1: ArrayList<Friend>): ArrayList<Friend>{

        for(i in 0 until (usersList1.size-1)){
            for(x in 0 until (usersList1.size - i - 1)){
                if(usersList1[x].time_last_sms > usersList1[x+1].time_last_sms){
                    val temp = usersList1[x]

                    usersList1[x] = usersList1[x+1]
                    usersList1[x+1] = temp
                }
            }
        }

        for(i in usersList1){
            Log.i("names", i.name)
        }


        return usersList1
//        getFriendsList(usersList1)

        Log.i("arrayTest", usersList.toString())
    }

    @SuppressLint("ResourceType", "NewApi")
    fun showPopupMenu(){
        val view = layoutInflater.inflate(R.layout.popup_menu, null)
        val popupMenu = PopupWindow(this)

        val tv_settings = view.findViewById<TextView>(R.id.tv_settings)
        val tv_about_us = view.findViewById<TextView>(R.id.tv_about_us)
        val tv_logout = view.findViewById<TextView>(R.id.tv_logout)

        popupMenu.contentView = view
        popupMenu.isOutsideTouchable = true
        popupMenu.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_menu_sender))
        popupMenu.elevation = 20f
        popupMenu.isFocusable = true

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        popupMenu.width = dm.widthPixels/2
        popupMenu.showAsDropDown(popup_menu, (-(dm.widthPixels/2.1)).toInt(), 0)

        tv_about_us.setOnClickListener {
            startActivity(Intent(this, AboutUsActivity::class.java))
            popupMenu.dismiss()
        }

        tv_logout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}