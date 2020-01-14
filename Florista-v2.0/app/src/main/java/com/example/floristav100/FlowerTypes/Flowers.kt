package com.example.floristav100.FlowerTypes

import android.widget.ImageView
import java.io.Serializable


abstract class Flowers : Serializable {

    var name: String? = null
    var image : Int? = null
    var price : Int? = null


}