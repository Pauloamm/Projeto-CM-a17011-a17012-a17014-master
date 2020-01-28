package com.example.floristav100.AccountSettingsAndInfo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.floristav100.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_main_menu.*

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var ref: FirebaseAuth

    private lateinit var  refForDelete : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        supportActionBar!!.hide()

        ref = FirebaseAuth.getInstance()

        refForDelete = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!!)



        avatarImageView.setImageURI(ref.currentUser!!.photoUrl)
        

        usernameTextViewSettings.text = ref.currentUser!!.displayName
        emailTextViewSettings.text = ref.currentUser!!.email


        NewEmailButtonView.setOnClickListener {
            newEmailAccount()

        }

        NewPasswordButtonView.setOnClickListener{
            newPasswordAccount()
        }

        DeleteAccountButtonView.setOnClickListener{
            deleteAccount()
        }

        avatarImageButton.setOnClickListener{
            pickImageFromGallery()
        }

    }


    fun profileImageSettings(){


       // var refToUploadImage = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!!)

       // var image : Int = 0

        //refToUploadImage.setValue(image)




    }


    private fun pickImageFromGallery(){

       val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 3)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 3 && resultCode == Activity.RESULT_OK)
        {

           // var imageViewBouquet = findViewById<ImageView>(R.id.avatarImageView) as ImageView
           // imageViewBouquet.setImageURI(data!!.data)


           // var refToUploadImage = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!! + "/otêpai")
           // refToUploadImage.setValue()

           var intent = Intent()
           setResult(Activity.RESULT_OK, intent)


            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("FIlho da puta")
                .setPhotoUri((data!!.data))
                .build()

            ref.currentUser!!.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(baseContext, "FUNCIONA CARALHO", Toast.LENGTH_SHORT).show()

                        var imageViewEdit = findViewById<ImageView>(R.id.avatarImageView) as ImageView
                        imageViewEdit.setImageURI(ref.currentUser!!.photoUrl)
                    }

                }



        }
    }







    fun deleteAccount(){
        // Removes the Account
        ref.currentUser!!.delete()

        // Removes the node from the Firebase of the selected account
        refForDelete.removeValue()

        var intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    fun newEmailAccount() {
        if (currentEmailView.text.toString().isEmpty()) {
            currentEmailView.error = "Please Enter the Current Email"
            currentEmailView.requestFocus()
            return
        }
        if (newEmailView.text.toString().isEmpty()) {
            newEmailView.error = "Please Enter the New Email"
            newEmailView.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(currentEmailView.text.toString()).matches()) {

            currentEmailView.error = "Please Enter a Valid Email"
            currentEmailView.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmailView.text.toString()).matches()) {

            newEmailView.error = "Please Enter a Valid Email"
            newEmailView.requestFocus()
            return
        }

        if (newEmailView.text.toString() == currentEmailView.text.toString()) {
            currentEmailView.error = "Emails Must not Match"
            currentEmailView.requestFocus()
            return
        }

        if (currentEmailView.text.toString() != ref.currentUser!!.email){
            currentEmailView.error = "This Account is not Associated with this Email"
            currentEmailView.requestFocus()
            return
        }


        ref.currentUser!!.updateEmail(newEmailView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //
                    ref.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            Toast.makeText(
                                this, "Email Changed Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }

                } else {
                    // If fails, display a message to the user.
                    Toast.makeText(
                        this, "An Error Occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    fun newPasswordAccount() {

        if (newPasswordView.text.toString().isEmpty()) {
            newPasswordView.error = "Please Enter the New Password"
            newPasswordView.requestFocus()
            return
        }
        if (confirmNewPasswordView.text.toString().isEmpty()) {
            confirmNewPasswordView.error = "Please Confirm the New Password"
            confirmNewPasswordView.requestFocus()
            return
        }

        if (newPasswordView.text.toString() != confirmNewPasswordView.text.toString()) {
            confirmNewPasswordView.error = "Passwords do not Match"
            confirmNewPasswordView.requestFocus()
            return
        }



        ref.currentUser!!.updatePassword(newPasswordView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //

                    Toast.makeText(
                        this, "Password Changed Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()

                } else {
                    // If fails, display a message to the user.
                    Toast.makeText(
                        this, "An Error Occurred.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


    }
}