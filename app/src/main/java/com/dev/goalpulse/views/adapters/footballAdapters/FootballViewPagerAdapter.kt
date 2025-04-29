package com.dev.goalpulse.views.adapters.footballAdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dev.goalpulse.Shared
import com.dev.goalpulse.views.fragments.DynamicLeagueFragment


class FootBallViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return DynamicLeagueFragment().apply {
            leagueOrder = position
        }
    }

    override fun getItemCount() = Shared.LEAGUES_IDS.size
}