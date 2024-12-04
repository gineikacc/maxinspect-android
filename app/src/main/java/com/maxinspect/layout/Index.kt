package com.maxinspect.layout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import com.maxinspect.R
import com.maxinspect.Util

class Index : ComponentActivity() {

    object CODE {
        val FILE_PICK = 4321
    }

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

        val registerProductsButton = findViewById<Button>(R.id.RegisterProductsButton)
        registerProductsButton.visibility = View.GONE
        registerProductsButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "text/csv"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(Intent.createChooser(intent, "Select a CSV file"), CODE.FILE_PICK)
        }


    }

    // Handle the file selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE.FILE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { uri ->


                Log.e("LoginPane", "BIGBIG")
                Log.e("LoginPane", uri.toString())
                Util.dbUploadCSVFile(uri, this)  // Pass the file URI to upload function
// Add response code or smth

            }
        }
    }


}
