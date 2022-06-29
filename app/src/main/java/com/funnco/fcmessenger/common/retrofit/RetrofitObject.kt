package com.funnco.fcmessenger.common.retrofit

import com.funnco.fcmessenger.common.retrofit.API.UserAuthAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://45.90.216.162")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userAuthAPI: UserAuthAPI by lazy {
        retrofit.create(UserAuthAPI::class.java)
    }
}