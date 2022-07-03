package com.funnco.fcmessenger.activity.main.ui.chatlist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.funnco.fcmessenger.activity.chat.ChatActivity
import com.funnco.fcmessenger.common.model.ChatModel
import com.funnco.fcmessenger.databinding.ItemChatBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ChatsRecyclerAdapter(var listOfItems: List<ChatModel>): RecyclerView.Adapter<ChatsRecyclerAdapter.ChatViewHolder>() {
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        lateinit var binding: ItemChatBinding

        fun bind (item: ChatModel){
            binding = ItemChatBinding.bind(itemView)
            val filepath: String = if(item.isChatPrivate!!){
                "http://45.90.216.162/resources/image/user/${item.avatarFilepath}"
            } else {
                "http://45.90.216.162/resources/image/chat/${item.avatarFilepath}"
            }
            Picasso.get()
                .load(filepath)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(binding.itemChatAvatar)
            binding.itemChatTxtName.text = item.chatName

            val dateFormat = SimpleDateFormat("HH:mm")
            val date = Date(item.lastMessage!!.creationTime!!.time)

            binding.itemChatTxtLastTime.text = dateFormat.format(date)
            binding.itemChatTxtLastMessage.text = item.lastMessage!!.messageContent

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ChatActivity::class.java)
                intent.putExtra("chatId", item.chatId!!)
                intent.putExtra("chatName", item.chatName!!)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false).root)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(listOfItems[position])
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }
}