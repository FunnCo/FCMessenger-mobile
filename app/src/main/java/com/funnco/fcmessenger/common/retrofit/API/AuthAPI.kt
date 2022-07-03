package com.funnco.fcmessenger.common.retrofit.API

import com.funnco.fcmessenger.common.model.UserModel
import com.funnco.fcmessenger.common.model.TokenHolder
import retrofit2.Call
import retrofit2.http.*

interface AuthAPI {
    @GET("/user/login/token")
    fun loginViaToken(@Header("Authorization") token: String): Call<Void>

    @GET("/user/login")
    fun loginByPassword(@Query("email") email: String, @Query("password") password: String): Call<TokenHolder>

    @POST("/user/register")
    fun register(@Body body: UserModel): Call<Void>

}