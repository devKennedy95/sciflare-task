package com.task.sciflare.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.task.sciflare.ui.fragment.ReceiverFragment
import com.task.sciflare.ui.fragment.SenderFragment

class HomeAdapter(
    activity: FragmentActivity,
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> SenderFragment()
        else -> ReceiverFragment()
    }
}


