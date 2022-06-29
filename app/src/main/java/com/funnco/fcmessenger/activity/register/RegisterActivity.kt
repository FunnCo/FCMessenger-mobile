package com.funnco.fcmessenger.activity.register

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.funnco.fcmessenger.activity.login.LoginActivity
import com.funnco.fcmessenger.activity.main.MainActivity
import com.funnco.fcmessenger.common.model.RegisterBody
import com.funnco.fcmessenger.common.model.TokenHolder
import com.funnco.fcmessenger.common.retrofit.RetrofitObject
import com.funnco.fcmessenger.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.registerBtnLogin.setOnClickListener {
            onBackPressed()
        }

        binding.registerBtnRegister.setOnClickListener {

            val registerBody = RegisterBody()
            registerBody.email = binding.registerEtxtEmail.text.toString()
            registerBody.phone = binding.registerEtxtPhone.text.toString()
            registerBody.firstname = binding.registerEtxtFirstname.text.toString()
            registerBody.lastname = binding.registerEtxtLastname.text.toString()
            registerBody.patronymic = binding.registerEtxtPatronymic.text.toString()
            registerBody.password = binding.registerEtxtPassword.text.toString()
            val repeatedPassword = binding.registerEtxtRepeatPassword.text.toString()

            // Проверка что все поля заполнены
            if (registerBody.email.isNullOrBlank() ||
                registerBody.phone.isNullOrBlank() ||
                registerBody.firstname.isNullOrBlank() ||
                registerBody.lastname.isNullOrBlank() ||
                registerBody.password.isNullOrBlank() ||
                repeatedPassword.isBlank()
            ) {
                showAlert("Пожалуйста, заполните все поля (отчество указывать не обязательно).")
                return@setOnClickListener
            }

            // Проверка на соотвествие почты и телефона паттерну
            val isPhoneCorrect = registerBody.phone!!.matches("^\\+7\\d{10}$".toRegex())
            if(!isPhoneCorrect){
                showAlert("Пожалуйста, введите корректный номер телефона в следующем формате: +71234567890")
                return@setOnClickListener
            }

            val isEmailCorrect = registerBody.email!!.matches(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$".toRegex()
            )
            if(!isEmailCorrect){
                showAlert("Пожалуйста, введите корректный адрес электронной почты")
                return@setOnClickListener
            }

            // Проверка на совпадение паролей
            if(repeatedPassword != registerBody.password){
                showAlert("Введенные пароли не совпадают")
                return@setOnClickListener
            }

            // Отправка запроса на регистрацию на сервер
            RetrofitObject.userAuthAPI.register(registerBody).enqueue(object: Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d(this.javaClass.name, "Successful registration")
                    getTokenFromServer(registerBody.email!!, registerBody.password!!)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(this.javaClass.name, "Failed to register")
                    showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                }

            })
        }
    }

    private fun getTokenFromServer(email: String, password: String){
        RetrofitObject.userAuthAPI.loginByPassword(email, password).enqueue(object : Callback<TokenHolder>{
            override fun onResponse(call: Call<TokenHolder>, response: Response<TokenHolder>) {
                if(response.isSuccessful && response.code() == 200){
                    Log.d(this.javaClass.name, "Successful authorization via password")
                    getSharedPreferences("settings", MODE_PRIVATE).edit().putString("token", response.body()!!.token!!).apply()
                    startMainActivity()
                }
            }

            override fun onFailure(call: Call<TokenHolder>, t: Throwable) {
                Log.d(this.javaClass.name, "Failed to authorize with password")
                showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
            }
        })
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