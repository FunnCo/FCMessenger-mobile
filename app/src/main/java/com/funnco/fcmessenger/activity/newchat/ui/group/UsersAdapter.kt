package com.funnco.fcmessenger.activity.newchat.ui.group

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.funnco.fcmessenger.common.model.UserModel
import com.funnco.fcmessenger.databinding.ItemNewChatMemberBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class UsersAdapter(val listOfItems: List<UserModel>) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    val listOfSelected: MutableList<UserModel> = mutableListOf()

    class UserViewHolder(itemView: View, val adapter: UsersAdapter): RecyclerView.ViewHolder(itemView){

        lateinit var binding: ItemNewChatMemberBinding
        private var isSelected = false

        fun bind(item: UserModel){
            binding = ItemNewChatMemberBinding.bind(itemView)

            binding.itemUserTxtName.text = item.firstname + " " + item.lastname
            Picasso.get()
                .load( "http://45.90.216.162/resources/image/user/${item.avatarFilename}")
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(binding.itemUserAvatar)

            binding.root.setOnClickListener {
                if(!isSelected){
                    isSelected = true
                    binding.root.setBackgroundColor(Color.parseColor("#B3E5FC"))
                    adapter.listOfSelected.add(item)
                } else {
                    isSelected = false
                    binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    adapter.listOfSelected.remove(item)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(ItemNewChatMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false).root, this)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(listOfItems[position])
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }
}