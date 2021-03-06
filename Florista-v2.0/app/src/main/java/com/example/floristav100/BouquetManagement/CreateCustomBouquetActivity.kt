package com.example.floristav100.BouquetManagement


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.floristav100.AccountSettingsAndInfo.UserIdFirebase
import com.example.floristav100.DataModels.*
import com.example.floristav100.DataModels.FlowerHierarchy.Flowers
import com.example.floristav100.DataModels.FlowerHierarchy.Orchid
import com.example.floristav100.DataModels.FlowerHierarchy.Rose
import com.example.floristav100.DataModels.FlowerHierarchy.Sunflower
import com.example.floristav100.DataModels.Utility.FlowerSelection
import com.example.floristav100.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_custom_bouquet.*
import java.util.ArrayList

class CreateCustomBouquetActivity : AppCompatActivity() {

    var flowerSelectionManager : FlowerSelection =
        FlowerSelection()

    lateinit var ref : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_custom_bouquet)
        supportActionBar!!.hide()

        // Gets reference from correspondent node in Firebase of Bouquet storage
        ref = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!! +"/Available Bouquets")


        // Sets up custom adapter
        allFlowerTypeView.adapter = FlowerTypeListAdapter()


        // Manages confirmButton action
        confirmButtonManagement()

    }


    // Manages Confirm Button action
    private fun confirmButtonManagement(){

        // Manages button click
        confirmAdd.setOnClickListener(){


            // Creates Bouquet from selected flowers
            var customBouquet = createCustomBouquet()


            // If there were flowers selected
            if(customBouquet.numberOfFlowers!! > 0){

                // Gets new id in the Firebase for the new created bouquet
                val bouquetId = ref.push().key
                customBouquet.id = bouquetId

                // Adds the new bouquet to the Firebase
                ref.child(bouquetId!!).setValue(customBouquet).addOnCompleteListener{

                    // Makes pop up message confirming the save
                    Toast.makeText(this,"Bouquet Saved!", Toast.LENGTH_LONG).show()

                }


            }
            else{

                // Makes pop up message telling there weren't flowers selected so the bouquet was not saved
                Toast.makeText(this,"No Flowers Selected\nBouquet Not Saved!", Toast.LENGTH_LONG).show()

            }


            // Closes current activity and return to main activity
            finish()

        }


    }



    // Creates custom Bouquet from selected flowers
    private fun createCustomBouquet() : Bouquets{

        // Creates temporary flower list for custom bouquet creation
        var flowerListForCustomBouquet : MutableList<Flowers> = ArrayList<Flowers>()

        for(x in 1..flowerSelectionManager.numberSunflowerSelected) flowerListForCustomBouquet.add(Sunflower())
        for(x in 1..flowerSelectionManager.numberRoseSelected) flowerListForCustomBouquet.add(Rose())
        for(x in 1..flowerSelectionManager.numberOrchidSelected) flowerListForCustomBouquet.add(Orchid())

        // Gets custom name if the user enters one, otherwise gives it a default name ("Custom Bouquet")
        var customName = when{

            customNameTextView.text.toString().isEmpty() -> "Custom Bouquet"
            else -> customNameTextView.text.toString()
        }
        return  Bouquets(customName, flowerListForCustomBouquet, imageChoosing())
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


    inner class FlowerTypeListAdapter : BaseAdapter() {


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