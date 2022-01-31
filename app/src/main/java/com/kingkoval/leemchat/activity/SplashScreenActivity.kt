package com.kingkoval.leemchat.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import com.kingkoval.leemchat.R

class SplashScreenActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences

    val CONST_FIRST_START: String = "firstStart"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        preferences = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        val firstStart: Boolean = preferences.getBoolean(CONST_FIRST_START, true)

        val motionLayout = findViewById<MotionLayout>(R.id.motionLayout)
        motionLayout.addTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }


            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if(firstStart) {
                    editor.putBoolean(CONST_FIRST_START, false)
                    editor.apply()

                    startActivity(Intent(this@SplashScreenActivity, RegistrationActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                } else{
                    startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }

        })

    }
}