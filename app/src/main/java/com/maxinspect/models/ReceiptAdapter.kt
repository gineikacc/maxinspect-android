package com.maxinspect.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maxinspect.R
import java.text.SimpleDateFormat

class ReceiptAdapter(
    private val receipts: List<Receipt>,
    private val onReceiptClicked: (Receipt) -> Unit
) : RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder>() {

    // ViewHolder inner class
    class ReceiptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateView: TextView = itemView.findViewById(R.id.dateText)
        val priceView: TextView = itemView.findViewById(R.id.priceText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.check_list_entry, parent, false)
        return ReceiptViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val item = receipts[position]
        holder.dateView.text = SimpleDateFormat("yy/MM/dd hh:mm").format(item.date).toString()
        holder.priceView.text = (item.price/100.0).toString() + " Eur"
        // Set up click listener
        holder.itemView.setOnClickListener {
            onReceiptClicked(item)
        }
    }

    override fun getItemCount() = receipts.size
}
