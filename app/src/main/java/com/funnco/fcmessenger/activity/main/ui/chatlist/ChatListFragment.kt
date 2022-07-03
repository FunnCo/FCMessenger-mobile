package com.funnco.fcmessenger.activity.main.ui.chatlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.funnco.fcmessenger.activity.newchat.NewChatActivity
import com.funnco.fcmessenger.common.model.ChatModel
import com.funnco.fcmessenger.common.retrofit.RetrofitObject
import com.funnco.fcmessenger.databinding.FragmentChatlistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatListFragment : Fragment() {

    private lateinit var binding: FragmentChatlistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatlistBinding.inflate(inflater, container, false)

        initRecycler()

        binding.chatlistBtnNew.setOnClickListener {
            startActivity(Intent(context, NewChatActivity::class.java))
        }

        return binding.root
    }

    private fun initRecycler(){
        val prefs = activity!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        RetrofitObject.chatAPI.getUserChats(token!!).enqueue(object: Callback<List<ChatModel>>{
            override fun onResponse(
                call: Call<List<ChatModel>>,
                response: Response<List<ChatModel>>
            ) {
                if(response.isSuccessful){
                    when(response.code()){
                        200 -> {
                            binding.chatlistRecycler.adapter = ChatsRecyclerAdapter(response.body()!!.sortedByDescending { it.lastMessage!!.creationTime })
                        }
                        else -> {
                            Log.d(this.javaClass.simpleName, "Failed to authorize with password: ${response.code()}")
                            showAlert("Возникла неожиданная проблема авторизации. Для получения подробной информации, свяжитесь с разработчиком API")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ChatModel>>, t: Throwable) {
                Log.d(this.javaClass.simpleName, "Failed to get chats: ${t.cause}")
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