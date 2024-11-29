package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class EmailCheck(
    val checkID: String,
    val products: Array<EmailProduct>,
)
