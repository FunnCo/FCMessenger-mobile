package com.funnco.fcmessenger.activity.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.funnco.fcmessenger.common.model.MessageModel
import com.funnco.fcmessenger.databinding.ItemMessageMyBinding
import com.funnco.fcmessenger.databinding.ItemMessageOtherBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(var listOfItems: List<MessageModel>, var currentUserPhone: String):
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: MessageModel, currentUserPhone: String){

            val dateFormat = SimpleDateFormat("HH:mm")
            val date = Date(item.creationTime!!.time)

            if(currentUserPhone == item.author!!.phone){
                val binding = ItemMessageMyBinding.bind(itemView)
                binding.itemMessageMyContent.text = item.messageContent
                binding.itemMessageMyTime.text = dateFormat.format(date)
            } else {
                val binding = ItemMessageOtherBinding.bind(itemView)
                binding.itemMessageOtherContent.text = item.messageContent
                binding.itemMessageOtherTime.text = dateFormat.format(date)
                binding.itemMessageOtherAuthor.text = item.author!!.firstname + " " + item.author!!.lastname
                Picasso.get()
                    .load( "http://45.90.216.162/resources/image/user/${item.author!!.avatarFilename}")
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding.itemMessageOtherImgAvatar)
            }
        }
    }

    fun updateMessages(newItems: List<MessageModel>){
        listOfItems = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when(viewType){
            0 -> MessageViewHolder(ItemMessageMyBinding.inflate(LayoutInflater.from(parent.context), parent, false).root)
            else -> MessageViewHolder(ItemMessageOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false).root)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(currentUserPhone == listOfItems[position].author!!.phone!!){
            0
        } else 1
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(listOfItems[position], currentUserPhone)
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

}