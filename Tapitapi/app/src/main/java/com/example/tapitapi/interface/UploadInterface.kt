package com.example.tapitapi.`interface`

import com.example.tapitapi.Res
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadInterface {
    @Multipart
    @POST("api/tapioka_searcher_data.php")
    fun upload(@Part image:MultipartBody.Part):Call<Res>
}