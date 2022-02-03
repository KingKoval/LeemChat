package com.kingkoval.leemchat.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.kingkoval.leemchat.R
import com.kingkoval.leemchat.model.User
import com.kingkoval.leemchat.adapter.UsersAdapter
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    var usersList = ArrayList<User>()
    lateinit var auth: FirebaseAuth

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        recycler_view_users.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        getUsersList()

        popup_menu.setOnClickListener {
            showPopupMenu()
        }


    }

    fun getUsersList(){
        val dbRef = FirebaseDatabase.getInstance().getReference("users")

        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnapshot: DataSnapshot in snapshot.children){
                    val user = dataSnapshot.getValue(User::class.java)

                    if (user != null) {
                        if(user.uid == auth.uid){
                            Glide.with(this@MainActivity).load(user.profileImage).into(iv_user_photo)
                            tv_user_name.text = "Hello, " + user.name
                        } else{
                            usersList.add(user)
                        }
                    }
                }

                val usersAdapter = UsersAdapter(this@MainActivity, this@MainActivity, usersList)

                recycler_view_users.adapter = usersAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



    fun showPopupMenu(){
        val wrapper = ContextThemeWrapper(this, R.style.CustomFontPopupMenu)
        val popupMenu = PopupMenu(wrapper, popup_menu)
        popupMenu.inflate(R.menu.popupmenu)

        popupMenu.setOnMenuItemClickListener {it ->
            when(it.itemId){
                R.id.menu_item_about ->
                    startActivity(Intent(this, AboutUsActivity::class.java))
                R.id.menu_item_logout -> {
                    Firebase.auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()

                }
                else ->
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

            true
        }

        popupMenu.show()
    }

}