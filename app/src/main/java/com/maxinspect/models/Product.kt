package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val checkName: String,
    val displayName: String,
    val price: Int,
    val weight: Int,
    val amount: Float,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fats: Float
)
