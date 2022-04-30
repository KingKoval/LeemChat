package com.kingkoval.leemchat

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.kingkoval.leemchat.adapter.UsersAdapter
import com.kingkoval.leemchat.model.Friend
import com.kingkoval.leemchat.model.User
import kotlinx.android.synthetic.main.fragment_search_friends.view.*
import kotlin.math.max
class SearchFriendsFragment() : Fragment() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_friends, container, false)
        val background = view.fragment_background

        view.recycler_view_users1.layoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)

        getUserList(view.recycler_view_users1)

        val font_nunitoRegular = ResourcesCompat.getFont(requireActivity(), R.font.nunito_regular)

        val searchView = view.search_view.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
        val searchView_line = view.search_view.findViewById(androidx.appcompat.R.id.search_plate) as View
        searchView_line.background = null
        searchView.typeface = font_nunitoRegular



        view.addOnLayoutChangeListener(object: View.OnLayoutChangeListener{
            override fun onLayoutChange(
                p0: View?,
                p1: Int,
                p2: Int,
                p3: Int,
                p4: Int,
                p5: Int,
                p6: Int,
                p7: Int,
                p8: Int
            ) {
                p0!!.removeOnLayoutChangeListener(this)

                val cx = background.right - getDips(44)
                val cy = background.bottom - getDips(44)

                val finalRadius = max(
                    background.width + (background.width/3),
                    background.height + (background.height/3))

                val anim = ViewAnimationUtils.createCircularReveal(
                    background,
                    cx,
                    cy,
                    0F,
                    finalRadius.toFloat()
                )

                anim.duration = 1000
                anim.start()

            }

        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)

        val background = view.fragment_background

        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val cx = background.width - getDips(44)
                val cy = background.bottom - getDips(44)

                val finalRadius = max(background.width, background.height)

                val anim = ViewAnimationUtils.createCircularReveal(
                    background,
                    cx,
                    cy,
                    finalRadius.toFloat(),
                    0F
                )

                anim.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {
//                    TODO("Not yet implemented")
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        view.visibility = View.INVISIBLE
                        activity?.supportFragmentManager!!
                            .beginTransaction()
                            .setCustomAnimations(0,0)
                            .remove(this@SearchFriendsFragment)
                            .commit()
                    }

                    override fun onAnimationCancel(p0: Animator?) {
//                    TODO("Not yet implemented")
                    }

                    override fun onAnimationRepeat(p0: Animator?) {
//                    TODO("Not yet implemented")
                    }

                })

                anim.duration = 1000
                anim.start()

            }
        }

        activity?.onBackPressedDispatcher!!.addCallback(callback)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFriendsFragment()
    }

    fun SearchView.setTypeFace(typeface: Typeface){

    }

    @SuppressLint("NotifyDataSetChanged")
    fun getUserList(view: RecyclerView){
        val dbRef = FirebaseDatabase.getInstance().getReference("users")

        var userList = ArrayList<User>()
        var usersAdapter: UsersAdapter

        val currUser = FirebaseAuth.getInstance().currentUser!!

        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for(dataSnapshot: DataSnapshot in snapshot.children){
                    val user = dataSnapshot.getValue(User::class.java)
                    var currUser = FirebaseAuth.getInstance().currentUser

                    if(user != null && user.uid != currUser!!.uid) {
                        userList.add(user)
                    }
                }

                val usersAdapter = UsersAdapter(requireActivity(), requireActivity(), userList)

                usersAdapter.notifyDataSetChanged()

                view.recycler_view_users1.adapter = usersAdapter

            }


            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun getDips(dps: Int): Int{
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dps.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}