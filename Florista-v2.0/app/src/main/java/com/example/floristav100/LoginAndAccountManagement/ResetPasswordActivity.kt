package com.example.floristav100.LoginAndAccountManagement

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.floristav100.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {


    private lateinit var ref: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        supportActionBar!!.hide()

        // Reference to Auth Firebase
        ref = FirebaseAuth.getInstance()

        // Manages button to send reset password email
        ConfirmResetPasswordButtonView.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {

        // Error management--------------------------
        if (emailToResetView.text.toString().isEmpty()) {
            emailToResetView.error = "Please Enter Email"
            emailToResetView.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailToResetView.text.toString()).matches()) {

            emailToResetView.error = "Please Enter a Valid Email"
            emailToResetView.requestFocus()
            return
        }
        // Error management--------------------------

        // Sends email to reset password (forgot)
        ref.sendPasswordResetEmail(emailToResetView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this, "Email Sent Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this, "No Account Associated with this Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}