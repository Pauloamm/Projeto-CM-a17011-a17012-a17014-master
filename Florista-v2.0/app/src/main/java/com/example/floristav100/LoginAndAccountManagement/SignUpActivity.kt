package com.example.floristav100.LoginAndAccountManagement

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.floristav100.AccountSettingsAndInfo.UserIdFirebase
import com.example.floristav100.DataModels.Bouquets
import com.example.floristav100.DataModels.FlowerHierarchy.Flowers
import com.example.floristav100.DataModels.FlowerHierarchy.Orchid
import com.example.floristav100.DataModels.FlowerHierarchy.Rose
import com.example.floristav100.DataModels.FlowerHierarchy.Sunflower
import com.example.floristav100.DataModels.Utility.ProfileAndImageManaging
import com.example.floristav100.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.emailView
import kotlinx.android.synthetic.main.activity_login.passwordView
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import java.util.ArrayList


// Documentation used as base for code
//https://firebase.google.com/docs/auth/android/password-auth


class SignUpActivity : AppCompatActivity() {

    private lateinit var refToAcc : FirebaseAuth
    private lateinit var refToDatabase : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar!!.hide()

        // Gets Firebase Auth Instance
        refToAcc = FirebaseAuth.getInstance()


        // Manages new account creation button click
        CreateAccountButtonView.setOnClickListener{

            // Method responsible for account creation
            newAccountCreation()

        }


        // Manages Image click
        newAccountImageView.setOnClickListener{

            // Method used for setting up confirmPasswordDialog for type of image choosing(camera or gallery)
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
        refToAcc.createUserWithEmailAndPassword(emailView.text.toString(), passwordView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // If account created successfully send verification email
                    refToAcc.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if(task.isSuccessful){

                                // Saves selected image for profile picture and Updates Profile

                                ProfileAndImageManaging.imageStorageAndProfileUpdate(
                                    newAccountImageView.drawable.toBitmap(),
                                    usernameView.text.toString(),
                                    refToAcc,
                                    this)

                                predefinedBouquetsCreation()




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

        // Sets up image type choosing View
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
            ProfileAndImageManaging.updateView(newAccountImageView, bitmapImage)

        }

        // If photo is selected from gallery
         if(requestCode == 2 && resultCode == Activity.RESULT_OK){


             // Converts Uri to bitmap
            val inputStream = contentResolver.openInputStream(data!!.data!!)
             var bitmapImage = BitmapFactory.decodeStream(inputStream)

             // Updates ImageView
             ProfileAndImageManaging.updateView(newAccountImageView, bitmapImage)

        }
    }


    // Creates 3 predefined Bouquets that are stored in Firebase
    private fun predefinedBouquetsCreation() {

        refToDatabase =  FirebaseDatabase.getInstance().getReference(refToAcc.currentUser!!.uid +"/Available Bouquets")

        var flowersListForPredefinedBouquet: MutableList<Flowers> = ArrayList<Flowers>()

        //First Bouquet- 100 sunflowers

        for(x in 0..99  ){


            flowersListForPredefinedBouquet.add(Sunflower())

        }

        // gets id to store
        var idToSave = refToDatabase.push().key

        // Created predefined bouquet and stores it
        var bouquetToSave = Bouquets("Shooting Star",flowersListForPredefinedBouquet,R.drawable.shootingstar)
        bouquetToSave.id = "PredefinedBouquet_1"

        refToDatabase.child(idToSave!!).
            setValue(bouquetToSave)


        // Clears list for reuse
        flowersListForPredefinedBouquet.clear()

        //SecondBouquet- 100 Roses

        for(x in 0..99  ){


            flowersListForPredefinedBouquet.add(Rose())

        }

        idToSave = refToDatabase.push().key

        bouquetToSave = Bouquets("Bloody Mary",flowersListForPredefinedBouquet,R.drawable.bloodymary)
        bouquetToSave.id = "PredefinedBouquet_2"

        refToDatabase.child(idToSave!!).
            setValue(bouquetToSave)


        flowersListForPredefinedBouquet.clear()


        //Third Bouquet- 100 Orchids

        for(x in 0..99  ){


            flowersListForPredefinedBouquet.add(Orchid())

        }

        idToSave = refToDatabase.push().key

        bouquetToSave = Bouquets("Venus",flowersListForPredefinedBouquet,R.drawable.venus)
        bouquetToSave.id = "PredefinedBouquet_3"

        refToDatabase.child(idToSave!!).
            setValue(bouquetToSave)
        flowersListForPredefinedBouquet.clear()


    }







}