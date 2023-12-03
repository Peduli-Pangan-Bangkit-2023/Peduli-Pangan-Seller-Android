package com.alvintio.pedulipanganseller.model

import com.google.gson.annotations.SerializedName

data class Product (
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)