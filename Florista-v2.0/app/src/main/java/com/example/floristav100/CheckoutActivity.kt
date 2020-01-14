package com.example.floristav100

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.floristav100.FlowerTypes.*
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import kotlinx.android.synthetic.main.activity_checkout.*
import java.math.BigDecimal

//Paypal acc:
//dlurdes@inwmail.net
//Dlurdesflorista!1

//bus-dlurdes@hotmail.com (business)
//p-dlurdes@hotmail.com (client used for testing)
//p2-dlurdes@hotmail.com
//123456789

//developer.paypal.com

class CheckoutActivity : AppCompatActivity(){

    // List of bouquets selected
    var checkoutBouquetList : MutableList<Bouquets> = ArrayList<Bouquets>()
    var priceToPay : Int = 0

    var config: PayPalConfiguration? = null
    var amount:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)



        // Gets the number of selected bouquets
        var checkedBouquetCounter = intent.getIntExtra("BouquetCounter",0)

        for(x in 1.. checkedBouquetCounter){


            // Gets all bouquets sent through intent (the ones selected) and adds them to the list
            var bouquetKey = "BouquetNumber" + x
            var currentBouquet = intent.getSerializableExtra(bouquetKey) as Bouquets

            checkoutBouquetList.add(currentBouquet)


        }


        // Initial total price(1 of each bouquet)
        for(b in checkoutBouquetList){

            priceToPay += b.totalPrice

        }
        paypalButton.text = "PayPal: " + priceToPay + "€"


        config = PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(UserInfo.client_id)
        var intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config)
        startService(intent)



        paypalButton.setOnClickListener{


            amount = priceToPay.toDouble()

            var payment = PayPalPayment(BigDecimal.valueOf(amount),"EUR","DLurdes", PayPalPayment.PAYMENT_INTENT_SALE)
            var intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
            startActivityForResult(intent, 1)
        }




        // Sets up custom adapter
        checkoutListView.adapter = CheckoutListAdapter()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1){

            if (resultCode == Activity.RESULT_OK)
            {
                Toast.makeText(this,"Transaction Completed!", Toast.LENGTH_LONG).show()
                finish()
            }
            else
            {
                Toast.makeText(this,"Transaction Failed!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun totalPriceUpdate(valueToRemove : Int, valueToAdd: Int){

        priceToPay -= valueToRemove
        priceToPay += valueToAdd
        paypalButton.text = "PayPal: " + priceToPay + "€"



    }

    inner class CheckoutListAdapter : BaseAdapter() {


        override fun getView(position: Int, convertView: View?, parente: ViewGroup?): View {

            var currentBouquet : Bouquets = getItem(position) as Bouquets

            // Gets View information
            var v = layoutInflater.inflate(R.layout.checkout_row, parente, false)

            var textViewNome = v.findViewById<TextView>(R.id.checkoutBouquetNameView)
            textViewNome.text = currentBouquet.name.toString()


            var bouquetImageView = v.findViewById<ImageView>(R.id.checkoutBouquetImageView)
            bouquetImageView.setImageResource( currentBouquet.image!!)



            // Gets adding and removing flowers buttons
            var minusButtonView = v.findViewById<Button>(R.id.checkoutMinusButton) as Button
            var plusButtonView = v.findViewById<Button>(R.id.checkoutPlusButton) as Button


            // Gets current flower type number

            var currentBouquetQuantityView =  v.findViewById(R.id.checkoutBouquetQuantity) as TextView

            var currentNumber = currentBouquetQuantityView.text.toString().toInt()

            var bouquetPriceView = v.findViewById<TextView>(R.id.checkoutBouquetPrice)
            var totalPriceOfCurrentBouquetQuantity = currentNumber *  currentBouquet.totalPrice
            bouquetPriceView.text = totalPriceOfCurrentBouquetQuantity.toString()




            //Sets up flower count info on screen
            var checkoutBouquetFlowerDescriptionView = v.findViewById<TextView>(R.id.checkoutBouquetFlowerDescription)
            var flowersNumbers =
                "x" + currentBouquet.sunflowerCounter.toString() + " Sunflowers \n" +
                        "x" +currentBouquet.roseCounter.toString() + " Roses \n" +
                        "x" + currentBouquet.orchidCounter.toString() + " Orchids\n" +
                        "Total: " +currentBouquet.numberOfFlowers.toString()

            checkoutBouquetFlowerDescriptionView.text = flowersNumbers



            minusButtonView.setOnClickListener {

                var valueToRemove = currentNumber * currentBouquet.totalPrice

                currentNumber--

                var totalPriceOfCurrentBouquetQuantity = currentNumber *  currentBouquet.totalPrice
                bouquetPriceView.text = totalPriceOfCurrentBouquetQuantity.toString()

                totalPriceUpdate(valueToRemove,totalPriceOfCurrentBouquetQuantity)

                currentBouquetQuantityView.text = currentNumber.toString()

            }


            plusButtonView.setOnClickListener {

                var valueToRemove = currentNumber * currentBouquet.totalPrice

                currentNumber++

                var totalPriceOfCurrentBouquetQuantity = currentNumber *  currentBouquet.totalPrice
                bouquetPriceView.text = totalPriceOfCurrentBouquetQuantity.toString()

                totalPriceUpdate(valueToRemove,totalPriceOfCurrentBouquetQuantity)

                currentBouquetQuantityView.text =  currentNumber.toString()



            }





            return  v
        }


        override fun getItem(position: Int): Any {
            return checkoutBouquetList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return checkoutBouquetList.size
        }



    }

}