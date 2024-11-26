package com.maxinspect.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maxinspect.R

class ProductAdapter(
    private val products: List<Product>,
    private val onProductClicked: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // ViewHolder inner class
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ADAPT FOr PRODUCT LATER
        val priceView: TextView = itemView.findViewById(R.id.priceText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.check_list_entry, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = products[position]
        holder.priceView.text = (item.price/100.0).toString() + " Eur"
        // Set up click listener
        holder.itemView.setOnClickListener {
            onProductClicked(item)
        }
    }

    override fun getItemCount() = products.size
}
