package com.example.floristav100.LoginAndAccountManagement

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.floristav100.AccountSettingsAndInfo.UserIdFirebase
import com.example.floristav100.Menus.MainMenuActivity
import com.example.floristav100.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    // Firebase Authentication reference
    private lateinit var ref : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        // Gets Firebase Authenticatiopn for login methods
        ref = FirebaseAuth.getInstance()


        // Manages button click
        buttonManager()
    }

    private fun buttonManager(){

        // Manages Sign Up button click
        SignUpButtonView.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Manages Reset Password button click
        ResetPasswordButtonView.setOnClickListener{
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        // Manages Login button click
        LoginButtonView.setOnClickListener{
            login()
        }

    }

    private fun login() {

        // Errors management-------------------------------//
        if (emailView.text.toString().isEmpty()){
            emailView.error = "Please Enter Email"
            emailView.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailView.text.toString()).matches()){

            emailView.error = "Please Enter a Valid Email"
            emailView.requestFocus()
            return
        }


        if (passwordView.text.toString().isEmpty()){
            passwordView.error = "Please Enter Password"
            passwordView.requestFocus()
            return
        }




        //-------------------------------------------------//


        // Checks if there is an account created with the corresponding email and password inserted
        ref.signInWithEmailAndPassword(emailView.text.toString(), passwordView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    val user = ref.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.

                    updateUI(null)
                }

            }
    }

    // Auto mail placement if there is any data of account in phone
    public override fun onStart() {
        super.onStart()

        // Checks for existing user
        val currentUser = ref.currentUser

        // If there is already data of a current account it writes the email automatically
        if (currentUser != null)
            emailView.text = Editable.Factory.getInstance().newEditable(currentUser.email)


    }

    // According to result of matching password and email it takes action
    private fun updateUI(currentUser : FirebaseUser?){

        // If there exists a current account associated it checks for the email verification before logging in
        if(currentUser!= null) {
            if(currentUser.isEmailVerified) {
                UserIdFirebase.UID = currentUser.uid

                startActivity(Intent(this, MainMenuActivity::class.java))

            }
            else {
                Toast.makeText(baseContext, "Email Not Verified.",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // If there is no currentUser it shows login error (not found user with current data inserted)
        else {
            Toast.makeText(baseContext, "Wrong Email/Password. Try again",
                Toast.LENGTH_SHORT).show()

        }
    }

}