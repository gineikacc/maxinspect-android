package com.maxinspect.layout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import com.maxinspect.Globals
import com.maxinspect.R
import com.maxinspect.Util

class Index : ComponentActivity() {

    object CODE {
        val FILE_PICK = 4321
    }

    override fun onResume() {
        super.onResume()
        buttonVisibility()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.index)

        val loginButton = findViewById<Button>(R.id.LoginButton)
        val receiptsButton = findViewById<Button>(R.id.ReceiptsButton)
        val productSearchButton = findViewById<Button>(R.id.ProductsButton)
        val analysisButton = findViewById<Button>(R.id.PurchaseAnalysisButton)
        val registerProductsButton = findViewById<Button>(R.id.RegisterProductsButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginPane::class.java)
            startActivity(intent)
        }

        receiptsButton.setOnClickListener {
            val intent = Intent(this, ReceiptListPane::class.java)
            startActivity(intent)
        }

        productSearchButton.setOnClickListener {
            val intent = Intent(this, ProductSearchPane::class.java)
            startActivity(intent)
        }


        analysisButton.setOnClickListener {
            val intent = Intent(this, PurchaseAnalysisPane::class.java)
            startActivity(intent)
        }

        buttonVisibility()

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

    fun buttonVisibility(){
        val receiptsButton = findViewById<Button>(R.id.ReceiptsButton)
        val productSearchButton = findViewById<Button>(R.id.ProductsButton)
        val analysisButton = findViewById<Button>(R.id.PurchaseAnalysisButton)
        val registerProductsButton = findViewById<Button>(R.id.RegisterProductsButton)


        registerProductsButton.visibility = View.GONE
        if(Globals.userID != ""){
            receiptsButton.visibility = View.VISIBLE
            productSearchButton.visibility = View.VISIBLE
            analysisButton.visibility = View.VISIBLE
            if(Globals.moderatorUserIDs.contains(Globals.userID)) {
                registerProductsButton.visibility = View.VISIBLE
            }
        } else {

            receiptsButton.visibility = View.GONE
            productSearchButton.visibility = View.GONE
            analysisButton.visibility = View.GONE
        }
    }

}
