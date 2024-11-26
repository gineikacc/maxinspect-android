package com.maxinspect.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.maxinspect.R

class Index : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.index)


        val loginButton = findViewById<Button>(R.id.LoginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginPane::class.java)
            startActivity(intent)
        }

        val receiptsButton = findViewById<Button>(R.id.ReceiptsButton)
        receiptsButton.setOnClickListener {
            val intent = Intent(this, ReceiptListPane::class.java)
            startActivity(intent)
        }

        val productSearchButton = findViewById<Button>(R.id.ProductsButton)
        productSearchButton.setOnClickListener {
            val intent = Intent(this, ProductSearchPane::class.java)
            startActivity(intent)
        }

        val analysisButton = findViewById<Button>(R.id.PurchaseAnalysisButton)
        analysisButton.setOnClickListener {
            val intent = Intent(this, PurchaseAnalysisPane::class.java)
            startActivity(intent)
        }

    }



}
