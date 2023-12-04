package com.alvintio.pedulipanganseller.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val attachment: String,
    val detail: String,
)
