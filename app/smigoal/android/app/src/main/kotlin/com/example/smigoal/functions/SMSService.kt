package com.example.smigoal.functions

import com.example.smigoal.BuildConfig
import com.example.smigoal.models.Message
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

const val BASE_URL = BuildConfig.BASE_URL
interface SMSService {
    @POST
    fun requestServer(@Url url: String, @Body message: Message): Call<String>
}