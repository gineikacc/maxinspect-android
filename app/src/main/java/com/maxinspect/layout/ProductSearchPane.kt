package com.maxinspect.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.maxinspect.R
import com.maxinspect.Util

class ProductSearchPane : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_search_pane)

        val input = findViewById<EditText>(R.id.productSearchInput)
        val submitButton = findViewById<Button>(R.id.productSearchSubmit)
        submitButton.setOnClickListener {
            //Open login view
            val intent = Intent(this, ProductListPane::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val headersToHide = arrayListOf<String>()
            intent.putStringArrayListExtra("PRODUCT_LIST_HIDDEN_HEADERS", headersToHide)
            intent.putExtra("PRODUCT_LIST_TITLE", "Paieškos rezultatai")
            intent.putExtra("PRODUCT_QUERY", input.text.toString())
            Util.dbQueryProducts(input.text.toString())
            startActivity(intent)
        }

    }

}