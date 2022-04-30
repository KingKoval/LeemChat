package com.kingkoval.leemchat.activity

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Pair
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kingkoval.leemchat.R
import com.kingkoval.leemchat.model.User
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var checkName: Boolean = false
    private var checkEmail: Boolean = false
    private var checkPass: Boolean = false
    private var checkConfirmPass: Boolean = false
    private var checkUserPhoto: Boolean = false

    private lateinit var dbRef: DatabaseReference

    lateinit var userPhotoUri: Uri
    var userPhotoUrl: String = "null"

    val DEFAULT_USER_PHOTO_URL = "https://firebasestorage.googleapis.com/v0/b/leemchat-2f216.appspot.com/o/user_photo%2Fdefault-user-image.png?alt=media&token=f8ae53fc-7108-4246-bcbd-dc7d95a07718"

    val resultLauncherUploadImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result:ActivityResult ->
        if(result.resultCode == RESULT_OK && result.data != null){
            checkUserPhoto = true
            Log.i("IMAGE!!!", "Image was selected")
            userPhotoUri = result.data!!.data!!


            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, userPhotoUri))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, userPhotoUri)
            }

            iv_user_photo.background = BitmapDrawable(bitmap)
        } else{
            checkUserPhoto = false
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
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    Pair<View, String>(tv_sign_in, "sign_in")
                )

                startActivity(Intent(
                    this@RegistrationActivity, LoginActivity::class.java),
                    options.toBundle()
                )
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        validateEnterDate()

        btn_registration.setOnClickListener {
            if (!checkName
                || !checkEmail
                || !checkPass
                || !checkConfirmPass
            ) {
                Toast.makeText(this, getString(R.string.sign_up_fail), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                createUser()
            }
        }



    }

    fun createUser(){
        auth.createUserWithEmailAndPassword(et_email.text.toString(), et_create_pass.text.toString()).
                addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        if(checkUserPhoto == true) {
                            uploadImageToFirebaseStorage()
                        } else {
                            addUserToDatabase(
                                DEFAULT_USER_PHOTO_URL, et_name.text.toString(), et_email.text.toString(),
                                auth.currentUser?.uid.toString()
                            )
                        }
                        Toast.makeText(this, getString(R.string.sign_up_done), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    } else{
                        Log.w("FIRERROR", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, getString(R.string.auth_fail) + ": " + {task.exception?.message},
                            Toast.LENGTH_LONG).show()
                    }
                }

    }

    fun addUserToDatabase(profileImage: String, name: String, email: String, uid: String){
        dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("users").child(uid).setValue(User(profileImage, name, email, uid)).addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                Log.i("BOHDAN","SUCCESS" + task.exception.toString())
            } else{
                Log.i("BOHDAN", "FAILED" + task.exception.toString())
            }
        }.addOnFailureListener { it ->
            Log.i("BOHDAN", it.toString())
        }

    }

    fun uploadImageToFirebaseStorage(){
        val fileName = UUID.randomUUID().toString()

        val ref = FirebaseStorage.getInstance().getReference("/user_photo/$fileName")

        ref.putFile(userPhotoUri).addOnSuccessListener {
            Log.i("USER", "image upload to firebase storage")

            ref.downloadUrl.addOnSuccessListener { task ->
                val userPhotoUrl1 = task.toString()

                userPhotoUrl = userPhotoUrl1


                addUserToDatabase(
                    userPhotoUrl, et_name.text.toString(), et_email.text.toString(),
                    auth.currentUser?.uid.toString()
                )

            }
        }.addOnFailureListener{
                it ->
            Log.i("USER123", it.toString())
            addUserToDatabase(DEFAULT_USER_PHOTO_URL, et_name.text.toString(), et_email.text.toString(),
                auth.currentUser?.uid.toString()
            )
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
                    input_layout_name.error = getString(R.string.name_empty)
                    checkName = false
                } else if(valName.length < 2){
                    input_layout_name.error = getString(R.string.name_small)
                    checkName = false
                } else if(valName.length > 25){
                    input_layout_name.error = getString(R.string.name_long)
                    checkName = false
                } else{
                    checkName = true
                    input_layout_name.error = null
                    input_layout_name.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                et_name.setOnFocusChangeListener { view, b ->
                    if(!b){
                        if(!checkName){
                            input_layout_name.isErrorEnabled = false
                            input_layout_name.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_account_circle_24_red)
                        }
                    } else{
                        input_layout_name.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_account_circle_24)
                    }
                }
            }
        })

        et_email.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val valEmail: String = et_email.text.toString()
                Log.i("XXX", "email: " + valEmail)

                if(valEmail.length == 0){
                    input_layout_email.error = getString(R.string.email_empty)
                    checkEmail = false
                } else if(!EMAIL_ADDRESS.matcher(valEmail).matches()){
                    input_layout_email.error = getString(R.string.email_invalid)
                    checkEmail = false
                } else{
                    checkEmail = true
                    input_layout_email.error = null
                    input_layout_email.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                et_email.setOnFocusChangeListener { view, b ->
                    if(!b){
                        if(!checkEmail){
                            input_layout_email.isErrorEnabled = false
                            input_layout_email.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_email_24_red)
                        }
                    } else{
                        input_layout_email.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_email_24)
                    }
                }
            }
        })

        et_create_pass.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val valPass = et_create_pass.text.toString()
                Log.i("XXX", "pass: " + valPass)



                if(valPass.length < 8){
                    input_layout_create_pass.error = getString(R.string.pass_lenght_check)
                    checkPass = false
                } else if(!valPass.matches("(.*[0-9].*)".toRegex())){
                    input_layout_create_pass.error = getString(R.string.pass_num_check)
                    checkPass = false
                } else if(!valPass.matches("(.*[A-Z].*)".toRegex())){
                    input_layout_create_pass.error = getString(R.string.pass_upper_check)
                    checkPass = false
                } else if(!valPass.matches("(.*[a-z].*)".toRegex())){
                    input_layout_create_pass.error = getString(R.string.pass_lower_check)
                    checkPass = false
                } else if(valPass.matches("(.*\\s.*)".toRegex())){
                    input_layout_create_pass.error = getString(R.string.pass_wspace_check)
                    checkPass = false
                } else {
                    checkPass = true
                    input_layout_create_pass.error = null
                    input_layout_create_pass.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                et_create_pass.setOnFocusChangeListener { view, b ->
                    if(!b){
                        if(!checkPass){
                            input_layout_create_pass.isErrorEnabled = false
                            input_layout_create_pass.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_lock_24_red)
                        }
                    } else{
                        input_layout_create_pass.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_lock_24)
                    }
                }
            }
        })

        et_confirm_pass.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val valConfirmPass: String = et_confirm_pass.text.toString()

                if(!valConfirmPass.equals(et_create_pass.text.toString())){
                    input_layout_confirm_pass.error = getString(R.string.pass_conf_check)
                    checkConfirmPass = false
                } else {
                    checkConfirmPass = true
                    input_layout_confirm_pass.error = null
                    input_layout_confirm_pass.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                et_confirm_pass.setOnFocusChangeListener { view, b ->
                    if(!b){
                        if(!checkConfirmPass){
                            input_layout_confirm_pass.isErrorEnabled = false
                            input_layout_confirm_pass.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_lock_24_red)
                        }
                    } else{
                        input_layout_confirm_pass.startIconDrawable = resources.getDrawable(R.drawable.ic_baseline_lock_24)
                    }
                }
            }

        })

    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//    }

}