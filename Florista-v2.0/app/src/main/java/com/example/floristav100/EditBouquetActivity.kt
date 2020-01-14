package com.example.floristav100

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.floristav100.FlowerTypes.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.edit_activity.*
import java.util.ArrayList

class EditBouquetActivity : AppCompatActivity (){

    lateinit var flowerSelectionManager : FlowerSelection
    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)

        // Gets bouquet selected
        var bouquetReceived = intent.getSerializableExtra("CurrentBouquet") as Bouquets

        // Gets reference from correspondent node in firebase of Bouquet storage
         ref = FirebaseDatabase.getInstance().getReference("Bouquets")

        // Creates a flower selection manager with the starting values as the ones of the bouquet
        flowerSelectionManager = FlowerSelection(bouquetReceived)

        // Gets the toolbar tiltle to be the same as the selected bouquet
        getSupportActionBar()!!.setTitle(bouquetReceived.name)

        // Updates listView
        allFlowerTypeForEditView.adapter = FlowerTypeListUpdateAdapter()


        // Manages the updateButton click and substitutes value in Firebase
        updateButton.setOnClickListener{

            // Gets updated bouquet object and associates with the id of the one to be updated
            var bouquetUpdated = updateBouquet()
            bouquetUpdated.id = bouquetReceived.id

            // Substitutes the bouquet in the Firebase
            ref.child(bouquetReceived.id!!).setValue(bouquetUpdated)


            // Creates intent to return necessary info
            var resultIntent = Intent()

            // Returns the info to know which action the user chose
            resultIntent.putExtra("TypeOfReturn", "UPDATE")

            // Returns the necessary info to update the bouquet in the MainActivity list of bouquets
            resultIntent.putExtra("BouquetToUpdateId",bouquetUpdated.id )
            resultIntent.putExtra("BouquetForUpdate", bouquetUpdated)

            setResult(Activity.RESULT_OK, resultIntent)

            finish()



        }

        deleteButton.setOnClickListener{


            // Removes the node from the Firebase of the selected bouquet
            ref.child(bouquetReceived.id!!).removeValue()


            // Intent made to return the id of the node removed so it can be removed from the list aswell
            var resultIntent = Intent()

            resultIntent.putExtra("TypeOfReturn", "DELETE")
            resultIntent.putExtra("BouquetToRemoveId",bouquetReceived.id )

            setResult(Activity.RESULT_OK, resultIntent)

            finish()

        }

    }

    private fun updateBouquet() : Bouquets{



        //creates temporary flower list for custom bouquet creation
        var flowerListForCustomBouquet : MutableList<Flowers> = ArrayList<Flowers>()

        for(x in 1..flowerSelectionManager.numberSunflowerSelected) flowerListForCustomBouquet.add(Sunflower())
        for(x in 1..flowerSelectionManager.numberRoseSelected) flowerListForCustomBouquet.add(Rose())
        for(x in 1..flowerSelectionManager.numberOrchidSelected) flowerListForCustomBouquet.add(Orchid())



        return Bouquets("Custom Bouquet", flowerListForCustomBouquet, imageChoosing())


    }

    private fun imageChoosing() : Int{

        var selectedImageforShow : Int


        // Priority list in case its equal number-> Venus - BloodyMary - Shooting Star

        if (flowerSelectionManager.numberOrchidSelected >= flowerSelectionManager.numberRoseSelected)
        {
            if (flowerSelectionManager.numberOrchidSelected >= flowerSelectionManager.numberSunflowerSelected)
            {
                selectedImageforShow = R.drawable.venus
            }
            else selectedImageforShow = R.drawable.shootingstar
        }
        else
        {
            if (flowerSelectionManager.numberRoseSelected >= flowerSelectionManager.numberSunflowerSelected)
            {
                selectedImageforShow = R.drawable.bloodymary
            }
            else selectedImageforShow = R.drawable.shootingstar
        }


        return selectedImageforShow
    }

    inner class FlowerTypeListUpdateAdapter : BaseAdapter() {


        override fun getView(position: Int, convertView: View?, parente: ViewGroup?): View {

            var currentFlower : Flowers = getItem(position) as Flowers

            // gets view information
            var v = layoutInflater.inflate(R.layout.flowertype_row, parente, false)

            var textViewNome = v.findViewById<TextView>(R.id.flowerTypeNameView)
            textViewNome.text = currentFlower.name.toString()


            var flowerImageView = v.findViewById<ImageView>(R.id.flowerTypeImageView)
            flowerImageView.setImageResource( currentFlower.image!!)



            // Gets adding and removing flowers buttons
            var minusButtonView = v.findViewById<Button>(R.id.minusButton) as Button
            var plusButtonView = v.findViewById<Button>(R.id.plusButton) as Button


            // Gets current flower type number
            var currentFlowerTypeSelectionView =  v.findViewById(R.id.flowerTypeNumberSelection) as EditText



            when(currentFlower){

                is Sunflower -> currentFlowerTypeSelectionView.text = Editable.Factory.getInstance().newEditable(flowerSelectionManager.numberSunflowerSelected.toString())
                is Rose -> currentFlowerTypeSelectionView.text = Editable.Factory.getInstance().newEditable(flowerSelectionManager.numberRoseSelected.toString())
                is Orchid -> currentFlowerTypeSelectionView.text = Editable.Factory.getInstance().newEditable(flowerSelectionManager.numberOrchidSelected.toString())

            }

            var currentNumber = currentFlowerTypeSelectionView.text.toString().toInt()


            currentFlowerTypeNumberStoring(currentNumber,currentFlower)


            minusButtonView.setOnClickListener {

                var currentNumber = currentFlowerTypeSelectionView.text.toString().toInt()
                currentNumber--


                currentFlowerTypeSelectionView.text = Editable.Factory.getInstance().newEditable(currentNumber.toString())

                currentFlowerTypeNumberStoring(currentNumber,currentFlower)


            }


            plusButtonView.setOnClickListener {

                var currentNumber = currentFlowerTypeSelectionView.text.toString().toInt()

                currentNumber++


                currentFlowerTypeSelectionView.text =  Editable.Factory.getInstance().newEditable(currentNumber.toString())

                currentFlowerTypeNumberStoring(currentNumber,currentFlower)



            }

            return  v
        }



        private fun currentFlowerTypeNumberStoring (currentNumber : Int, currentFlower : Flowers) {


            when(currentFlower){

                is Sunflower -> {

                    flowerSelectionManager.numberSunflowerSelected = currentNumber

                }
                is Rose ->{

                    flowerSelectionManager.numberRoseSelected = currentNumber
                }
                is Orchid ->{

                    flowerSelectionManager.numberOrchidSelected = currentNumber
                }


            }


            /*currentFlowerTypeNumberSelection += increase

            currentFlowerTypeSelectionView.text = currentFlowerTypeNumberSelection.toString()

            when(currentFlowerType){

                is Rose -> numberRoseSelected = currentFlowerTypeNumberSelection
                is Orchid -> numberOrchidSelected = currentFlowerTypeNumberSelection
                is Sunflower -> numberSunflowerSelected = currentFlowerTypeNumberSelection
            }*/


        }




        override fun getItem(position: Int): Any {
            return flowerSelectionManager.allDifferentFlowerTypes[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return flowerSelectionManager.allDifferentFlowerTypes.size
        }



    }



}