package com.maxinspect

import android.app.Application
import com.maxinspect.models.EmailPurchase
import com.maxinspect.models.Product
import com.maxinspect.models.Purchase
import com.maxinspect.models.Receipt

object Globals {
    var receipts = ArrayList<Receipt>()
    var products = ArrayList<Product>()
    var purchases = ArrayList<Purchase>()
    var syncQueue = ArrayList<EmailPurchase>()
}

class MaxInspect : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}