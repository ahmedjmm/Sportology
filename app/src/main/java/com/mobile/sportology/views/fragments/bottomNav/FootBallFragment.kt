package com.mobile.sportology.views.fragments.bottomNav

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.tabs.TabLayout
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.Shared
import com.mobile.sportology.databinding.TabItemBinding
import com.mobile.sportology.servicesAndUtilities.DateTimeUtils
import com.mobile.sportology.viewModels.FootBallViewModel
import com.mobile.sportology.views.activities.FavoritesActivity
import com.mobile.sportology.views.activities.MainActivity
import com.mobile.sportology.views.adapters.footballAdapters.FootBallViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FootBallFragment : Fragment(R.layout.fragment_football) {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var liveMatchesSwitch: MaterialSwitch
    private lateinit var searchButton: ImageButton
    private lateinit var favoritesButton: ImageButton
    private lateinit var extendedFab: ExtendedFloatingActionButton
    private lateinit var footBallViewModel: FootBallViewModel
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DateTimeUtils.timeFormat = sharedPreferences.getString(
            resources.getString(R.string.selected_time_format), "12 HS")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        footBallViewModel = (activity as MainActivity).footBallViewModel
        return inflater.inflate(R.layout.fragment_football, container ,false)
    }

    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        viewPager.apply {
            adapter = FootBallViewPagerAdapter(childFragmentManager, lifecycle)
            isUserInputEnabled = false
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })
        }

        lifecycleScope.launch {
            if (Shared.isConnected) {
                for (index in Shared.LEAGUES_IDS.indices) {
                    val responseState = footBallViewModel.getLeague(id = Shared.LEAGUES_IDS[index])
                    responseState.apply {
                        when(this) {
                            is ResponseState.Success -> {
                                val tabBinding = TabItemBinding.inflate(layoutInflater)
                                tabBinding.league = this.data?.body()?.response?.get(0)?.league
                                val tab = tabLayout.newTab().setCustomView(tabBinding.root)
                                tabLayout.addTab(tab)
                                val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in)
                                tab.customView?.startAnimation(animation)
                            }
                            is ResponseState.Loading -> {}
                            is ResponseState.Error -> {}
                        }
                    }
                }
            }
        }
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        liveMatchesSwitch.setOnCheckedChangeListener { _, isChecked ->
            Shared.isLiveMatches = isChecked
            val leagues = footBallViewModel.leaguesLiveData.value
            if(isChecked) {
                lifecycleScope.launch(Dispatchers.IO) {
                    for (index in Shared.LEAGUES_IDS.indices) {
                        leagues?.data?.response?.get(0)?.seasons?.get(0)?.year?.let {
                            footBallViewModel.getLeagueMatches(
                                leagueId = Shared.LEAGUES_IDS[index],
                                season = it,
                                liveMatches = resources.getString(R.string.live_matches),
                                leagueOrder = index
                            )
                        }
                    }
                }
            }
            else {
                lifecycleScope.launch(Dispatchers.IO) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        for (index in Shared.LEAGUES_IDS.indices) {
                            leagues?.data?.response?.get(0)?.seasons?.get(0)?.year?.let {
                                footBallViewModel.getLeagueMatches(
                                    leagueId = Shared.LEAGUES_IDS[index],
                                    season = it,
                                    liveMatches = null,
                                    leagueOrder = index
                                )
                            }
                        }
                    }
                }
            }
        }

        favoritesButton.setOnClickListener {
            startActivity(Intent(this.context, FavoritesActivity::class.java))
        }

        searchButton.setOnClickListener {
            activity?.onSearchRequested()
        }

        extendedFab.setOnClickListener {

        }
    }

    private fun initializeViews(view: View) {
        viewPager = view.findViewById(R.id.view_pager)
        extendedFab = view.findViewById(R.id.extended_fab)
        tabLayout = view.findViewById(R.id.tab_layout)
        searchButton = view.findViewById(R.id.search_button)
        favoritesButton = view.findViewById(R.id.favorites_button)
        liveMatchesSwitch = view.findViewById(R.id.live_switch)
    }
}