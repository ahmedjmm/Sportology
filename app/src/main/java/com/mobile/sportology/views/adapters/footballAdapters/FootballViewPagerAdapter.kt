package com.mobile.sportology.views.adapters.footballAdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.sportology.Shared
import com.mobile.sportology.views.fragments.DynamicLeagueFragment

class FootBallViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        val dynamicLeagueFragment = DynamicLeagueFragment()
        dynamicLeagueFragment.leagueOrder = position
        return dynamicLeagueFragment
    }

    override fun getItemCount() = Shared.LEAGUES_IDS.size
}