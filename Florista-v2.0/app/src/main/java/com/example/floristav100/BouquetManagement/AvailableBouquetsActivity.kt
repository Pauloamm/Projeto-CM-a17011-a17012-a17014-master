package com.example.floristav100.BouquetManagement

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.floristav100.AccountSettingsAndInfo.AccountSettingsActivity
import com.example.floristav100.AccountSettingsAndInfo.UserIdFirebase
import com.example.floristav100.DataModels.*
import com.example.floristav100.DataModels.FlowerHierarchy.Flowers
import com.example.floristav100.DataModels.FlowerHierarchy.Orchid
import com.example.floristav100.DataModels.FlowerHierarchy.Rose
import com.example.floristav100.DataModels.FlowerHierarchy.Sunflower
import com.example.floristav100.Payment.CheckoutActivity
import com.example.floristav100.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_password_check.view.*
import java.util.ArrayList

class AvailableBouquetsActivity : AppCompatActivity() {

    var bouquetList : MutableList<Bouquets> = ArrayList<Bouquets>()
    lateinit var ref: DatabaseReference
    lateinit var refToConfirmPassword : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()


        // Gets reference from correspondent node in Firebase of Bouquet storage
        ref = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!! +"/Available Bouquets")
        refToConfirmPassword = FirebaseAuth.getInstance()





        // Sets up adapter for the list
        bouquetListView.adapter = BouquetAdapter()


        // Reads custom bouquets from Firebase
        readingFirebaseData()

        // Calls and manages result from CreateCustomBouquetActivity
        //addNewBouquetManager()

        // Manages the button for the CheckoutActivity
        checkoutManager()



    }




    //------------------------ BOTAO SETTINGS ACC
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detalhe_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {



                //--------------------------------
                var dialog = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.dialog_password_check,null)
                dialog.setView(dialogView)
                dialog.setCancelable(true)


                // confirmPasswordDialog.show()


                //val customDialog = confirmPasswordDialog.create()

                //customDialog.show()
                dialog.show()

               // customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                    dialogView.DialogPasswordButtonView.setOnClickListener{

                    refToConfirmPassword.signInWithEmailAndPassword(refToConfirmPassword.currentUser!!.email.toString(), dialogView.dialogPasswordView.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                startActivityForResult(Intent(this, AccountSettingsActivity::class.java), 2)
                            }
                            else
                            {
                                dialogView.dialogPasswordView.error = "Wrong Password"
                                dialogView.dialogPasswordView.requestFocus()
                            }
                        }


                }
                //--------------------------------

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //------------------------ BOTAO SETTINGS ACC




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


            val intent = Intent(this@AvailableBouquetsActivity, CheckoutActivity::class.java)


            var bouquetCounter : Int = 0

            for(checkBouquet in bouquetList){

                if(checkBouquet.isChecked == true){

                    bouquetCounter++

                    var bouquetKey = "BouquetNumber" + bouquetCounter

                    intent.putExtra(bouquetKey, checkBouquet)
                }


            }

            intent.putExtra("BouquetCounter", bouquetCounter)

            if(bouquetCounter > 0)
            startActivity(intent)
            else
                Toast.makeText(this,"No Bouquets Selected for Checkout!", Toast.LENGTH_LONG).show()



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

        // DEPOIS DE APAGAR - SAIR
        else if(requestCode == 2 && resultCode == Activity.RESULT_OK) finish()

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
            priceView.text = "Price: " + currentBouquet.totalPrice.toString() + "€"

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


            // Manages item click
            v.setOnClickListener {


                // Only shows edit menu if the bouquet is not predefined
                if( currentBouquet.id != "PredefinedBouquet_1"
                    && currentBouquet.id != "PredefinedBouquet_2"
                    && currentBouquet.id != "PredefinedBouquet_3"){


                    var intent = Intent(this@AvailableBouquetsActivity, EditBouquetActivity::class.java)

                    intent.putExtra("CurrentBouquet", getItem(position) as Bouquets)

                    startActivityForResult(intent,1)



                }
                // If the bouquet is not a predefined one it can Update or be deleted
                else{

                    Toast.makeText(this@AvailableBouquetsActivity, "Predefined Bouquets can't be edited!", Toast.LENGTH_SHORT).show()

                }

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




