package com.example.floristav100.Menus

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.floristav100.AccountSettingsAndInfo.AccountSettingsActivity
import com.example.floristav100.AccountSettingsAndInfo.UserIdFirebase
import com.example.floristav100.BouquetManagement.AvailableBouquetsActivity
import com.example.floristav100.BouquetManagement.CreateCustomBouquetActivity
import com.example.floristav100.DataModels.Bouquets
import com.example.floristav100.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.dialog_password_check.view.*

class MainMenuActivity : AppCompatActivity() {


    private lateinit var ref : FirebaseAuth
    private lateinit var refToUpdateImage : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_menu)
        supportActionBar!!.hide()

        ref = FirebaseAuth.getInstance()
        refToUpdateImage = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!! + "/otêpai")



       //updateImage()




        usernameTextView.text = ref.currentUser!!.displayName
        emailTextView.text = ref.currentUser!!.email

        editButtonView.setOnClickListener{

            dialog()

        }

        historyButtonView.setOnClickListener{
            var intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }


        availableBouquetsButtonView.setOnClickListener{
            var intent = Intent(this, AvailableBouquetsActivity::class.java)
            startActivity(intent)

        }

        createCustomBouquetButtonView.setOnClickListener{

            var intent = Intent(this, CreateCustomBouquetActivity::class.java)
            startActivity(intent)
        }

        transactionHistoryButtonView.setOnClickListener{


            var intent = Intent (this, HistoryTransactionActivity::class.java)
            startActivity(intent)

        }


        pauloInstaButtonView.setOnClickListener{
            var url : String = "https://www.instagram.com/pauloamm2000/"
            var intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(url))
            startActivity(intent)
        }

        luisInstaButtonView.setOnClickListener{

            var url : String = "https://www.instagram.com/luismsilva99/"
            var intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(url))
            startActivity(intent)


        }


    }



    fun dialog(){
        //--------------------------------
        var dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
        val dialogView = layoutInflater.inflate(R.layout.dialog_password_check,null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)


        dialog.show()

        dialogView.DialogPasswordButtonView.setOnClickListener{

            if (dialogView.dialogPasswordView.text.toString().isEmpty()){
                dialogView.dialogPasswordView.error = "Please Enter Password"
                dialogView.dialogPasswordView.requestFocus()

            }
            else
            ref.signInWithEmailAndPassword(ref.currentUser!!.email.toString(), dialogView.dialogPasswordView.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivityForResult(Intent(this, AccountSettingsActivity::class.java), 1)
                    }
                    else
                    {

                        dialogView.dialogPasswordView.error = "Wrong Password"
                        dialogView.dialogPasswordView.requestFocus()
                    }
                }


        }
        //--------------------------------
    }


    private fun updateImage(){

       mainAvatarImageView.setImageURI(ref.currentUser!!.photoUrl)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 1)
        {
            updateImage()

        }
    }
}