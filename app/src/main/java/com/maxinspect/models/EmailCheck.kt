package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class EmailCheck(
    var owner: String,
    val checkID: String,
    val products: Array<EmailProduct>,
    val dateIssued: String
)
