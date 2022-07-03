package com.funnco.fcmessenger.activity.newchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.funnco.fcmessenger.activity.newchat.ui.ChatCreationAdapter
import com.funnco.fcmessenger.activity.newchat.ui.group.GroupChatCreationFragment
import com.funnco.fcmessenger.activity.newchat.ui.personal.PersonalChatCreationFragment
import com.funnco.fcmessenger.databinding.ActivityNewChatBinding
import com.google.android.material.tabs.TabLayoutMediator

class NewChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newchatViewpager.adapter = ChatCreationAdapter(listOf(PersonalChatCreationFragment(), GroupChatCreationFragment()), this)
        TabLayoutMediator(binding.newchatTabLayout, binding.newchatViewpager) { tab, position ->
            when (position){
                0 -> tab.text = "Личный"
                1 -> tab.text = "Групповой"
            }
        }.attach()

        supportActionBar?.title = "Создать чат"


    }
}