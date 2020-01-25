package com.example.floristav100

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : AppCompatActivity() {


    private lateinit var ref : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_menu)


        ref = FirebaseAuth.getInstance()


        usernameTextView.text = ref.currentUser!!.displayName
        emailTextView.text = ref.currentUser!!.email


        historyButtonView.setOnClickListener{

        }


        availableBouquetsButtonView.setOnClickListener{
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

        }

        createCustomBouquetButtonView.setOnClickListener{

        }

        transactionHistoryButtonView.setOnClickListener{

        }


    }

}