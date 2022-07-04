package com.funnco.fcmessenger.activity.newchat.ui.group

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.funnco.fcmessenger.common.model.ChatModel
import com.funnco.fcmessenger.common.model.CreateChatModel
import com.funnco.fcmessenger.common.model.MessageModel
import com.funnco.fcmessenger.common.model.UserModel
import com.funnco.fcmessenger.common.retrofit.RetrofitObject
import com.funnco.fcmessenger.common.utils.CurrentUser
import com.funnco.fcmessenger.databinding.FragmentGroupChatCreationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GroupChatCreationFragment : Fragment() {

    private lateinit var binding: FragmentGroupChatCreationBinding

    private var listOfKnownUsers: MutableList<UserModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupChatCreationBinding.inflate(layoutInflater,container, false)

        binding.groupCreationBtnCancel.setOnClickListener {
            activity!!.onBackPressed()
        }

        binding.groupCreationEtxtMessage.doOnTextChanged { text, start, before, count ->
            if(!binding.groupCreationEtxtChatName.text.isNullOrBlank() && !text!!.isBlank()){
                binding.groupCreationBtnStart.isEnabled = true
            }
        }

        binding.groupCreationEtxtChatName.doOnTextChanged { text, start, before, count ->
            if(!binding.groupCreationEtxtMessage.text.isNullOrBlank() && !text!!.isBlank()){
                binding.groupCreationBtnStart.isEnabled = true
            }
        }

        binding.groupCreationBtnStart.setOnClickListener {
            create()
        }

        init()

        return binding.root
    }

    private fun create(){
        val prefs = activity!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        val selectedUsers = (binding.groupCreationRecycler.adapter as UsersAdapter).listOfSelected.toList()

        if(selectedUsers.isEmpty()){
            showAlert("Пожалуйста, выбирите пользователей, с к оторыми вы хотите создать чат")
            return
        }

        val selectedPhones = mutableListOf<String>()
        for(user in selectedUsers){
            selectedPhones.add(user.phone!!)
        }

        val createdChatModel = CreateChatModel()
        createdChatModel.chatName = binding.groupCreationEtxtChatName.text.toString()
        createdChatModel.chatMembersPhones = selectedPhones.toList()

        RetrofitObject.chatAPI.createChat(token!!, createdChatModel).enqueue(object:Callback<ChatModel>{
            override fun onResponse(call: Call<ChatModel>, response: Response<ChatModel>) {
                if(response.isSuccessful && response.code() == 200){
                    postMessage(response.body()!!.chatId!!)
                }
            }

            override fun onFailure(call: Call<ChatModel>, t: Throwable) {
                Log.d(this.javaClass.simpleName, "Failed to authorize with password: ${t.message}")
                showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
            }

        })
    }

    private fun postMessage(chatId: String){
        val prefs = activity!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        val messageModel = MessageModel()
        messageModel.messageContent = binding.groupCreationEtxtMessage.text.toString()
        messageModel.chatId = chatId

        RetrofitObject.chatAPI.postMessage(token!!, messageModel).enqueue(object: Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    when(response.code()){
                        200 -> {
                            MainScope().launch(Dispatchers.Main) {
                                delay(500)
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

    private fun init(){
        val prefs = activity!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        RetrofitObject.chatAPI.getUserChats(token!!).enqueue(object: Callback<List<ChatModel>>{
            override fun onResponse(
                call: Call<List<ChatModel>>,
                response: Response<List<ChatModel>>
            ) {
                if(response.isSuccessful && response.code() == 200){
                    val result = response.body()!!

                    for(chat in result){
                        listOfKnownUsers.addAll(chat.chatMembers!!)
                    }
                    listOfKnownUsers = listOfKnownUsers.distinctBy { it.phone }.toMutableList()
                    if(listOfKnownUsers.isEmpty()){
                        return
                    }
                    val indexOfCurrentUser = listOfKnownUsers.indexOfFirst { it.phone == CurrentUser.model!!.phone!! }
                    listOfKnownUsers.removeAt(indexOfCurrentUser)
                    binding.groupCreationRecycler.adapter = UsersAdapter(listOfKnownUsers)
                } else {
                    Log.d(this.javaClass.simpleName, "Failed to authorize with password: ${response.code()}, ${response.message()}")
                    showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                }
            }

            override fun onFailure(call: Call<List<ChatModel>>, t: Throwable) {
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