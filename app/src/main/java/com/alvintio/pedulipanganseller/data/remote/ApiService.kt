package com.alvintio.pedulipanganseller.data.remote

import com.alvintio.pedulipanganseller.model.Login
import com.alvintio.pedulipanganseller.model.Product
import com.alvintio.pedulipanganseller.model.Register
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @POST("/insertproduct")
    @Multipart
    fun uploadProduct(
        @Part attachment: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("detail") description: RequestBody,
        @Part("date") date: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody
    ): Call<Product>

    @GET("/getproducts")
    fun getProducts(): Call<List<Product>>

    @GET("/getproduct/{id}")
    fun getProductById(@Path("id") productId: String): Call<Product>
}
