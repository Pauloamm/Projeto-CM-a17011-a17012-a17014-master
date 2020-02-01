package com.example.floristav100.AccountSettingsAndInfo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.floristav100.DataModels.Utility.ProfileAndImageManaging
import com.example.floristav100.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var refAcc: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        supportActionBar!!.hide()

        // Defaults result to cancelled in case the user decides to leave the activity without choosing an action
        setResult(Activity.RESULT_CANCELED)

        // Reference to Firebase Auth current user
        refAcc = FirebaseAuth.getInstance()

        // Sets up initial view (imageView and textViews)
        viewSetup()

        // Manages clicks of buttons
        buttonManager()
    }


    private fun viewSetup(){

        // Updates avatar ImageView
        ProfileAndImageManaging.updateView(avatarImageView,refAcc.currentUser!!.photoUrl!!, this)

        // Updates TextViews/EditTextViews with profile info
        usernameTextViewSettings.text = Editable.Factory.getInstance().newEditable(refAcc.currentUser!!.displayName)
        emailTextViewSettings.text = refAcc.currentUser!!.email
    }

    private fun buttonManager(){

        // Manages image clicking for changing profile picture
        avatarImageView.setOnClickListener{
            methodForImageChoosing()
        }

        // Manages Update profile button click
        updateProfileButtonManager()

        // Manages new email button click
        newEmailButtonView.setOnClickListener {
            newEmailAccount()
        }

        // Manages new password button click
        newPasswordButtonView.setOnClickListener{
            newPasswordAccount()
        }

        // Manages delete account button click
        deleteAccountButtonView.setOnClickListener{
            deleteAccount()
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

            val imageBitmap : Bitmap = data?.extras?.get("data") as Bitmap

            ProfileAndImageManaging.updateView(avatarImageView, imageBitmap)
        }

        // If photo is selected from gallery
        if(requestCode == 2 && resultCode == Activity.RESULT_OK){

            val inputStream = contentResolver.openInputStream(data!!.data!!)
            val imageBitmap = BitmapFactory.decodeStream(inputStream)

            ProfileAndImageManaging.updateView(avatarImageView, imageBitmap)

        }
    }



    private fun updateProfileButtonManager(){

        profileUpdateButton.setOnClickListener{

            // Gets Username written in edit text for update profile displayName
            var newUsername = usernameTextViewSettings.text.toString()

            if (newUsername.isEmpty()){
                usernameTextViewSettings.error = "Name Required"
                usernameTextViewSettings.requestFocus()
                return@setOnClickListener
            }

            // Only updates when written username is not empty
            var intent = Intent()
            intent.putExtra("UpdateInformation","UpdateProfile")

            setResult(Activity.RESULT_OK, intent)

           // Updates profile and finishes this activity once is all done
            ProfileAndImageManaging.imageStorageAndProfileUpdate(avatarImageView.drawable.toBitmap(),newUsername,refAcc, this,{

                finish()

            })





        }
    }


    private fun newEmailAccount() {

        // Error management-----------------------------
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

        if (currentEmailView.text.toString() != refAcc.currentUser!!.email){
            currentEmailView.error = "This Account is not Associated with this Email"
            currentEmailView.requestFocus()
            return
        }
        // Error management-----------------------------


        // Updates email of current user
        refAcc.currentUser!!.updateEmail(newEmailView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sends email checking for the new email
                    refAcc.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            Toast.makeText(
                                this, "Email Changed Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Send intent with type of action chosen and sets RESULT_OK
                            var intent = Intent()
                            intent.putExtra("UpdateInformation", "UpdateEmail")
                            setResult(Activity.RESULT_OK, intent)

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

    private fun newPasswordAccount() {

        // Error management-----------------------------
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
        // Error management-----------------------------

        // Updates password of current user
        refAcc.currentUser!!.updatePassword(newPasswordView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

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

    private fun deleteAccount(){

        // Removes the node from the Firebase of the selected account
        FirebaseDatabase.getInstance().getReference(refAcc.currentUser!!.uid).removeValue()

        // Removes profile image of current account
        FirebaseStorage.getInstance().reference.child("pics/${refAcc.currentUser!!.uid}").delete()

        // Removes the Account
        refAcc.currentUser!!.delete()

        // Send intent with type of action chosen and sets RESULT_OK
        var intent = Intent()
        intent.putExtra("UpdateInformation","DeleteAccount")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}