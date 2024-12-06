package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class EmailReceipt(
    var owner: String,
    val checkID: String,
    val dateIssued: String,
    val cost: Int,
    val purchases: Array<EmailPurchase>,
)
