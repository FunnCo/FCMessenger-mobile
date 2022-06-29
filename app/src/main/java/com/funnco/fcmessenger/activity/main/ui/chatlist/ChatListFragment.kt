package com.funnco.fcmessenger.activity.main.ui.chatlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.funnco.fcmessenger.databinding.FragmentChatlistBinding

class ChatListFragment : Fragment() {

    private lateinit var binding: FragmentChatlistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatlistBinding.inflate(inflater, container, false)
        return binding.root
    }
}