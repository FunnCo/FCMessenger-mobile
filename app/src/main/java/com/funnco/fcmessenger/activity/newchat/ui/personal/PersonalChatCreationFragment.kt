package com.funnco.fcmessenger.activity.newchat.ui.personal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.funnco.fcmessenger.R
import com.funnco.fcmessenger.common.model.ChatModel
import com.funnco.fcmessenger.common.model.CreateChatModel
import com.funnco.fcmessenger.common.model.MessageModel
import com.funnco.fcmessenger.common.model.UserModel
import com.funnco.fcmessenger.common.retrofit.RetrofitObject
import com.funnco.fcmessenger.databinding.FragmentPersonalChatCreationBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PersonalChatCreationFragment : Fragment() {

    private lateinit var binding: FragmentPersonalChatCreationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPersonalChatCreationBinding.inflate(layoutInflater, container, false)

        binding.personalCreationBtnCancel.setOnClickListener {
            activity!!.onBackPressed()
        }

        binding.personalCreationEtxtSearched.doOnTextChanged { text, _, _, _ ->
            binding.personalCreationBtnStart.isEnabled = !text.isNullOrBlank()
        }

        binding.personalCreationBtnStart.setOnClickListener {
            startChat()
        }

        binding.personalCreationBtnFind.setOnClickListener {
            binding.personalCreationUserCard.visibility = View.GONE
            binding.personalCreationTxtHint.visibility = View.GONE
            binding.personalCreationProgress.visibility = View.VISIBLE

            val phone = binding.personalCreationEtxtSearched.text.toString()

            // Проверка на соотвествие почты и телефона паттерну
            val isPhoneCorrect = phone.matches("^\\+7\\d{10}$".toRegex())
            if(!isPhoneCorrect){
                binding.personalCreationProgress.visibility = View.GONE
                binding.personalCreationTxtHint.visibility = View.VISIBLE
                binding.personalCreationTxtHint.text = "Пожалуйста, введите корректный номер телефона"
                return@setOnClickListener
            }

            val prefs = activity!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
            val token = prefs.getString("token", null)

            RetrofitObject.userAPI.getUserInfo(token!!, phone).enqueue(object : Callback<UserModel>{
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if(response.isSuccessful){

                        val user = response.body()!!

                        when(response.code()){
                            200 -> {
                                binding.personalCreationProgress.visibility = View.GONE
                                binding.personalCreationUserCard.visibility = View.VISIBLE

                                binding.personalCreationTxtUserName.text = "${user.lastname} ${user.firstname} ${user.patronymic}"
                                Picasso.get()
                                    .load("http://45.90.216.162/resources/image/user/${user.avatarFilename}")
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .into(binding.personalCreationImgUserAvatar)
                                binding.personalCreationTxtUserEmail.text = user.email
                                binding.personalCreationTxtUserPhone.text = user.phone
                            }
                            404 -> {
                                binding.personalCreationProgress.visibility = View.GONE
                                binding.personalCreationTxtHint.visibility = View.VISIBLE
                                binding.personalCreationTxtHint.text = "Пользователь с введеным номером телефона не найден"
                            }
                            else -> {
                                binding.personalCreationProgress.visibility = View.GONE
                                binding.personalCreationTxtHint.visibility = View.VISIBLE
                                binding.personalCreationTxtHint.text = "Неизвестная ошибка сервера, попробуйте повторить запрос"
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    binding.personalCreationProgress.visibility = View.GONE
                    binding.personalCreationTxtHint.visibility = View.VISIBLE
                    binding.personalCreationTxtHint.text = "Неизвестная ошибка сервера, попробуйте повторить запрос"
                }

            })


        }

        return binding.root
    }

    private fun startChat(){
        val prefs = activity!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        val chatModel = CreateChatModel()
        chatModel.chatName = null
        chatModel.chatMembersPhones = listOf(binding.personalCreationTxtUserPhone.text.toString())

        if(binding.personalCreationEtxtMessage.text.isNullOrBlank()){
            showAlert("Пожалуйста, введите первое сообщение")
            return
        }

        RetrofitObject.chatAPI.createChat(token!!, chatModel).enqueue(object: Callback<ChatModel>{
            override fun onResponse(call: Call<ChatModel>, response: Response<ChatModel>) {
                if(response.isSuccessful){
                    when(response.code()){
                        200 -> {
                            sendFirstMessage(response.body()!!.chatId!!)
                        }
                        else -> {
                            Log.d(this.javaClass.simpleName, "Failed to authorize with password")
                            showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ChatModel>, t: Throwable) {
                Log.d(this.javaClass.simpleName, "Failed to authorize with password: ${t.stackTrace}")
                showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
            }

        })
    }

    private fun sendFirstMessage(chatId: String){
        val prefs = activity!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        val messageModel = MessageModel()
        messageModel.messageContent = binding.personalCreationEtxtMessage.text.toString()
        messageModel.chatId = chatId

        RetrofitObject.chatAPI.postMessage(token!!, messageModel).enqueue(object: Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    when(response.code()){
                        200 -> {
                            MainScope().launch(Dispatchers.IO) {
                                delay(250)
                                activity!!.onBackPressed()
                            }
                        }
                        else -> {
                            Log.d(this.javaClass.simpleName, "Failed to authorize with password: ${response.code()}")
                            showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                        }
                    }
                }

            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(this.javaClass.simpleName, "Failed to authorize with password: ${t.stackTrace}")
                showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
            }

        })
    }

    private fun showAlert(message: String){
        AlertDialog.Builder(context!!)
            .setMessage(message)
            .setNeutralButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
    }

}