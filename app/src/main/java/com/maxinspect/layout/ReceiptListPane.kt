package com.maxinspect.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maxinspect.Globals
import com.maxinspect.R
import com.maxinspect.models.ReceiptAdapter
import java.text.SimpleDateFormat

class ReceiptListPane : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_list_pane)

        // Sample data for the RecyclerView
        val receipts = Globals.receipts

        // Initialize the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.receiptRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the adapter with a click listener
        val adapter = ReceiptAdapter(receipts) { receipt ->
            // Handle the receipt click
            val intent = Intent(this, ReceiptPane::class.java)
            intent.putExtra("RECEIPT_DATE", SimpleDateFormat("yy/MM/dd hh:mm").format(receipt.date).toString())
            intent.putExtra("RECEIPT_PRICE", receipt.price.toString())
            startActivity(intent)
            Toast.makeText(this, "${receipt.toString()} clicked!", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter



    }

}