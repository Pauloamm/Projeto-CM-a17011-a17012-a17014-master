package com.example.floristav100.DataModels

import java.util.*
import kotlin.collections.ArrayList


class Transaction  {

    var totalPrice : Int = 0
    var totalBouquets : Int = 0
    var currentTimeString : String = ""
    var currentDateString : String = ""


    var quantitiesList : MutableList<Int> = ArrayList()
    var bouquetsBoughtList : MutableList<Bouquets> = ArrayList()

    // Used for reading in Firebase without it going BOOOOOM Exception
    constructor()
    
    constructor(bouquetsQuantity : MutableList<Int>, boughtBouquets : MutableList<Bouquets>, totalPrice : Int){


       quantitiesList = bouquetsQuantity
       bouquetsBoughtList = boughtBouquets

       this.totalPrice = totalPrice

        // Gets current time
        getCurrentTime()

        // Calculates total bouquets
        totalBouquetsCalculator()
    }


    private fun getCurrentTime(){

        // Gets time at which the transaction was made

        var currentTime : Calendar = Calendar.getInstance()

        var hour : Int = currentTime.get((Calendar.HOUR_OF_DAY))
        var minute : Int = currentTime.get((Calendar.MINUTE))


        if (minute < 10) currentTimeString = "" + hour + ":0" + minute
        else currentTimeString = "" + hour + ":" + minute



        var day : Int = currentTime.get((Calendar.DAY_OF_MONTH))
        var month : String = getMonthName(currentTime.get((Calendar.MONTH)))
        var year : Int = currentTime.get((Calendar.YEAR))

        currentDateString = "" + day + " " + month + " " + year

    }

    private fun getMonthName(month : Int) : String{

        // Receives month by number and returns its name


        when (month){
            0 -> return "January"
            1 -> return "February"
            2 -> return "March"
            3 -> return "April"
            4 -> return "May"
            5 -> return "June"
            6 -> return "July"
            7 -> return "August"
            8 -> return "September"
            9 -> return "October"
            10 -> return "November"
            11 -> return "December"
        }
         return ""
    }



    private fun totalBouquetsCalculator(){

        var total : Int = 0

        for(x in quantitiesList) total += x

        this.totalBouquets = total
    }


}