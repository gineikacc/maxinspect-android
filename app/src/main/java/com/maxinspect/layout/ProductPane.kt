package com.maxinspect.layout

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.maxinspect.Globals
import com.maxinspect.R

class ProductPane : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_pane)

        // Sample data for the RecyclerView
        val prodID = intent.getStringExtra("PRODUCT_ID")
        val prod = Globals.products.find { it.checkName==prodID }!!

        val productTitle = findViewById<TextView>(R.id.productTitle)
        val productWeight = findViewById<TextView>(R.id.productWeight)
        val productPrice = findViewById<TextView>(R.id.productPrice)
        val productProtein = findViewById<TextView>(R.id.productProtein)
        val productFats = findViewById<TextView>(R.id.productFats)
        val productCarbs = findViewById<TextView>(R.id.productCarbs)
        val productCalories = findViewById<TextView>(R.id.productCals)

        val price = (prod.price.toFloat()/100f).toString()
        productTitle.text = "Pavadinimas : ${prod.displayName}"
        val weight = if(prod.weight == 0) "sveriamas" else prod.weight.toString()+"g"
        productWeight.text = "Svoris : ${weight}"
        productPrice.text = "Kaina : $price Eur"

        productCalories.text = "Kalorijos/100g : ${prod.calories}"
        productProtein.text = "Baltymai/100g : ${prod.protein}"
        productFats.text = "Riebalai/100g : ${prod.fats}"
        productCarbs.text = "Angliavandeniai/100g : ${prod.carbs}"
    }

}