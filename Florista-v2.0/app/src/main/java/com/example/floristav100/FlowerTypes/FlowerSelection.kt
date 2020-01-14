package com.example.floristav100.FlowerTypes

class FlowerSelection {

    // List for show which flowers are available
    var allDifferentFlowerTypes: MutableList<Flowers> = ArrayList<Flowers>()

    // Counters fo each type of flower selected
     var numberSunflowerSelected = 0
     var numberRoseSelected = 0
     var numberOrchidSelected = 0

    constructor(){

        PredefinedListCreation()
    }

    constructor(bouquetReceivedForEdit : Bouquets){

        PredefinedListCreation()
        CountersDefinedByPreviousCreatedBouquet(bouquetReceivedForEdit)




    }

    private fun CountersDefinedByPreviousCreatedBouquet(bouquetReceivedForEdit : Bouquets){

        numberSunflowerSelected = bouquetReceivedForEdit.sunflowerCounter
        numberRoseSelected = bouquetReceivedForEdit.roseCounter
        numberOrchidSelected = bouquetReceivedForEdit.orchidCounter


    }

    // Creates List with all different type of flowers
    private fun PredefinedListCreation(){

        allDifferentFlowerTypes.add(Sunflower())
        allDifferentFlowerTypes.add((Rose()))
        allDifferentFlowerTypes.add((Orchid()))



    }


}

