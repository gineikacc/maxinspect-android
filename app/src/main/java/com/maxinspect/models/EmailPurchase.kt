package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class EmailPurchase(
    val checkName: String,
    val displayName: String,
    val price: Int,
    val amount: Float,
    val weight: Int,
    val calories: Float,
    val protein: Float,
    val fats: Float,
    val carbs: Float,
)