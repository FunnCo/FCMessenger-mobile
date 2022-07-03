package com.funnco.fcmessenger.common.retrofit.API

import com.funnco.fcmessenger.common.model.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserAPI {
    @PUT("/user/change/about")
    fun changeAbout(@Header("Authorization") token: String, @Body newInfo: UserModel): Call<Void>

    @Multipart
    @POST("/user/change/avatar")
    fun changeAvatar(@Header("Authorization") token: String, @Part image: MultipartBody.Part): Call<Void>

    @GET("user/info")
    fun getUserInfo(@Header("Authorization") token: String, @Query(value = "phone") phone: String?): Call<UserModel>


}