package com.example.floristav100

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var ref : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
       ref = FirebaseAuth.getInstance()


        SignUpButtonView.setOnClickListener{
           startActivity(Intent(this, SignUpActivity::class.java))
        }


        LoginButtonView.setOnClickListener{
            login()
        }
    }

    private fun login() {
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

                // ...
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = ref.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser : FirebaseUser?){

        if(currentUser!= null) {
            if(currentUser.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))

            }
            else {
                Toast.makeText(baseContext, "Email Not Verified.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(baseContext, "Login failed.",
                Toast.LENGTH_SHORT).show()

        }
    }

}