package com.example.floristav100.DataModels.Utility

import com.example.floristav100.DataModels.*
import com.example.floristav100.DataModels.FlowerHierarchy.Flowers
import com.example.floristav100.DataModels.FlowerHierarchy.Orchid
import com.example.floristav100.DataModels.FlowerHierarchy.Rose
import com.example.floristav100.DataModels.FlowerHierarchy.Sunflower

class FlowerSelection {

    // List for show which flowers are available
    var allDifferentFlowerTypes: MutableList<Flowers> = ArrayList<Flowers>()

    // Counters fo each type of flower selected
     var numberSunflowerSelected = 0
     var numberRoseSelected = 0
     var numberOrchidSelected = 0

    constructor(){

        predefinedListCreation()
    }

    constructor(bouquetReceivedForEdit : Bouquets){

        predefinedListCreation()
        countersDefinedByPreviousCreatedBouquet(bouquetReceivedForEdit)
    }

    private fun countersDefinedByPreviousCreatedBouquet(bouquetReceivedForEdit : Bouquets){

        numberSunflowerSelected = bouquetReceivedForEdit.sunflowerCounter
        numberRoseSelected = bouquetReceivedForEdit.roseCounter
        numberOrchidSelected = bouquetReceivedForEdit.orchidCounter
    }

    // Creates List with all different type of flowers
    private fun predefinedListCreation(){

        allDifferentFlowerTypes.add(Sunflower())
        allDifferentFlowerTypes.add((Rose()))
        allDifferentFlowerTypes.add((Orchid()))
    }


}

