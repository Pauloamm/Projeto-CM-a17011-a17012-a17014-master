package com.example.floristav100.Menus

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.floristav100.AccountSettingsAndInfo.AccountSettingsActivity
import com.example.floristav100.BouquetManagement.AvailableBouquetsActivity
import com.example.floristav100.BouquetManagement.CreateCustomBouquetActivity
import com.example.floristav100.DataModels.Utility.ProfileAndImageManaging
import com.example.floristav100.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.dialog_password_check.view.*

class MainMenuActivity : AppCompatActivity() {


    private lateinit var ref : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_menu)
        supportActionBar!!.hide()

        // Gets instance of current player for data display
        ref = FirebaseAuth.getInstance()

        // Sets up initial view with data from profile
        initialViewSetup()

        // Manages Edit profile button click
        editButtonView.setOnClickListener{

            confirmPasswordDialog()
        }

        // Manages the 4 main buttons for different options
        mainMenuButtonsManager()

        // Manages instagram clicks
        instagramButtonsManager()
    }

    private fun initialViewSetup(){

        // Updates avatar ImageView
        ProfileAndImageManaging.updateView(mainAvatarImageView,ref.currentUser!!.photoUrl!!,this)

        // Updates TextViews with profile info
        usernameTextView.text = ref.currentUser!!.displayName
        emailTextView.text = ref.currentUser!!.email
    }


    private fun instagramButtonsManager(){
        // According to which insta selected it opens up for web or app view

        pauloInstaButtonView.setOnClickListener{
            var url : String = "https://www.instagram.com/pauloamm2000/"
            var intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        luisInstaButtonView.setOnClickListener{
            var url : String = "https://www.instagram.com/luismsilva99/"
            var intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    private fun mainMenuButtonsManager(){

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
    }

    private fun confirmPasswordDialog(){

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
                    else {
                        dialogView.dialogPasswordView.error = "Wrong Password"
                        dialogView.dialogPasswordView.requestFocus()
                    }
                }
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Manages result from AccountSettingsActivity(updating views)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK  )
        {
            var updateInformationType : String = data?.getStringExtra("UpdateInformation")!!

            when (updateInformationType) {
                "UpdateProfile" ->{

                    // Updates Image View according to the new photo stored in profile
                    ProfileAndImageManaging.updateView(mainAvatarImageView, ref.currentUser!!.photoUrl!!, this)

                    // Updates main username view according to new username saved in profile in settings activity
                    usernameTextView.text = ref.currentUser!!.displayName
                }

                // Updates Email TextView according to new email stored in account
                "UpdateEmail" -> emailTextView.text = ref.currentUser!!.email


                // After account deletion send you back to login screen
                "DeleteAccount" -> finish()
            }

        }
    }
}