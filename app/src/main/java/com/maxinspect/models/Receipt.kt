package com.maxinspect.models

import kotlinx.serialization.Serializable

@Serializable
data class Receipt(val price:Int, val date: String, val id : Int)
