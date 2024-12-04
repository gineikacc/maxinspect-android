package com.maxinspect.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.maxinspect.R

class ProductSearchPane : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_search_pane)

        val submitButton = findViewById<Button>(R.id.productSearchSubmit)
        submitButton.setOnClickListener {
            //Open login view
            val intent = Intent(this, ProductListPane::class.java)
            val headersToHide = arrayListOf<String>()
            intent.putExtra("PRODUCT_LIST_TITLE", "Paieškos rezultatai")
            intent.putStringArrayListExtra("PRODUCT_LIST_HIDDEN_HEADERS", headersToHide)
            startActivity(intent)
        }

    }

}