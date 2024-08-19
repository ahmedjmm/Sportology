package com.dev.goalpulse.views.adapters.newsAdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dev.goalpulse.views.fragments.newsFragments.EverythingFragment
import com.dev.goalpulse.views.fragments.newsFragments.TopHeadlinesFragment

class NewsViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return if(position == 0) EverythingFragment()
        else TopHeadlinesFragment()
    }

    override fun getItemCount() = 2
}