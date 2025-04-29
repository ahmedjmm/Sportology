package com.dev.goalpulse.views.fragments.bottomNav

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
import com.bumptech.glide.Glide
import com.dev.goalpulse.R
import com.dev.goalpulse.databinding.TabItemBinding
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.tabs.TabLayout
import com.dev.goalpulse.Shared
import com.dev.goalpulse.views.viewsUtilities.DateTimeUtils
import com.dev.goalpulse.viewModels.FootBallViewModel
import com.dev.goalpulse.views.activities.FavoritesActivity
import com.dev.goalpulse.views.activities.HomeActivity
import com.dev.goalpulse.views.adapters.footballAdapters.FootBallViewPagerAdapter
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
//    private lateinit var extendedFab: ExtendedFloatingActionButton
    private lateinit var footBallViewModel: FootBallViewModel
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DateTimeUtils.timeFormat = sharedPreferences.getString("time_format", "12 HS")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        footBallViewModel = (activity as HomeActivity).footBallViewModel
        return inflater.inflate(R.layout.fragment_football, container ,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setOnClickListeners()
        viewPager.apply {
            adapter = FootBallViewPagerAdapter(childFragmentManager, lifecycle)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })
        }

        footBallViewModel.leaguesLiveData.observe(viewLifecycleOwner) {
            it.forEach { league ->
                TabItemBinding.inflate(layoutInflater).apply {
                    this.league = league[0]
                    val tab = tabLayout.newTab().setCustomView(this.root)
                    val leagueLogoString = "https://images.sportdevs.com/${league[0].hashImage}.png"
                    Glide.with(this@FootBallFragment).load(leagueLogoString).into(leagueLogo);
                    tabLayout.addTab(tab)
                    val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in)
                    tab.customView?.startAnimation(animation)
                }
            }
        }
    }

    private fun setOnClickListeners() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

//        liveMatchesSwitch.setOnCheckedChangeListener { _, isChecked ->
//            Shared.isLiveMatches = isChecked
//
//            footBallViewModel.leaguesLiveData.value?.let {
//                for(index in it.indices) {
//                    val season = it[index].response?.get(0)?.seasons?.get(0)?.year!!
//                    val leagueId = it[index].response?.get(0)?.leagueData?.id!!
//                    if(Shared.isLiveMatches)
//                        lifecycleScope.launch(Dispatchers.IO) {
//                            footBallViewModel.getLeagueMatches(
//                                leagueId = leagueId,
//                                season = season,
//                                liveMatches = resources.getString(R.string.live_matches)
//                            )
//                        }
//                    else
//                        lifecycleScope.launch(Dispatchers.IO) {
//                            footBallViewModel.getLeagueMatches(
//                                leagueId = leagueId,
//                                season = season,
//                                liveMatches = null
//                            )
//                        }
//                }
//            }
//        }

        favoritesButton.setOnClickListener {
            startActivity(Intent(this.context, FavoritesActivity::class.java))
        }

        searchButton.setOnClickListener {
            activity?.onSearchRequested()
        }

//        extendedFab.setOnClickListener {
//            Toast.makeText(context, "Coming soon", Toast.LENGTH_LONG).show()
//        }
    }

    private fun initializeViews(view: View) {
        viewPager = view.findViewById(R.id.view_pager)
//        extendedFab = view.findViewById(R.id.extended_fab)
        tabLayout = view.findViewById(R.id.tab_layout)
        searchButton = view.findViewById(R.id.search_button)
        favoritesButton = view.findViewById(R.id.favorites_button)
        liveMatchesSwitch = view.findViewById(R.id.live_switch)
    }
}