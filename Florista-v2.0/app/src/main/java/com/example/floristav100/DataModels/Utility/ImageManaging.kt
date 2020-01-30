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

object ImageManaging {

    fun updateView(imageView : ImageView, bitmapImage : Bitmap){

        imageView.setImageBitmap(bitmapImage)

    }

    fun updateView(imageView : ImageView, uriImage : Uri, context : Context){

        Glide.with(context)
            .load(uriImage)
            .into(imageView)

    }




     fun savesImageToFirebaseStorage(bitmapImage : Bitmap, referenceToUser : FirebaseAuth, context : Context){


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

                    }
                }
            }else {
                uploadTask.exception!!.let{

                    Toast.makeText(context,"Image Not Uploaded!", Toast.LENGTH_LONG).show()
                }
            }
        }


    }

    fun updateProfile(){



    }



}