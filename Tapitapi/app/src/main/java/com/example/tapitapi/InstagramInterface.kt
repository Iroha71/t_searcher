package com.example.tapitapi

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface InstagramInterface {
    @GET("p/fA9uwTtkSN/media/?size=t")
    fun getImageUrl():Call<ResponseBody>
}