package com.funnco.fcmessenger.common.retrofit

import com.funnco.fcmessenger.common.retrofit.API.AuthAPI
import com.funnco.fcmessenger.common.retrofit.API.ChatAPI
import com.funnco.fcmessenger.common.retrofit.API.UserAPI
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*

object RetrofitObject {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://45.90.216.162")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().registerTypeAdapter(Date::class.java, object: JsonDeserializer<Date>{
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): Date {
                return Date(json!!.asJsonPrimitive.asLong);
            }

        }).create()))
        .build()

    val authAPI: AuthAPI by lazy {
        retrofit.create(AuthAPI::class.java)
    }

    val userAPI: UserAPI by lazy {
        retrofit.create(UserAPI::class.java)
    }

    val chatAPI: ChatAPI by lazy {
        retrofit.create(ChatAPI::class.java)
    }
}