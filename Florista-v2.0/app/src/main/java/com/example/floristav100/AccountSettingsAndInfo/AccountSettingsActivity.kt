package com.example.floristav100.AccountSettingsAndInfo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.floristav100.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import java.io.ByteArrayOutputStream
import java.net.URL

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var ref: FirebaseAuth

    private lateinit var  refForDelete : DatabaseReference
    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        supportActionBar!!.hide()

        ref = FirebaseAuth.getInstance()

        refForDelete = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!!)




        // DECODE URI TO BITMAP
        //val inputStream = contentResolver.openInputStream(ref.currentUser!!.photoUrl!!)
        //val bitmap = BitmapFactory.decodeStream(inputStream)
        //avatarImageView.setImageBitmap(bitmap)
        // DECODE URI TO BITMAP

        // PARA POR A PUTA DA PHOTOURL NA IMAGEM
        Glide.with(this)
            .load(ref.currentUser!!.photoUrl)
            .into(avatarImageView)


        usernameTextViewSettings.text = Editable.Factory.getInstance().newEditable(ref.currentUser!!.displayName)
        emailTextViewSettings.text = ref.currentUser!!.email



        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(view)
        avatarImageView.setOnClickListener{
            bottomSheetDialog.show()
        }
        view.cameraId.setOnClickListener{
            pickImageFromCamera()
        }
        view.galleryId.setOnClickListener{
            pickImageFromGallery()
        }






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

            var photo = when {
                ::imageUri.isInitialized -> imageUri
                ref.currentUser!!.photoUrl == null -> Uri.parse( "https://picsum.photos/200")
                else -> ref.currentUser!!.photoUrl
            }

            val username = usernameTextViewSettings.text.toString()

            if (username.isEmpty()){
                usernameTextViewSettings.error = "Name Required"
                usernameTextViewSettings.requestFocus()
                return@setOnClickListener
            }

            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photo)
                .build()

            ref.currentUser!!.updateProfile(updates)
                ?.addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        Toast.makeText(this,"funciona!", Toast.LENGTH_LONG).show()

                        var intent = Intent()
                        intent.putExtra("UpdateInformation", "UpdateProfile")
                        setResult(Activity.RESULT_OK, intent)

                    } else {
                        Toast.makeText(this, task.exception?.message!!, Toast.LENGTH_LONG).show()
                    }

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

                        avatarImageView.setImageBitmap(bitmap)


                    }
                }
            }else {
                uploadTask.exception?.let{
                    Toast.makeText(this,"talvez nao funcione!", Toast.LENGTH_LONG).show()
                }
            }
        }


    }





    fun deleteAccount(){
        // Removes the Account
        ref.currentUser!!.delete()

        // Removes the node from the Firebase of the selected account
        refForDelete.removeValue()

        var adw = FirebaseStorage.getInstance().
            reference
            .child("pics/${UserIdFirebase.UID}")
        adw.delete()

        var intent = Intent()
        intent.putExtra("UpdateInformation","DeleteAccount")
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