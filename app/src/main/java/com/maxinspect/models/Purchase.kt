package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class Purchase(
    val product_id: String,
    val receipt_id: Int,
    val amount: Float,
    val cost: Int,
)
