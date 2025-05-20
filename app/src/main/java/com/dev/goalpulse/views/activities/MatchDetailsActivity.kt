package com.dev.goalpulse.views.activities

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
import com.dev.goalpulse.R
import com.dev.goalpulse.databinding.ActivityMatchDetailsBinding
import com.dev.goalpulse.views.fragments.matchDetails.LeagueStandingFragmentDirections
import com.dev.goalpulse.views.fragments.matchDetails.MatchLineupsFragmentDirections
import com.google.android.material.snackbar.Snackbar
import com.dev.goalpulse.models.football.Matches
import com.dev.goalpulse.repositories.RemoteRepository
import com.dev.goalpulse.servicesAndUtilities.NetworkConnectivityReceiver
import com.dev.goalpulse.viewModels.MatchDetailsViewModel
import com.dev.goalpulse.viewModels.MyViewModelProvider
import com.dev.goalpulse.views.fragments.matchDetails.MatchStatisticsFragmentDirections
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
    private lateinit var _binding: ActivityMatchDetailsBinding

    private lateinit var snackBar: Snackbar
    private lateinit var navController: NavController
    private lateinit var action: NavDirections
    private val args: MatchDetailsActivityArgs? by navArgs()

    private lateinit var match: Matches.MatchesItem

    val viewModel by lazy {
        ViewModelProvider(
            this, MyViewModelProvider(
                application, remoteRepository = remoteRepository,
                defaultLocalRepository = null))[MatchDetailsViewModel::class.java]
    }
    private var menuId = R.id.home_lineups

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_match_details)

        this.match = args!!.match
        _binding.matchDetails = this.match
        getMatchStatistics()
        this.match.graphsId?.let {
            getMatchGraphs()
        }

        snackBar = Snackbar.make(
            _binding.motionLayout,
            this.resources.getString(R.string.unable_to_connect),
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(this.resources.getString(R.string.dismiss)) {
            snackBar.dismiss()
        }

        navController =
            _binding.matchDetailsLayout.navHostFragment.getFragment<NavHostFragment>().navController

        _binding.stats.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                getMatchStatistics()
                args?.match?.graphsId?.let {
                    getMatchGraphs()
                }
            }
            if (navController.currentDestination?.id == R.id.matchLineupsFragment) {
                action =
                    MatchLineupsFragmentDirections.actionMatchLineupsFragmentToMatchStatsFragment()
                startFragment(action)
            } else if (navController.currentDestination?.id == R.id.matchStandingsFragment) {
                action =
                    LeagueStandingFragmentDirections.actionMatchStandingsFragmentToMatchStatsFragment()
                startFragment(action)
            }
        }

        _binding.lineups.setOnClickListener {
            showMenu(it)
        }

        _binding.standing.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) { getStandings() }
            if (navController.currentDestination?.id == R.id.matchStatsFragment) {
                action =
                    MatchStatisticsFragmentDirections.actionMatchStatsFragmentToMatchStandingsFragment()
                startFragment(action)
            } else if (navController.currentDestination?.id == R.id.matchLineupsFragment) {
                action =
                    MatchLineupsFragmentDirections.actionMatchLineupsFragmentToMatchStandingsFragment()
                startFragment(action)
            }
        }

//        viewModel.matchStatisticsLiveData.observe(this) { responseState ->
//            when(responseState) {
//                is ResponseState.Success -> {
//                    _binding.matchDetails = responseState.data
//                    _binding.dateTime.text = responseState.data?.response?.get(0)?.fixture?.date?.let {
//                            date -> DateTimeUtils.formatDateTime(date)
//                    }
//                    _binding.status.text = responseState.data?.response?.get(0)?.fixture?.status?.long
//                    _binding.score.text = responseState.data?.response?.get(0)?.goals?.home.toString() + " - " +
//                            responseState.data?.response?.get(0)?.goals?.away.toString()
//                }
//                is ResponseState.Error -> {
//                    _binding.matchDetails = responseState.data
//                    _binding.dateTime.text = responseState.data?.response?.get(0)?.fixture?.date?.let {
//                            date -> DateTimeUtils.formatDateTime(date)
//                    }
//                    _binding.status.text = responseState.data?.response?.get(0)?.fixture?.status?.long
//                }
//                is ResponseState.Loading -> {}
//            }
//        }
    }

    private fun startFragment(action: NavDirections) {
        _binding.matchDetailsLayout.navHostFragment.findNavController().navigate(action)
    }

    private fun prepareLineupsFragment(id: Int) {
        when (id) {
            R.id.home_lineups -> {
                when (navController.currentDestination?.id) {
                    R.id.matchStatsFragment -> {
                        action =
                            MatchStatisticsFragmentDirections.actionMatchStatsFragmentToMatchLineupsFragment(
                                R.id.home_lineups)
                        startFragment(action)
                    }
                    R.id.matchStandingsFragment -> {
                        action =
                            LeagueStandingFragmentDirections.actionMatchStandingsFragmentToMatchLineupsFragment(
                                R.id.home_lineups)
                        startFragment(action)
                    }
                    R.id.matchLineupsFragment -> {
                        action =
                            MatchLineupsFragmentDirections.actionMatchLineupsFragmentSelf2(R.id.home_lineups)
                        startFragment(action)
                    }
                }
            }
            R.id.away_lineups -> {
                when (navController.currentDestination?.id) {
                    R.id.matchStatsFragment -> {
                        action =
                            MatchStatisticsFragmentDirections.actionMatchStatsFragmentToMatchLineupsFragment(
                                R.id.away_lineups)
                        startFragment(action)
                    }
                    R.id.matchStandingsFragment -> {
                        action =
                            LeagueStandingFragmentDirections.actionMatchStandingsFragmentToMatchLineupsFragment(
                                R.id.away_lineups)
                        startFragment(action)
                    }
                    R.id.matchLineupsFragment -> {
                        action =
                            MatchLineupsFragmentDirections.actionMatchLineupsFragmentSelf2(R.id.away_lineups)
                        startFragment(action)
                    }
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

    fun getStandings() {
        args?.let {
            val seasonId = it.match.seasonId!!
            val stringSeasonId = viewModel.convertArgumentFromIntToString(seasonId)
            viewModel.getStandings(season = stringSeasonId)
        }
    }

    fun getMatchStatistics() {
        args?.let {
            val matchId = it.match.id!!
            val stringMatchId = viewModel.convertArgumentFromIntToString(matchId)
            viewModel.getMatchStatistics(matchId = stringMatchId)
        }
    }

    private fun getMatchGraphs() {
        args?.let {
            val graphId = it.match.graphsId!!
            val stringGraphId = viewModel.convertArgumentFromIntToString(graphId)
            viewModel.getMatchGraphs(graphId = stringGraphId)
        }

    }

    fun getMatchPositions() {
        args?.let {
            val matchPositionsId = it.match.id!!
            val stringMatchId = viewModel.convertArgumentFromIntToString(matchPositionsId)
            viewModel.getMatchPlayerPositions(matchId = stringMatchId)
        }
    }
}