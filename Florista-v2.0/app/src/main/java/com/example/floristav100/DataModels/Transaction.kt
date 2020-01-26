package com.example.floristav100.DataModels

class Transaction {

    var totalPrice : Int = 0

    var quantitiesList : MutableList<Int> = ArrayList()
    var bouquetsBoughtList : MutableList<Bouquets> = ArrayList()

    constructor(bouquetsQuantity : MutableList<Int>, boughtBouquets : MutableList<Bouquets>, totalPrice : Int){


       quantitiesList = bouquetsQuantity
       bouquetsBoughtList = boughtBouquets

       this.totalPrice = totalPrice
    }

}