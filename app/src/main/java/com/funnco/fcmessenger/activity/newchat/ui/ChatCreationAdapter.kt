package com.funnco.fcmessenger.activity.newchat.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChatCreationAdapter(val listOfItems: List<Fragment>, fragmentActivity: AppCompatActivity
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return listOfItems.size
    }

    override fun createFragment(position: Int): Fragment {
        return listOfItems[position]
    }

}