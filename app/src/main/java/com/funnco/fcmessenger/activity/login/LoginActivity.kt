package com.funnco.fcmessenger.activity.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.funnco.fcmessenger.activity.main.MainActivity
import com.funnco.fcmessenger.activity.register.RegisterActivity
import com.funnco.fcmessenger.common.model.TokenHolder
import com.funnco.fcmessenger.common.retrofit.RetrofitObject
import com.funnco.fcmessenger.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.loginBtnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginBtnLogin.setOnClickListener {
            val email = binding.loginEtxtEmail.text.toString()
            val password = binding.loginEtxtPassword.text.toString()

            val isEmailCorrect = email.matches(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$".toRegex()
            )

            if(!isEmailCorrect){
                showAlert("Пожалуйста, введите корректный адрес электронной почты")
                return@setOnClickListener
            }

            RetrofitObject.authAPI.loginByPassword(email, password).enqueue(object : Callback<TokenHolder>{
                override fun onResponse(call: Call<TokenHolder>, response: Response<TokenHolder>) {
                    if(response.isSuccessful && response.code() == 200){
                        Log.d(this.javaClass.name, "Successful authorization via password")
                        getSharedPreferences("settings", MODE_PRIVATE).edit().putString("token", response.body()!!.token!!).apply()
                        startMainActivity()
                    }
                    if(response.code() == 404){
                        Log.d(this.javaClass.name, "User not found")
                        showAlert("Введена не верная почта или пароль. Пожалуйста, проверьте данные, и введите их снова")
                    }
                }

                override fun onFailure(call: Call<TokenHolder>, t: Throwable) {
                    Log.d(this.javaClass.name, "Failed to authorize with password")
                    showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                }

            })

        }

    }

    private fun startMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun showAlert(message: String){
        AlertDialog.Builder(this)
            .setMessage(message)
            .setNeutralButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
    }
}