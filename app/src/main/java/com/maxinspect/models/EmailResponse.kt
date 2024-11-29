package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class EmailResponse(
    val responseMeme: String,
    val someNumber: Int,
)
