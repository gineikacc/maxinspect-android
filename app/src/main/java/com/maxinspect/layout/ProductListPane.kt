package com.maxinspect.layout

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.maxinspect.Globals
import com.maxinspect.R
import com.maxinspect.models.Product


class ProductListPane : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_list_pane)
        // Sample data for the RecyclerView
        lateinit var itemList : List<Product>
        itemList = ArrayList(Globals.products)
        var query = intent.getStringExtra("PRODUCT_QUERY")
        if(query != null) {
            itemList = ArrayList(itemList).filter { it.checkName.contains(query, true) }
        }
        // Sort by given attribute
        val sortby = intent.getStringExtra("PRODUCT_LIST_SORTBY") ?: "none"
        if(sortby=="price"){
            itemList = itemList.sortedByDescending { it.price }
        }
        if(sortby=="protein"){
            itemList = itemList.sortedByDescending { it.protein }
        }
        if(sortby=="fats"){
            itemList = itemList.sortedByDescending { it.fats }
        }
        if(sortby=="calories"){
            itemList = itemList.sortedByDescending { it.calories }
        }


        val productListTitle = findViewById<TextView>(R.id.productListTitle)
        productListTitle.text = intent.getStringExtra("PRODUCT_LIST_TITLE") ?: "WEOW"

        val tableLayout = findViewById<TableLayout>(R.id.purchasesTableView)
        val headersToHide = intent.getStringArrayListExtra("PRODUCT_LIST_HIDDEN_HEADERS")
        val headerRow = TableRow(this)

        // Define header columns
        val headers = arrayOf("Kaina", "Svoris", "kCal", "Balt", "Rieb", "Angl")
        headers.forEach { headerText ->
            val headerView = TextView(this).apply {
                text = headerText
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding(4, 4, 4, 24)
            }
            if( headersToHide == null || !headersToHide.contains(headerText)){
                headerRow.addView(headerView)
            }
        }
        tableLayout.addView(headerRow)

        for (item in itemList) {
            addToTable(
                item,
                tableLayout,
                headersToHide,
            ) {
                val intent = Intent(this, ProductPane::class.java)
                intent.putExtra("PRODUCT_ID", item.checkName)
                startActivity(intent)
            }
        }

    }

    private fun addToTable(item:Product, table: TableLayout, headersToHide: ArrayList<String>? ,onClick: (x: Product) -> Unit){

        val hiddenHeaders = headersToHide ?: arrayListOf<String>()

        // Create a new TableRow
        val nameTableRow = TableRow(this)
        val dataTableRow = TableRow(this)

        // Create and configure a TextView for the item name
        val itemName = TextView(this)
        itemName.text = item.displayName
        itemName.setPadding(4, 4, 4, 4)
        itemName.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        ).apply {
            span = 4
        }

        // Create and configure a TextView for the item price
        if(!hiddenHeaders.contains("Kaina")) {
            val itemPrice = TextView(this)
            itemPrice.text = (item.price/100f).toString() + " Eur"
            itemPrice.setPadding(4, 4, 4, 24)
            dataTableRow.addView(itemPrice)
        }

        // Create and configure a TextView for the item price
        if(!hiddenHeaders.contains("Svoris")) {
            val itemAmount = TextView(this)
            if (item.weight == 0) {
                itemAmount.text = "Sveriamas"
            } else {
                itemAmount.text = item.weight.toString() + "g"
            }
            itemAmount.setPadding(4, 4, 4, 24)
            dataTableRow.addView(itemAmount)
        }

        // Create and configure a TextView for the item price
        if(!hiddenHeaders.contains("kCal")) {
            val itemCals = TextView(this)
            itemCals.text = item.calories.toInt().toString()
            itemCals.setPadding(4, 4, 4, 24)
            dataTableRow.addView(itemCals)
        }

        // Create and configure a TextView for the item price
        if(!hiddenHeaders.contains("Balt")) {
            val itemProtein = TextView(this)
            itemProtein.text = item.protein.toString()
            itemProtein.setPadding(4, 4, 4, 24)
            dataTableRow.addView(itemProtein)
        }

        // Create and configure a TextView for the item price
        if(!hiddenHeaders.contains("Rieb")) {
            val itemFat = TextView(this)
            itemFat.text = item.fats.toString()
            itemFat.setPadding(4, 4, 4, 24)
            dataTableRow.addView(itemFat)
        }

        // Create and configure a TextView for the item price
        if(!hiddenHeaders.contains("Angl")) {
            val itemCarb = TextView(this)
            itemCarb.text = item.carbs.toString()
            itemCarb.setPadding(4, 4, 4, 24)
            dataTableRow.addView(itemCarb)
        }

        // Add TextViews to the TableRow
        nameTableRow.addView(itemName)

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
