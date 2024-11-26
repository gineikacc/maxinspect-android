package com.maxinspect.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maxinspect.R
import com.maxinspect.models.Receipt
import com.maxinspect.models.ReceiptAdapter
import java.text.SimpleDateFormat
import java.util.Date

class ReceiptListPane : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_list_pane)

        // Sample data for the RecyclerView
        val receipts = listOf(
            Receipt("Rec 1", 1451, Date()),
            Receipt("Rec 2", 565, Date()),
            Receipt("Rec 3", 1122, Date()),
            Receipt("Rec 4", 2168, Date()),
            Receipt("Rec 5", 123, Date()),
            Receipt("Rec 6", 123, Date()),
            Receipt("Rec 7", 123, Date()),
            Receipt("Rec 8", 123, Date()),
            Receipt("Rec 9", 123, Date()),
            Receipt("Rec 10", 123, Date()),
            Receipt("Rec 11", 123, Date()),
            Receipt("Rec 12", 123, Date()),
            Receipt("Rec 13", 123, Date()),
            Receipt("Rec 14", 123, Date()),
            Receipt("Rec 15", 123, Date()),
            Receipt("Rec 16", 123, Date()),
            Receipt("Rec 17", 123, Date()),
            Receipt("Rec 18", 123, Date()),
            Receipt("Rec 19", 123, Date()),
            Receipt("Rec 20", 123, Date()),
        )

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
            Toast.makeText(this, "${receipt.title} clicked!", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter



    }

}