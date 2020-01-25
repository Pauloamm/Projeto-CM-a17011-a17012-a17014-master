package com.example.floristav100.LoginAndAccountManagement

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.floristav100.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.emailView
import kotlinx.android.synthetic.main.activity_login.passwordView
import kotlinx.android.synthetic.main.activity_signup.*


//https://firebase.google.com/docs/auth/android/password-auth


class SignUpActivity : AppCompatActivity() {

    private lateinit var ref : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        ref = FirebaseAuth.getInstance()


        CreateAccountButtonView.setOnClickListener{
            newAccountCreation()
        }

    }

    private fun newAccountCreation(){
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

        if (confirmPassowrdView.text.toString() != passwordView.text.toString()){
            confirmPassowrdView.error = "Passwords do not Match"
            confirmPassowrdView.requestFocus()
            return
        }


        ref.createUserWithEmailAndPassword(emailView.text.toString(), passwordView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = ref.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(this, "Account Created Successfully",
                                    Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Password too Short. Try Again!",
                        Toast.LENGTH_SHORT).show()
                }

            }
    }
}