package com.example.floristav100.DataModels.Utility


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

object ProfileAndImageManaging  {



    // Updates current Image Views
    fun updateView(imageView : ImageView, bitmapImage : Bitmap){

        imageView.setImageBitmap(bitmapImage)

    }

    fun updateView(imageView : ImageView, uriImage : Uri, context : Context){

        Glide.with(context)
            .load(uriImage)
            .into(imageView)

    }
    //-----------------------------


    // Stores image in Firebase storage and updates profile
    fun imageStorageAndProfileUpdate(bitmapImage : Bitmap, newUsername: String, referenceToUser : FirebaseAuth, context : Context){


        val baos = ByteArrayOutputStream()


        val storageRef = FirebaseStorage.getInstance().
            reference
            .child("pics/${referenceToUser.currentUser!!.uid}")

        bitmapImage!!.compress(Bitmap.CompressFormat.PNG,100, baos)

        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)


        upload.addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener{ urlTask ->
                    urlTask.result?.let{

                        Toast.makeText(context,"Image uploaded successfully", Toast.LENGTH_LONG).show()
                        updateProfile(referenceToUser,newUsername,it, context)



                    }
                }
            }else {
                uploadTask.exception!!.let{

                    Toast.makeText(context,"Image Not Uploaded!", Toast.LENGTH_LONG).show()
                }
            }
        }


    }



    // Updates Image and Username
    private fun updateProfile(ref : FirebaseAuth, newUsername : String, imageUriToSave : Uri, context: Context){


        val updates = UserProfileChangeRequest.Builder()
            .setDisplayName(newUsername)
            .setPhotoUri(imageUriToSave)
            .build()

        ref.currentUser!!.updateProfile(updates)
            ?.addOnCompleteListener{ task ->
                if (task.isSuccessful){

                    Toast.makeText(context, "Profile info Updated", Toast.LENGTH_LONG).show()


                } else {

                    Toast.makeText(context, task.exception?.message!!, Toast.LENGTH_LONG).show()
                }

            }

    }







}