package com.example.floristav100

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.floristav100.FlowerTypes.*
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    var bouquetList : MutableList<Bouquets> = ArrayList<Bouquets>()
    lateinit var ref: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setTitle("D.Lurdes");

        // Gets reference from correspondent node in Firebase of Bouquet storage
        ref = FirebaseDatabase.getInstance().getReference("Bouquets")


        // Created predefined bouquets(not stored in Firebase)
        predefinedBouquetsCreation()


        // Sets up adapter for the list
        bouquetListView.adapter = BouquetAdapter()


        // Reads custom bouquets from Firebase
        readingFirebaseData()

        // Calls and manages result from CreateCustomBouquetActivity
        addNewBouquetManager()

        // Manages the button for the CheckoutActivity
        checkoutManager()



    }


    // Creates 3 predefined Bouquets that are not stored in Firebase
    private fun predefinedBouquetsCreation() {
        var flowersListForPredefinedBouquet: MutableList<Flowers> = ArrayList<Flowers>()

        //First Bouquet- 100 sunflowers

        for(x in 0..99  ){


            flowersListForPredefinedBouquet.add(Sunflower())

        }
        bouquetList.add(Bouquets("Shooting Star",flowersListForPredefinedBouquet,R.drawable.shootingstar))

        flowersListForPredefinedBouquet.clear()

        //SecondBouquet- 100 Roses

        for(x in 0..99  ){


            flowersListForPredefinedBouquet.add(Rose())

        }
        bouquetList.add(Bouquets("Bloody Mary", flowersListForPredefinedBouquet, R.drawable.bloodymary))

        flowersListForPredefinedBouquet.clear()


        //Third Bouquet- 100 Orchids

        for(x in 0..99  ){


            flowersListForPredefinedBouquet.add(Orchid())

        }
        bouquetList.add(Bouquets("Venus", flowersListForPredefinedBouquet, R.drawable.venus))

        flowersListForPredefinedBouquet.clear()


    }

    // Reads the data from the associated Firebase and stores them in the list
    private fun readingFirebaseData(){



        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){

                    for (h in p0.children){

                        // Bool to check if the node is'nt already stored in the list
                        var alreadyInList : Boolean = false

                        // Gets current node Bouquet
                        var bouquetInCurrentNode = h.getValue(Bouquets::class.java)


                        // Checks the list for a bouquet with the same id
                        for(b in bouquetList){

                            // If it finds one it changes the bool variable to true
                            if(b.id != null && b.id == bouquetInCurrentNode!!.id ) {

                                alreadyInList = true
                                break

                            }

                        }

                        // If the bouquet in the current node of the firebase is'nt stored in the list it stores it
                        if(alreadyInList == false){

                            bouquetList.add(bouquetInCurrentNode!!)

                        }


                    }

                    // Updates listView
                    bouquetListView.adapter = BouquetAdapter()




                }

            }

        })


    }

    // Manages the button for the CheckoutActivity
    private fun checkoutManager(){

        checkoutButtonView.setOnClickListener{


            val intent = Intent(this@MainActivity, CheckoutActivity::class.java)


            var bouquetCounter : Int = 0

            for(checkBouquet in bouquetList){

                if(checkBouquet.isChecked == true){

                    bouquetCounter++

                    var bouquetKey = "BouquetNumber" + bouquetCounter

                    intent.putExtra(bouquetKey, checkBouquet)
                }


            }

            intent.putExtra("BouquetCounter", bouquetCounter)
            startActivity(intent)

        }


    }

   // Manages the button for the CreateCustomBouquetActivity
    private fun addNewBouquetManager(){

        addNewBouquet.setOnClickListener {

            val intent = Intent(this@MainActivity, CreateCustomBouquetActivity::class.java)

            startActivity(intent)

        }

    }

    // Manages activity results from EditBouquetActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result from EditBouquetActivity
         if(requestCode == 1 && resultCode == Activity.RESULT_OK){

            // Gets which action was made (Update)
            var typeOfReturn : String = data?.getStringExtra("TypeOfReturn")!!


            // UPDATE ACTION
             if(typeOfReturn == "UPDATE"){

                // Gets data returned from the EditBouquetActivity

                // Gets the id of the bouquet to update
                var bouquetIdToUpdate = data?.getStringExtra("BouquetToUpdateId")

                // Gets the updated bouquet
                var bouquetUpdated =  data?.getSerializableExtra("BouquetForUpdate") as Bouquets


                // Variable responsible for storing the index of the bouquet in the list to update
                var indexToSubstitute : Int = 0

                for(b in bouquetList){

                    if(b.id == bouquetIdToUpdate) indexToSubstitute = bouquetList.indexOf(b)

                }

                // Substitutes the bouquet with the same id
                bouquetList[indexToSubstitute] = bouquetUpdated

                // Small message pop up to show it went sucessfully
                Toast.makeText(this,"Bouquet Updated", Toast.LENGTH_LONG).show()



            }

            // DELETE ACTION
            else if(typeOfReturn == "DELETE"){

                 // Gets the data of the id of the bouquet to remove
                var bouquetIdToRemove = data?.getStringExtra("BouquetToRemoveId")

                 // Searches the bouquet list for the bouquet with the same id to be removed
                for(b in bouquetList){

                    if(b.id == bouquetIdToRemove){

                        bouquetList.remove(b)
                        break

                    }

                }

                 // Makes small message pop up
                 Toast.makeText(this,"Bouquet Deleted", Toast.LENGTH_LONG).show()

            }

            // Updates listView
            bouquetListView.adapter = BouquetAdapter()

        }

    }

    // Bouquet Adapter
    inner class BouquetAdapter : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parente: ViewGroup?): View {

            // Gets current bouquet
            var currentBouquet : Bouquets = getItem(position) as Bouquets

            // gets view information
            var v = layoutInflater.inflate(R.layout.bouquet_row, parente, false)

            var textViewNome = v.findViewById<TextView>(R.id.bouquetNameView) as TextView
            textViewNome.text = bouquetList[position].name

            var textViewFlowerCount = v.findViewById<TextView>(R.id.bouquetFlowerCountView) as TextView
            textViewFlowerCount.text = bouquetList[position].numberOfFlowers.toString()

            var imageViewBouquet = v.findViewById<ImageView>(R.id.bouquetImageView) as ImageView
            imageViewBouquet.setImageResource( bouquetList[position].image!!)

            var checkView = v.findViewById(R.id.checkBuyView) as CheckBox
            currentBouquet.UpdateCheck(checkView)

            var priceView = v.findViewById<TextView>(R.id.bouquetPriceView)
            priceView.text = currentBouquet.totalPrice.toString()

            var checkBox = v.findViewById<CheckBox>(R.id.checkBuyView)
            checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener {
                    compoundButton, b -> currentBouquet.UpdateCheck(checkBox)
            })



            //sets up flower count info on screen
            var flowersNumbers =
                "x" + currentBouquet.sunflowerCounter.toString() + " Sunflowers \n" +
                        "x" +currentBouquet.roseCounter.toString() + " Roses \n" +
                        "x" + currentBouquet.orchidCounter.toString() + " Orchids\n" +
                        "Total: " +currentBouquet.numberOfFlowers.toString()

            textViewFlowerCount.text = flowersNumbers



            // If the bouquet is not a predefined one it can Update or be deleted
            if(currentBouquet.id != null)
            v.setOnClickListener{


                var intent = Intent(this@MainActivity, EditBouquetActivity::class.java)

                intent.putExtra("CurrentBouquet", getItem(position) as Bouquets)

                startActivityForResult(intent,1)

            }


            return  v
        }

        override fun getItem(position: Int): Any {
            return bouquetList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return bouquetList.size
        }

    }
}




