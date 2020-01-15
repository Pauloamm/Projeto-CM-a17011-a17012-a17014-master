package com.example.floristav100

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_login.*

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var ref: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        ref = FirebaseAuth.getInstance()


        NewEmailButtonView.setOnClickListener {
            newEmailAccount()

        }

        NewPasswordButtonView.setOnClickListener{
            newPasswordAccount()
        }

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
            newEmailView.error = "Emails Must not Match"
            newEmailView.requestFocus()
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
        if (currentPasswordView.text.toString().isEmpty()) {
            currentPasswordView.error = "Please Enter the Current Password"
            currentPasswordView.requestFocus()
            return
        }
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
        if (currentPasswordView.text.toString() == newPasswordView.text.toString()) {
            confirmNewPasswordView.error = "Passwords Must not Match"
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