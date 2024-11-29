package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class EmailProduct(
    val name: String,
    val price: Int,
    val amount: Double,
)
