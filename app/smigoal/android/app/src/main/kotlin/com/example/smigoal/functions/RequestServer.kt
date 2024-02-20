package com.example.smigoal.functions

import android.content.Context
import android.util.Log
import com.example.smigoal.BuildConfig
import com.example.smigoal.db.MessageEntity
import com.example.smigoal.models.Message
import com.example.smigoal.models.SMSServiceData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RequestServer {
    val BASE_URL = BuildConfig.BASE_URL

    val okHttpClient = OkHttpClient.Builder()
        // 연결 타임아웃 시간 설정
        .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 시간을 30초로 설정
        // 읽기 타임아웃 시간 설정
        .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임아웃 시간을 30초로 설정
        // 쓰기 타임아웃 시간 설정
        .writeTimeout(30, TimeUnit.SECONDS) // 쓰기 타임아웃 시간을 30초로 설정
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val smsService = retrofit.create(SMSService::class.java)

    fun getServerRequest(context: Context, url: String, message: Message, sender: String, containsUrl: Boolean, timestamp: Long) {
        smsService.requestServer(url, message).enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val body = response.body()!!
                Log.i("test", body)
                val entity = when(body) {
                    "ham" -> MessageEntity(message.url, message.message, sender, containsUrl, timestamp, false)
                    else -> MessageEntity(message.url, message.message, sender, containsUrl, timestamp, true)
                }
                Log.i("test", entity.toString())
                SMSServiceData.setResponseFromServer(context, entity)
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("test", t.toString())
            }
        })
    }
}