package com.funnco.fcmessenger.activity.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.funnco.fcmessenger.activity.login.LoginActivity
import com.funnco.fcmessenger.activity.main.MainActivity
import com.funnco.fcmessenger.common.retrofit.RetrofitObject
import com.funnco.fcmessenger.databinding.ActivitySplashBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        loginViaToken()
    }

    // Working with some sharedprefs over there
    fun loginViaToken(){
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        if (token == null){
            startLoginActivity()
        }
        else {
            RetrofitObject.authAPI.loginViaToken(token).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful && response.code() == 200){
                        Log.d(this.javaClass.name, "Successful token authorization")
                        startMainActivity()
                    } else {
                        startLoginActivity()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(this.javaClass.name, "Failed token authorization")
                    startLoginActivity()
                }

            })
        }
    }

    private fun startLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun startMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }
}