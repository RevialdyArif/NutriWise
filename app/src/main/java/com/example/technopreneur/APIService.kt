package com.example.technopreneur

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("product/{barcode}.json")
    fun getProductDetails(@Path("barcode") barcode: String): Call<ProductResponse>
}
