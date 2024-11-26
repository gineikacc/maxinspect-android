package com.maxinspect.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.maxinspect.R
import com.maxinspect.models.Product

class PurchaseAnalysisPane : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.analysis_pane)

        val expensiveButton = findViewById<Button>(R.id.expensivePurchasesButton)
        expensiveButton.setOnClickListener {
            //Open login view
            val intent = Intent(this, ProductListPane::class.java)
            intent.putExtra("PRODUCT_LIST_TITLE", "Brangiausios prekės")
            intent.putExtra("PRODUCT_LIST_SORTBY", "price")
            startActivity(intent)
        }

        val proteinButton = findViewById<Button>(R.id.bestProteinPurchasesButton)
        proteinButton.setOnClickListener {
            //Open login view
            val intent = Intent(this, ProductListPane::class.java)
            intent.putExtra("PRODUCT_LIST_TITLE", "Geriausi baltymų šaltiniai")
            intent.putExtra("PRODUCT_LIST_SORTBY", "protein")
            startActivity(intent)
        }

        val fatsButton = findViewById<Button>(R.id.bestFatPurchasesButton)
        fatsButton.setOnClickListener {
            //Open login view
            val intent = Intent(this, ProductListPane::class.java)
            intent.putExtra("PRODUCT_LIST_TITLE", "Geriausi riebalų šaltiniai")
            intent.putExtra("PRODUCT_LIST_SORTBY", "fats")
            startActivity(intent)
        }

        val caloriesButton = findViewById<Button>(R.id.bestCaloriePurchasesButton)
        caloriesButton.setOnClickListener {
            //Open login view
            val intent = Intent(this, ProductListPane::class.java)
            intent.putExtra("PRODUCT_LIST_TITLE", "Geriausi kalorijų šaltiniai")
            intent.putExtra("PRODUCT_LIST_SORTBY", "calories")
            startActivity(intent)
        }

    }

}