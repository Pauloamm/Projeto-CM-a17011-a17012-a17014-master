package com.example.floristav100.DataModels.FlowerHierarchy

import java.io.Serializable


abstract class Flowers : Serializable {

    var name: String? = null
    var image : Int? = null
    var price : Int? = null


}