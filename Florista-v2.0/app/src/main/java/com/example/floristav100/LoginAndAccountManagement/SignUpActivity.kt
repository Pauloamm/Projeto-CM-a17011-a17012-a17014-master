package com.example.floristav100.LoginAndAccountManagement

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.floristav100.AccountSettingsAndInfo.UserIdFirebase
import com.example.floristav100.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_login.emailView
import kotlinx.android.synthetic.main.activity_login.passwordView
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import java.io.ByteArrayOutputStream


//https://firebase.google.com/docs/auth/android/password-auth


class SignUpActivity : AppCompatActivity() {

    private lateinit var ref : FirebaseAuth
    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        ref = FirebaseAuth.getInstance()
        supportActionBar!!.hide()


        CreateAccountButtonView.setOnClickListener{
            newAccountCreation()

        }





        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(view)
        newAccountImageView.setOnClickListener{
            bottomSheetDialog.show()
        }
        view.cameraId.setOnClickListener{
            pickImageFromCamera()
        }
        view.galleryId.setOnClickListener{
            pickImageFromGallery()
        }


    }

    private fun saveImage(){

        var photo = when {
            ::imageUri.isInitialized -> imageUri
            ref.currentUser!!.photoUrl == null -> Uri.parse( "https://picsum.photos/200")
            else -> ref.currentUser!!.photoUrl
        }

        val username = usernameView.text.toString()


        val updates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .setPhotoUri(photo)
            .build()

        ref.currentUser!!.updateProfile(updates)
            ?.addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    Toast.makeText(this,"funciona!", Toast.LENGTH_LONG).show()



                    var intent = Intent()
                    setResult(Activity.RESULT_OK, intent)

                } else {
                    Toast.makeText(this, task.exception?.message!!, Toast.LENGTH_LONG).show()
                }

            }
    }


    private fun pickImageFromGallery(){

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 2)
    }

    private fun pickImageFromCamera(){

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(this.packageManager!!)?.also {
                startActivityForResult(pictureIntent, 1)
            }
        }
    }


    private fun newAccountCreation(){

        if (usernameView.text.toString().isEmpty()){
            usernameView.error = "Username Required"
            usernameView.requestFocus()
            return
        }

        if (emailView.text.toString().isEmpty()){
            emailView.error = "Please Enter Email"
            emailView.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailView.text.toString()).matches()){

            emailView.error = "Please Enter a Valid Email"
            emailView.requestFocus()
            return
        }

        if (passwordView.text.toString().isEmpty()){
            passwordView.error = "Please Enter Password"
            passwordView.requestFocus()
            return
        }

        if (confirmPassowrdView.text.toString() != passwordView.text.toString()){
            confirmPassowrdView.error = "Passwords do not Match"
            confirmPassowrdView.requestFocus()
            return
        }


        ref.createUserWithEmailAndPassword(emailView.text.toString(), passwordView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = ref.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if(task.isSuccessful){

                                saveImage()

                                Toast.makeText(this, "Account Created Successfully",
                                    Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Password too Short. Try Again!",
                        Toast.LENGTH_SHORT).show()
                }

            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK)
        {

            val imageBitmap : Bitmap = data?.extras?.get("data") as Bitmap

            pocaralhoPaulo(imageBitmap)
        }
        if(requestCode == 2 && resultCode == Activity.RESULT_OK){

            val inputStream = contentResolver.openInputStream(data!!.data!!)
            val imageBitmap = BitmapFactory.decodeStream(inputStream)

            pocaralhoPaulo(imageBitmap)
        }
    }




    private fun pocaralhoPaulo(bitmap : Bitmap){

        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance().
            reference
            .child("pics/${UserIdFirebase.UID}")

        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)


        upload.addOnCompleteListener() { uploadTask ->
            if (uploadTask.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener{ urlTask ->
                    urlTask.result?.let{
                        imageUri = it
                        Toast.makeText(this,"talvez funcione!", Toast.LENGTH_LONG).show()

                        newAccountImageView.setImageBitmap(bitmap)


                    }
                }
            }else {
                uploadTask.exception?.let{
                    Toast.makeText(this,"talvez nao funcione!", Toast.LENGTH_LONG).show()
                }
            }
        }


    }

}