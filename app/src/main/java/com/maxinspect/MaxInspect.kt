package com.maxinspect

import android.app.Application
import com.maxinspect.models.EmailPurchase
import com.maxinspect.models.Product
import com.maxinspect.models.Purchase
import com.maxinspect.models.Receipt

object Globals {
    var userID = ""
    var receipts = ArrayList<Receipt>()
    var products = ArrayList<Product>()
    var purchases = ArrayList<Purchase>()
    var syncQueue = ArrayList<EmailPurchase>()
    var moderatorUserIDs = ArrayList<String>()
}

class MaxInspect : Application() {
    override fun onCreate() {
        super.onCreate()
        Globals.moderatorUserIDs.add("airolen11@gmail.com")
    }
}