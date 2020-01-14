package com.example.floristav100

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
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
            emailView.error = "Please Enter Password"
            emailView.requestFocus()
            return
        }

        ref.createUserWithEmailAndPassword(emailView.text.toString(), passwordView.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account Created Successfully",
                        Toast.LENGTH_SHORT).show()
                        finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "An Error Occurred. Try Again!",
                        Toast.LENGTH_SHORT).show()
                }

            }
    }
}