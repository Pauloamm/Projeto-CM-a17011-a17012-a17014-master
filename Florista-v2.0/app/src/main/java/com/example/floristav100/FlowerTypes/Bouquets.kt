package com.example.floristav100.FlowerTypes

import android.widget.CheckBox
import java.io.Serializable
import java.util.ArrayList

class Bouquets : Serializable {

    var id :String? = null
    var name :String?= null
    private var flowers : MutableList<Flowers> = ArrayList<Flowers>()
    var numberOfFlowers : Int?= null
    var image : Int? = null
    var isChecked :Boolean? = true

    var sunflowerCounter : Int = 0
    var orchidCounter : Int = 0
    var roseCounter : Int = 0
    var totalPrice : Int = 0

    constructor( name : String, flowersList : MutableList<Flowers>, image : Int)
    {
        this.name = name
        this.flowers = flowersList
        this.numberOfFlowers = flowers.count()
        this.image = image

        getEachFlowerTypeCount()
        totalPrice()
    }

    // Used for reading in firebase without it going BOOOOOM Exception
    constructor()



    private fun getEachFlowerTypeCount(){

        for(f in flowers){

            if(f is Sunflower) sunflowerCounter++
            else if (f is Orchid) orchidCounter++
            else if (f is Rose) roseCounter++


        }
    }

    private fun totalPrice()
    {
        for(f in flowers){

            if(f is Sunflower) totalPrice += f.price!!
            else if (f is Orchid) totalPrice += f.price!!
            else if (f is Rose) totalPrice += f.price!!


        }
    }

    public fun UpdateCheck(checkBoxView: CheckBox){


        this.isChecked = checkBoxView.isChecked

    }

}