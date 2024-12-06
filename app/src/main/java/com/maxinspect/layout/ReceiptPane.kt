package com.maxinspect.layout

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.maxinspect.Globals
import com.maxinspect.R
import com.maxinspect.Util
import com.maxinspect.models.Purchase

class ReceiptPane : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_pane)
        Util.syncGlobals()

        val receiptID = intent.getIntExtra("RECEIPT_ID", -1)
        val receipt = Globals.receipts.find { it.id == receiptID }
        Log.w("LoginPane", receiptID.toString())
        Log.w("LoginPane", receiptID.toString())
        Log.w("LoginPane", receiptID.toString())
        Log.w("LoginPane", receiptID.toString())
        Log.w("LoginPane", receiptID.toString())
        var purchases : List<Purchase>
        purchases = Globals.purchases.filter { it.receipt.id == receiptID }

        val receiptDateLabel = findViewById<TextView>(R.id.receiptDataLabel)
        val receiptPriceLabel = findViewById<TextView>(R.id.receiptPriceLabel)

        receiptDateLabel.text = intent.getStringExtra("RECEIPT_DATE")
        val receiptPriceString = intent.getStringExtra("RECEIPT_PRICE")
        val receiptPrice = ((receiptPriceString?.toFloat() ?: 0f) /100f).toString() + " Eur"
        receiptPriceLabel.text = receiptPrice


        val tableLayout = findViewById<TableLayout>(R.id.purchasesTableView)

        val headerRow = TableRow(this)

        // Define header columns
        val headers = arrayOf("Kaina", "Kiekis", "kCal", "Balt", "Rieb", "Angl")
        headers.forEach { headerText ->
            val headerView = TextView(this).apply {
                text = headerText
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding(4, 4, 4, 24)
            }
            headerRow.addView(headerView)
        }
        tableLayout.addView(headerRow)

        for (item in purchases) {
            addToTable(
                item,
                tableLayout,
            ) {

                val intent = Intent(this, ProductPane::class.java)
                intent.putExtra("PRODUCT_ID", it.product.checkName)
                startActivity(intent)
                Toast.makeText(this, "${it.product.displayName} clicked!", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun addToTable(item: Purchase, table: TableLayout, onClick: (x: Purchase) -> Unit){

        // Create a new TableRow
        val nameTableRow = TableRow(this)
        val dataTableRow = TableRow(this)

        // Create and configure a TextView for the item name
        val itemName = TextView(this)
        itemName.text = item.product.displayName
        itemName.setPadding(4, 4, 4, 4)
        itemName.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        ).apply {
           span = 6
        }

        // Create and configure a TextView for the item price
        val itemPrice = TextView(this)
        itemPrice.text = (item.cost/100f).toString() + " Eur"
        itemPrice.setPadding(4, 4, 4, 24)

        // Create and configure a TextView for the item price
        val itemAmount = TextView(this)
        if(item.product.weight == 0) {
            itemAmount.text = item.amount.toString()+"kg"
        } else if (item.product.weight < 0) {
            itemAmount.text = "-"
        } else {
            itemAmount.text = item.amount.toInt().toString()+"vnt"
        }
        itemAmount.setPadding(4, 4, 4, 24)

        // Create and configure a TextView for the item price
        val itemCals = TextView(this)
        itemCals.text = item.product.calories.toInt().toString()
        itemCals.setPadding(4, 4, 4, 24)

        // Create and configure a TextView for the item price
        val itemProtein = TextView(this)
        itemProtein.text = item.product.protein.toString()
        itemProtein.setPadding(4, 4, 4, 24)

        // Create and configure a TextView for the item price
        val itemFat = TextView(this)
        itemFat.text = item.product.fats.toString()
        itemFat.setPadding(4, 4, 4, 24)

        // Create and configure a TextView for the item price
        val itemCarb = TextView(this)
        itemCarb.text = item.product.carbs.toString()
        itemCarb.setPadding(4, 4, 4, 24)

        // Add TextViews to the TableRow
        nameTableRow.addView(itemName)
        dataTableRow.addView(itemPrice)
        dataTableRow.addView(itemAmount)
        dataTableRow.addView(itemCals)
        dataTableRow.addView(itemProtein)
        dataTableRow.addView(itemFat)
        dataTableRow.addView(itemCarb)

        // Add TableRow to TableLayout
        table.addView(nameTableRow)
        table.addView(dataTableRow)

        //Add onClick
        nameTableRow.setOnClickListener{
            onClick(item)
        }
        dataTableRow.setOnClickListener{
            onClick(item)
        }
    }
}