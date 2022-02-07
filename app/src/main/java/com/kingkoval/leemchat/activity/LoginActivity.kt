package com.kingkoval.leemchat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kingkoval.leemchat.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var checkEmail: Boolean = false
    private var checkPass: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        tv_sign_up.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        validateEnterDate()

        btn_login.setOnClickListener{
            if(!checkEmail || !checkPass){
                Toast.makeText(this, "Failed sign in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else{
                signInUser()
            }

        }
    }

    fun signInUser(){
        auth.signInWithEmailAndPassword(et_email.text.toString(), et_pass.text.toString()).
                addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Success sign in", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    } else{
                        Toast.makeText(this, "Failed sign in", Toast.LENGTH_SHORT).show()
                        et_pass.setText("")
                        input_layout_pass.isErrorEnabled = false
                    }
                }
    }

    fun validateEnterDate(){
        et_email.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val email: String = et_email.text.toString()

                if(email.length == 0){
                    input_layout_email.error = "Email cannot be empty"
                    checkEmail = false
                } else if(!EMAIL_ADDRESS.matcher(et_email.text.toString()).matches()){
                    input_layout_email.error = "Invalid email!"
                    checkEmail = false
                } else{
                    checkEmail = true
                    input_layout_email.error = null
                    input_layout_email.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) { }
        })

        et_pass.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(et_pass.text.toString().length < 8){
                    input_layout_pass.error = "Password must contain a minimum 8 characters in length"
                    checkPass = false
                } else{
                    checkPass = true
                    input_layout_pass.error = null
                    input_layout_pass.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) { }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}

