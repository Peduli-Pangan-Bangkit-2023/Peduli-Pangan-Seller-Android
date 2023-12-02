package com.alvintio.pedulipanganseller.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name") val name: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("userId") val userId: String = ""
)
