package com.example.floristav100.Menus

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.diegodobelo.expandingview.ExpandingItem
import com.diegodobelo.expandingview.ExpandingList
import com.example.floristav100.AccountSettingsAndInfo.UserIdFirebase
import com.example.floristav100.DataModels.Bouquets
import com.example.floristav100.DataModels.Transaction
import com.example.floristav100.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.expanding_sub_item.*


class HistoryTransactionActivity : AppCompatActivity() {

    private var mExpandingList: ExpandingList? = null

    var transactionList : MutableList<Transaction> = ArrayList()

    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()

        setContentView(R.layout.activity_history_transaction)

        ref = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!! + "/Transaction History")

        mExpandingList = findViewById(R.id.expanding_list_main)

        readingDataFirebase()

        createItems()

    }



    fun readingDataFirebase(){

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){

                    for (h in p0.children){


                        // Gets current node Bouquet
                        var transactionInCurrentNode = h.getValue(Transaction::class.java)


                        transactionList.add(transactionInCurrentNode!!)


                    }

                    // Updates listView
                    // bouquetListView.adapter = BouquetAdapter()
                }

            }

        })
    }


    private fun createItems() {


        for(putaquepariuestamerda in transactionList)
        {
            addItem(putaquepariuestamerda.totalPrice, putaquepariuestamerda.quantitiesList, putaquepariuestamerda.bouquetsBoughtList )
        }

    }

    private fun addItem(totalPrice: Int, quantitiesList : MutableList<Int>, bouquetsBought: MutableList<Bouquets>) {
        //Let's create an item with R.layout.expanding_layout
        val item = mExpandingList!!.createNewItem(R.layout.expanding_layout)

        //If item creation is successful, let's configure it
        if (item != null) {
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            (item.findViewById(R.id.titleTextView) as TextView).text = totalPrice.toString()

            //We can create items in batch.
            item.createSubItems(bouquetsBought.size)
            for (i in 0 until item.subItemsCount) {
                //Let's get the created sub item by its index
                val view = item.getSubItemView(i)




                //Let's set some values in
                configureSubItem(view, bouquetsBought[i], quantitiesList[i])
            }



        }
    }

    private fun configureSubItem(view: View, currentBouquet: Bouquets, currentBoquetQuantity : Int) {

        sub_titleTextView.text = currentBouquet.name


        //sets up flower count info on screen
        var flowersNumbers =
            "x" + currentBouquet.sunflowerCounter.toString() + " Sunflowers \n" +
                    "x" +currentBouquet.roseCounter.toString() + " Roses \n" +
                    "x" + currentBouquet.orchidCounter.toString() + " Orchids\n" +
                    "Total: " +currentBouquet.numberOfFlowers.toString()

        sub_descriptionTextView.text = flowersNumbers

        sub_bouquetCounterTextView.text = "x" + currentBoquetQuantity.toString()


        sub_currentBouquetTotalPrice.text = "Price: " + currentBouquet.totalPrice * currentBoquetQuantity

        sub_currentBouquetImageView.setImageResource(currentBouquet.image!!)
    }



    internal interface OnItemCreated {
        fun itemCreated(title: String)
    }
}
