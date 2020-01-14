package com.example.floristav100

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var ref : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        ref = FirebaseAuth.getInstance()


        SignUpButtonView.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = ref.currentUser
        //updateUI(currentUser)
    }

    fun updateUI(currentUser : FirebaseAuth?){


    }

}