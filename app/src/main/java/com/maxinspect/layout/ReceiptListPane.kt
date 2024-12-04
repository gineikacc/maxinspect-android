package com.maxinspect.layout

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maxinspect.Globals
import com.maxinspect.R
import com.maxinspect.models.ReceiptAdapter

class ReceiptListPane : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_list_pane)

        // Initialize the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.receiptRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the adapter with a click listener
        val adapter = ReceiptAdapter(Globals.receipts) { receipt ->
            // Handle the receipt click
            val intent = Intent(this, ReceiptPane::class.java)
            intent.putExtra("RECEIPT_ID", receipt.id)
            intent.putExtra("RECEIPT_DATE", receipt.date)
            intent.putExtra("RECEIPT_PRICE", receipt.price.toString())
            startActivity(intent)
            //Toast.makeText(this, "${receipt.toString()} clicked!", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter



    }

}