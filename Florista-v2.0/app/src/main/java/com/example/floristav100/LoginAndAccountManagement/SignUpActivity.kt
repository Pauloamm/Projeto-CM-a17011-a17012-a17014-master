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
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.example.floristav100.AccountSettingsAndInfo.UserIdFirebase
import com.example.floristav100.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.emailView
import kotlinx.android.synthetic.main.activity_login.passwordView
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import java.io.ByteArrayOutputStream


// Documentation used as base for code
//https://firebase.google.com/docs/auth/android/password-auth


class SignUpActivity : AppCompatActivity() {

    private lateinit var ref : FirebaseAuth
    private  var imageUri : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar!!.hide()

        // Gets Firebase Auth Instance
        ref = FirebaseAuth.getInstance()


        // Mnages new accoutn creation button click
        CreateAccountButtonView.setOnClickListener{

            // Method responsible for account creation
            newAccountCreation()

        }


        // Manages Image click
        newAccountImageView.setOnClickListener{

            // Method used for setting up dialog for type of image choosing(camera or gallery)
            methodForImageChoosing()
        }


    }



    private fun newAccountCreation(){

        // Errors Management---------------------------------------------------------//

        // Username Error
        if (usernameView.text.toString().isEmpty()){
            usernameView.error = "Username Required"
            usernameView.requestFocus()
            return
        }

        // Email Erros
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

        // Password Errors
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
        //---------------------------------------------------------------------------//


        // Creates user account in Firebase
        ref.createUserWithEmailAndPassword(emailView.text.toString(), passwordView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // If account created successfully send verification email
                    ref.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if(task.isSuccessful){

                                // Saves selected image for profile picture(if not selected a random one is used)
                                savesImageToFirebaseStorage()



                                Toast.makeText(this, "Account Created Successfully",
                                    Toast.LENGTH_SHORT).show()

                                // Goes back to login screen
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

    private fun methodForImageChoosing(){

        // Sets up dialog View
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        // Manages type click
        view.cameraId.setOnClickListener{
            pickImageFromCamera()
        }
        view.galleryId.setOnClickListener{
            pickImageFromGallery()
        }


    }

    private fun pickImageFromCamera(){

        // Intent used for taking picture from camera to be used
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(this.packageManager!!)?.also {
                startActivityForResult(pictureIntent, 1)
            }
        }
    }

    private fun pickImageFromGallery(){

        // Intent used for picking gallery photo
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If photo is taken from camera successfully
        if (requestCode == 1 && resultCode == Activity.RESULT_OK)
        {

            // Gets bitmap image from camera
            var bitmapImage  = data?.extras?.get("data") as Bitmap

            // Updates ImageView
            newAccountImageView.setImageBitmap(bitmapImage)

        }

        // If photo is selected from gallery
         if(requestCode == 2 && resultCode == Activity.RESULT_OK){


             // Converts Uri to bitmap
            val inputStream = contentResolver.openInputStream(data!!.data!!)
             var bitmapImage = BitmapFactory.decodeStream(inputStream)

             // Updates ImageView
             newAccountImageView.setImageBitmap(bitmapImage)

        }
    }




    private fun savesImageToFirebaseStorage(){




        val baos = ByteArrayOutputStream()


        val storageRef = FirebaseStorage.getInstance().
            reference
            .child("pics/${ref.currentUser!!.uid}")

        newAccountImageView.drawable.toBitmap()!!.compress(Bitmap.CompressFormat.JPEG,100, baos)

        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)


        upload.addOnCompleteListener() { uploadTask ->
            if (uploadTask.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener{ urlTask ->
                    urlTask.result?.let{

                        imageUri = it

                        Toast.makeText(this,"Image uploaded successfully", Toast.LENGTH_LONG).show()

                        saveImage()


                    }
                }
            }else {
                uploadTask.exception?.let{
                    Toast.makeText(this,"Image Not Uploaded!", Toast.LENGTH_LONG).show()
                }
            }
        }


    }

    private fun saveImage(){

        val username = usernameView.text.toString()

        var imageToSave = when{

            imageUri == null -> Uri.parse("https://picsum.photos/200")
            imageUri != null -> imageUri
            else -> Uri.parse("https://picsum.photos/200")

        }

        val updates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .setPhotoUri(imageToSave)
            .build()

        ref.currentUser!!.updateProfile(updates)
            ?.addOnCompleteListener{ task ->
                if (task.isSuccessful){

                    Toast.makeText(this, "Profile info saved", Toast.LENGTH_LONG).show()


                } else {

                    Toast.makeText(this, task.exception?.message!!, Toast.LENGTH_LONG).show()
                }

            }
    }

}