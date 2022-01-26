package com.kingkoval.leemchat

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var usersList = ArrayList<User>()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view_users.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)



//        usersList.add(User("https://i.stack.imgur.com/l60Hf.png", "Ivan", "ivan@eblan.com", "12321fadas"))
//        usersList.add(User("https://i.stack.imgur.com/l60Hf.png", "Kolua", "ivan@eblan.com", "12321fadas"))
//        usersList.add(User("https://i.stack.imgur.com/l60Hf.png", "Petya", "ivan@eblan.com", "12321fadas"))
//        usersList.add(User("https://i.stack.imgur.com/l60Hf.png", "Bohdan", "ivan@eblan.com", "12321fadas"))

//        var adapter = UsersAdapter(this, usersList)

//        recycler_view_users.adapter = adapter

        getUsersList()
    }

    fun getUsersList(){
        val dbRef = FirebaseDatabase.getInstance().getReference("users")

        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnapshot: DataSnapshot in snapshot.children){
                    val user = dataSnapshot.getValue(User::class.java)

                    if (user != null) {
                        usersList.add(user)
                    }
                }

                val usersAdapter = UsersAdapter(this@MainActivity, usersList)

                recycler_view_users.adapter = usersAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}