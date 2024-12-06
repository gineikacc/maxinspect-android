package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class EmailPurchase(
    val checkID: Int,
    val productID: String,
    val cost: Int,
    val amount: Float
)
