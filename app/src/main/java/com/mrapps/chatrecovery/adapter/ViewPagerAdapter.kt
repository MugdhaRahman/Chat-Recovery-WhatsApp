package com.mrapps.chatrecovery.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mrapps.chatrecovery.fragment.FragmentCalls
import com.mrapps.chatrecovery.fragment.FragmentHome
import com.mrapps.chatrecovery.fragment.FragmentMedia

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentHome()
            1 -> FragmentCalls()
            2 -> FragmentMedia()
            else -> FragmentHome()
        }
    }

}