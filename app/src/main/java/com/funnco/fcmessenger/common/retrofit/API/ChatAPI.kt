package com.funnco.fcmessenger.common.retrofit.API

import com.funnco.fcmessenger.common.model.ChatModel
import com.funnco.fcmessenger.common.model.CreateChatModel
import com.funnco.fcmessenger.common.model.MessageModel
import retrofit2.Call
import retrofit2.http.*


interface ChatAPI {
    @POST("/chat/create")
    fun createChat(@Header("Authorization") token: String, @Body chat: CreateChatModel): Call<ChatModel>

    @POST("/chat/post/message")
    fun postMessage(@Header("Authorization") token: String, @Body message: MessageModel): Call<Void>

    @GET("/chat/my")
    fun getUserChats(@Header("Authorization") token: String): Call<List<ChatModel>>

    @GET("/chat/get/messages")
    fun getMessages(@Header("Authorization") token: String, @Query("chatId") chatId: String): Call<List<MessageModel>>

}