package com.funnco.fcmessenger.activity.main

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.funnco.fcmessenger.R
import com.funnco.fcmessenger.common.model.UserModel
import com.funnco.fcmessenger.common.retrofit.RetrofitObject
import com.funnco.fcmessenger.common.utils.CurrentUser
import com.funnco.fcmessenger.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_chats, R.id.navigation_settings
            )
        )

        getCurrentUser()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun getCurrentUser(){
        val prefs = getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        RetrofitObject.userAPI.getUserInfo(token!!, null).enqueue(object: Callback<UserModel>{
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if(response.isSuccessful && response.code() == 200){
                    CurrentUser.model = response.body()!!
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
            }

        })
    }
}