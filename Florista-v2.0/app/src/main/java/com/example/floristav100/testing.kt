package com.example.floristav100

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.bloder.magic.view.MagicButton
import kotlinx.android.synthetic.main.activity_transaction_history.*

class testing : AppCompatActivity(){




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history_transaction)


        pinheiros.setMagicButtonClickListener{
            Toast.makeText(this, "Po caralho paulo", Toast.LENGTH_SHORT).show()
        }

    }
}