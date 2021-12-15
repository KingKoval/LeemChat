package com.kingkoval.leemchat

import android.app.Instrumentation
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*
import org.w3c.dom.Text
import java.net.URI
import java.util.*
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var checkName: Boolean = false
    private var checkEmail: Boolean = false
    private var checkPass: Boolean = false
    private var checkConfirmPass: Boolean = false

    lateinit var userPhotoUri: Uri

    val resultLauncherUploadImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result:ActivityResult ->
        if(result.resultCode == RESULT_OK && result.data != null){
            Log.i("IMAGE!!!", "Image was selected")
            userPhotoUri = result.data!!.data!!


            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, userPhotoUri))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, userPhotoUri)
            }

            iv_user_photo.background = BitmapDrawable(bitmap)
        } else{

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = Firebase.auth

        iv_user_photo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            resultLauncherUploadImage.launch(intent)
        }


        tv_sign_in.setOnClickListener{
                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        validateEnterDate()

        btn_registration.setOnClickListener {
            if (!checkName
                || !checkEmail
                || !checkPass
                || !checkConfirmPass
            ) {
                Toast.makeText(this, "Failed registration", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                createUser()
            }
        }

    }

    fun uploadImageToFirebaseStorage(){
        val fileName = UUID.randomUUID().toString()

        val ref = FirebaseStorage.getInstance().getReference("/user_photo/$fileName")

        ref.putFile(userPhotoUri).addOnSuccessListener {
            Log.i("USER", "image upload to firebase storage")
        }
    }

    fun createUser(){
        auth.createUserWithEmailAndPassword(et_email.text.toString(), et_create_pass.text.toString()).
                addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        uploadImageToFirebaseStorage()
                        Toast.makeText(this, "Success registration", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    } else{
                        Log.w("FIRERROR", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }

    }

    fun validateEnterDate(){
        et_name.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val valName: String = et_name.text.toString().trim()
                Log.i("XXX", "name: " + valName)
                Log.i("XXX","name length: " + valName.length)
                if(valName.length == 0){
                    input_layout_name.error = "Name cannot be empty"
                    checkName = false
                } else if(valName.length < 2){
                    input_layout_name.error = "Name is small"
                    checkName = false
                } else if(valName.length > 25){
                    input_layout_name.error = "Name is too long"
                    checkName = false
                } else{
                    checkName = true
                    input_layout_name.error = null
                    input_layout_name.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        et_email.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val valEmail: String = et_email.text.toString()
                Log.i("XXX", "email: " + valEmail)

                if(valEmail.length == 0){
                    input_layout_email.error = "Email cannot be empty"
                    checkEmail = false
                } else if(!EMAIL_ADDRESS.matcher(valEmail).matches()){
                    input_layout_email.error = "Invalid email"
                    checkEmail = false
                } else{
                    checkEmail = true
                    input_layout_email.error = null
                    input_layout_email.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) { }
        })

        et_create_pass.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val valPass = et_create_pass.text.toString()
                Log.i("XXX", "pass: " + valPass)

                if(valPass.length < 8){
                    input_layout_create_pass.error = "Password must containt a minimum of 8 characters in length"
                    checkPass = false
                } else if(!valPass.matches("(.*[0-9].*)".toRegex())){
                    input_layout_create_pass.error = "Password must contain a minimum of 1 numeric character"
                    checkPass = false
                } else if(!valPass.matches("(.*[A-Z].*)".toRegex())){
                    input_layout_create_pass.error = "Password must contain a minimum of 1 upper case letter"
                    checkPass = false
                } else if(!valPass.matches("(.*[a-z].*)".toRegex())){
                    input_layout_create_pass.error = "Password must contain a minimum of 1 lower case letter"
                    checkPass = false
                } else if(valPass.matches("(.*\\s.*)".toRegex())){
                    input_layout_create_pass.error = "Password cannot contain whitespace"
                    checkPass = false
                } else {
                    checkPass = true
                    input_layout_create_pass.error = null
                    input_layout_create_pass.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) { }
        })

        et_confirm_pass.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val valConfirmPass: String = et_confirm_pass.text.toString()

                if(!valConfirmPass.equals(et_create_pass.text.toString())){
                    input_layout_confirm_pass.error = "Password mismatch"
                    checkConfirmPass = false
                } else {
                    checkConfirmPass = true
                    input_layout_confirm_pass.error = null
                    input_layout_confirm_pass.isErrorEnabled = false
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