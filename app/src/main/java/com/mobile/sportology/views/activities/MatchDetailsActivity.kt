package com.mobile.sportology.views.activities

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.databinding.ActivityMatchDetailsBinding
import com.mobile.sportology.repositories.RemoteRepository
import com.mobile.sportology.views.viewsUtilities.DateTimeUtils
import com.mobile.sportology.servicesAndUtilities.NetworkConnectivityReceiver
import com.mobile.sportology.viewModels.MatchDetailsViewModel
import com.mobile.sportology.viewModels.MyViewModelProvider
import com.mobile.sportology.views.fragments.matchDetailsFragments.LeagueStandingsFragmentDirections
import com.mobile.sportology.views.fragments.matchDetailsFragments.MatchLineupsFragmentDirections
import com.mobile.sportology.views.fragments.matchDetailsFragments.MatchStatsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MatchDetailsActivity : AppCompatActivity(), NetworkConnectivityReceiver.NetworkStateListener {
    @Inject
    lateinit var intentFilter: IntentFilter
    @Inject
    lateinit var networkConnectivityReceiver: NetworkConnectivityReceiver
    @Inject
    lateinit var remoteRepository: RemoteRepository
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMatchDetailsBinding
    private lateinit var snackBar: Snackbar
    private lateinit var navController: NavController
    private lateinit var action: NavDirections
    val args: MatchDetailsActivityArgs? by navArgs()
    val viewModel by lazy {
        ViewModelProvider(
            this, MyViewModelProvider(
                application, remoteRepository = remoteRepository,
                defaultLocalRepository = null))[MatchDetailsViewModel::class.java]
    }
    private var menuId = R.id.home_lineups

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_match_details)

        intent?.let {
            getFixtureById(it)
            lifecycleScope.launch(Dispatchers.IO) { getStandings(it) }
        }?: run {
            getFixtureById(args)
            lifecycleScope.launch(Dispatchers.IO) { getStandings(args) }
        }

        snackBar = Snackbar.make(
            binding.motionLayout,
            this.resources.getString(R.string.unable_to_connect),
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(this.resources.getString(R.string.dismiss)) {
            snackBar.dismiss()
        }

        navController =
            binding.matchDetailsLayout.navHostFragment.getFragment<NavHostFragment>().navController

        binding.stats.setOnClickListener {
            if (navController.currentDestination?.displayName.equals(
                    "com.mobile.sportology:id/matchLineupsFragment")
            ) {
                action =
                    MatchLineupsFragmentDirections.actionMatchLineupsFragmentToMatchStatsFragment()
                startFragment(action)
            } else if (navController.currentDestination?.displayName.equals(
                    "com.mobile.sportology:id/matchStandingsFragment"
                )
            ) {
                action =
                    LeagueStandingsFragmentDirections.actionMatchStandingsFragmentToMatchStatsFragment()
                startFragment(action)
            }
        }
        binding.lineups.setOnClickListener {
            showMenu(it)
        }
        binding.standing.setOnClickListener {
            if (navController.currentDestination?.displayName.equals(
                    "com.mobile.sportology:id/matchStatsFragment"
                )
            ) {
                action =
                    MatchStatsFragmentDirections.actionMatchStatsFragmentToMatchStandingsFragment()
                startFragment(action)
            } else if (navController.currentDestination?.displayName.equals(
                    "com.mobile.sportology:id/matchLineupsFragment"
                )
            ) {
                action =
                    MatchLineupsFragmentDirections.actionMatchLineupsFragmentToMatchStandingsFragment()
                startFragment(action)
            }
        }

        viewModel.fixtureByIdLiveData.observe(this) { responseState ->
            when(responseState) {
                is ResponseState.Success -> {
                    binding.matchDetails = responseState.data
                    binding.dateTime.text = responseState.data?.response?.get(0)?.fixture?.date?.let {
                            date -> DateTimeUtils.formatDateTime(date)
                    }
                    binding.status.text = responseState.data?.response?.get(0)?.fixture?.status?.long
                    binding.score.text = responseState.data?.response?.get(0)?.goals?.home.toString() + " - " +
                            responseState.data?.response?.get(0)?.goals?.away.toString()
                }
                is ResponseState.Error -> {
                    binding.matchDetails = responseState.data
                    binding.dateTime.text = responseState.data?.response?.get(0)?.fixture?.date?.let {
                            date -> DateTimeUtils.formatDateTime(date)
                    }
                    binding.status.text = responseState.data?.response?.get(0)?.fixture?.status?.long
                }
                is ResponseState.Loading -> {}
            }
        }
    }

    private fun startFragment(action: NavDirections) {
        binding.matchDetailsLayout.navHostFragment.findNavController().navigate(action)
    }

    private fun prepareLineupsFragment(id: Int) {
        when (id) {
            R.id.home_lineups -> {
                if (navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchStatsFragment"
                    )
                ) {
                    action =
                        MatchStatsFragmentDirections.actionMatchStatsFragmentToMatchLineupsFragment(R.id.home_lineups)
                    startFragment(action)
                } else if (navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchStandingsFragment"
                    )
                ) {
                    action =
                        LeagueStandingsFragmentDirections.actionMatchStandingsFragmentToMatchLineupsFragment(R.id.home_lineups)
                    startFragment(action)
                } else if(navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchLineupsFragment"
                    )
                ) {
                    action =
                        MatchLineupsFragmentDirections.actionMatchLineupsFragmentSelf2(R.id.home_lineups)
                    startFragment(action)
                }
            }
            R.id.away_lineups -> {
                if (navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchStatsFragment"
                    )
                ) {
                    action =
                        MatchStatsFragmentDirections.actionMatchStatsFragmentToMatchLineupsFragment(R.id.away_lineups)
                    startFragment(action)
                } else if (navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchStandingsFragment"
                    )
                ) {
                    action =
                        LeagueStandingsFragmentDirections.actionMatchStandingsFragmentToMatchLineupsFragment(R.id.away_lineups)
                    startFragment(action)
                } else if(navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchLineupsFragment"
                    )
                ) {
                    action =
                        MatchLineupsFragmentDirections.actionMatchLineupsFragmentSelf2(R.id.away_lineups)
                    startFragment(action)
                }
            }
        }
    }

    private fun showMenu(v: View) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.lineups_menu, popup.menu)
        popup.menu.findItem(menuId).apply {
            this.isChecked = true
            prepareLineupsFragment(this.itemId)
        }
        popup.setOnMenuItemClickListener {
            menuId = it.itemId
            it.isChecked = true
            prepareLineupsFragment(it.itemId)
            true
        }
        popup.show()
    }

    override fun onResume() {
        super.onResume()
        networkConnectivityReceiver.setListener(this)
        registerReceiver(networkConnectivityReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        networkConnectivityReceiver.setListener(null)
        unregisterReceiver(networkConnectivityReceiver)
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        if(!isConnected) snackBar.show()
        else snackBar.dismiss()
    }

    suspend fun getStandings(args: Any?) {
        args?.let {
            if(it is Intent) {
                viewModel.getStandings(
                    season = it.getIntExtra("season", 2023),
                    leagueId = it.getIntExtra("leagueId", 135),
                )
            }
            else if (it is MatchDetailsActivityArgs) {
                viewModel.getStandings(
                    season = it.season,
                    leagueId = it.leagueId
                )
            }
            else {}
        }
    }

    fun getFixtureById(args: Any?) {
        args?.let {
            if(it is Intent) {
                viewModel.getFixtureById(
                    timeZone = sharedPreferences.getString("time_zone", "Asia/Dubai")!!,
                    fixtureId = it.getIntExtra("matchId", 867946),
                )
            }
            else if (it is MatchDetailsActivityArgs) {
                viewModel.getFixtureById(
                    timeZone = sharedPreferences.getString("time_zone", "Asia/Dubai")!!,
                    fixtureId = it.matchId,
                )
            }
        }
    }
}