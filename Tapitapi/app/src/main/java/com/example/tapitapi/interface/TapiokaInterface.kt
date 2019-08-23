package com.example.tapitapi.`interface`

import com.example.tapitapi.Menu
import com.example.tapitapi.Store
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TapiokaInterface {
    @GET("/api/get_menu.php")
    fun getMenu(@Query("tag") tag:String):Call<Store>
}