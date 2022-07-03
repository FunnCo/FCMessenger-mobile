package com.funnco.fcmessenger.activity.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.funnco.fcmessenger.activity.main.MainActivity
import com.funnco.fcmessenger.common.model.MessageModel
import com.funnco.fcmessenger.common.model.UserModel
import com.funnco.fcmessenger.common.retrofit.RetrofitObject
import com.funnco.fcmessenger.databinding.ActivityChatBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Intent передает chatId и chatName

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)

        binding.chatMessageRecycler.adapter = MessageAdapter(emptyList(), "")

        val chatName = intent.getStringExtra("chatName")
        supportActionBar?.title = chatName

        binding.chatBtnSend.setOnClickListener {
            postNewMessage()
        }

        setContentView(binding.root)

        init()

    }

    private lateinit var phone: String

    private fun startUpdateCycle(){
        MainScope().launch(Dispatchers.Main) {
            while (true){

                delay(500)
            }
        }
    }

    private fun init() {
        val prefs = getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        RetrofitObject.userAPI.getUserInfo(token!!, null).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                when (response.code()) {
                    200 -> {
                        phone = response.body()!!.phone!!
                        initRecycler(intent.getStringExtra("chatId")!!, phone)
                    }
                    else -> {
                        Log.d(
                            this.javaClass.simpleName,
                            "Failed to authorize with password: ${response.code()}"
                        )
                        showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                    }
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.d(this.javaClass.simpleName, "Failed to get chats: ${t.cause}")
                showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
            }
        })
    }

    fun initRecycler(chatId: String, currentPhone: String) {
        val prefs = getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        RetrofitObject.chatAPI.getMessages(token!!, chatId)
            .enqueue(object : Callback<List<MessageModel>> {
                override fun onResponse(
                    call: Call<List<MessageModel>>,
                    response: Response<List<MessageModel>>
                ) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                (binding.chatMessageRecycler.adapter as MessageAdapter).currentUserPhone = currentPhone
                                (binding.chatMessageRecycler.adapter as MessageAdapter).updateMessages(
                                    response.body()!!.reversed()
                                )
                            }
                            else -> {
                                Log.d(
                                    this.javaClass.simpleName,
                                    "Failed to authorize with password: ${response.code()}"
                                )
                                showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<MessageModel>>, t: Throwable) {
                    Log.d(this.javaClass.simpleName, "Failed to get chats: ${t.cause}")
                    showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                }

            })
    }


    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setNeutralButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
    }

    private fun postNewMessage() {
        val prefs = getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        if (binding.chatEtxtMessage.text.isNullOrBlank()) {
            return
        }

        val messageModel = MessageModel()
        messageModel.messageContent = binding.chatEtxtMessage.text.toString()
        messageModel.chatId = intent.getStringExtra("chatId")!!

        RetrofitObject.chatAPI.postMessage(token!!, messageModel).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> {
                        initRecycler(intent.getStringExtra("chatId")!!, phone)
                        binding.chatEtxtMessage.setText("")
                    }
                    else -> {
                        Log.d(
                            this.javaClass.simpleName,
                            "Failed to authorize with password: ${response.code()}"
                        )
                        showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(this.javaClass.simpleName, "Failed to get chats: ${t.cause}")
                showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
            }

        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}