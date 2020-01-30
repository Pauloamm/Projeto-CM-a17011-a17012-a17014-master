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
import com.bumptech.glide.Glide
import com.example.floristav100.AccountSettingsAndInfo.AccountSettingsActivity
import com.example.floristav100.AccountSettingsAndInfo.UserIdFirebase
import com.example.floristav100.BouquetManagement.AvailableBouquetsActivity
import com.example.floristav100.BouquetManagement.CreateCustomBouquetActivity
import com.example.floristav100.DataModels.Bouquets
import com.example.floristav100.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.dialog_password_check.view.*

class MainMenuActivity : AppCompatActivity() {


    private lateinit var ref : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_menu)
        supportActionBar!!.hide()


        ref = FirebaseAuth.getInstance()



        Glide.with(this)
            .load(ref.currentUser!!.photoUrl)
            .into(mainAvatarImageView)

        usernameTextView.text = ref.currentUser!!.displayName
        emailTextView.text = ref.currentUser!!.email




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




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 1 && resultCode == Activity.RESULT_OK  )
        {
            var UpdateInformation : String = data?.getStringExtra("UpdateInformation")!!

            when (UpdateInformation) {
                "UpdateProfile" ->{
                    Glide.with(this)
                        .load(ref.currentUser!!.photoUrl)
                        .into(mainAvatarImageView)

                    usernameTextView.text = ref.currentUser!!.displayName
                }
                // Updates Email TextView
                "UpdateEmail" -> emailTextView.text = ref.currentUser!!.email
                // Deletes Current Account
                "DeleteAccount" -> finish()
            }

        }
    }
}