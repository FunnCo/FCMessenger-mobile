package com.funnco.fcmessenger.activity.newchat.ui.group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.funnco.fcmessenger.R
import com.funnco.fcmessenger.databinding.FragmentGroupChatCreationBinding


class GroupChatCreationFragment : Fragment() {

    private lateinit var binding: FragmentGroupChatCreationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupChatCreationBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

}