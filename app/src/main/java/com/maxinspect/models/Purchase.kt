package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class Purchase(
    val product: Product,
    val receipt: Receipt,
    val amount: Float,
    val cost: Int,
)
