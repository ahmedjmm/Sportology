package com.mobile.sportology.views.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.annotation.MenuRes
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
import com.mobile.sportology.Shared
import com.mobile.sportology.databinding.ActivityMatchDetailsBinding
import com.mobile.sportology.repositories.RemoteRepository
import com.mobile.sportology.servicesAndUtilities.DateTimeUtils
import com.mobile.sportology.servicesAndUtilities.NetworkConnectivityReceiver
import com.mobile.sportology.viewModels.MatchDetailsActivityViewModel
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
    private lateinit var binding: ActivityMatchDetailsBinding
    private lateinit var snackBar: Snackbar
    private lateinit var navController: NavController
    private lateinit var action: NavDirections
    val args: MatchDetailsActivityArgs? by navArgs()
    val viewModel by lazy {
        ViewModelProvider(
            this, MyViewModelProvider(
                application, remoteRepository = remoteRepository,
                defaultLocalRepository = null))[MatchDetailsActivityViewModel::class.java]
    }
    private var menuId = R.id.home_lineups

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_match_details)

        snackBar = Snackbar.make(binding.motionLayout,
            this.resources.getString(R.string.unable_to_connect),
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(this.resources.getString(R.string.dismiss)) {
            snackBar.dismiss()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.teamLineups = R.id.home_lineups
            if(Shared.isConnected) {
                intent?.let {
                    getFixtureById(it)
                    viewModel.getStandings(
                        season = it.getIntExtra("season", 2022),
                        leagueId = it.getIntExtra("leagueId", 39)
                    )
                }?: run {
                    getFixtureById(args)
                    viewModel.getStandings(season = args!!.season, leagueId = args!!.leagueId)
                }
            }
        }

        navController =
            binding.matchDetailsLayout.navHostFragment.getFragment<NavHostFragment>().navController

        binding.matchDetailsLayout.stats.setOnClickListener {
//            mainBinding.motionLayout.getTransition(id.matchDetailsTransition).isEnabled = true
            if (navController.currentDestination?.displayName.equals(
                    "com.mobile.sportology:id/matchAwayLineupsFragment")
            ) {
                action =
                    MatchLineupsFragmentDirections.actionMatchAwayLineupsFragmentToMatchStatsFragment()
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
        binding.matchDetailsLayout.lineups.setOnClickListener {
//            mainBinding.motionLayout.getTransition(id.matchDetailsTransition).isEnabled = true
            showMenu(it, R.menu.lineups_menu)
        }
        binding.matchDetailsLayout.standing.setOnClickListener {
            if (navController.currentDestination?.displayName.equals(
                    "com.mobile.sportology:id/matchStatsFragment"
                )
            ) {
                action =
                    MatchStatsFragmentDirections.actionMatchStatsFragmentToMatchStandingsFragment()
                startFragment(action)
            } else if (navController.currentDestination?.displayName.equals(
                    "com.mobile.sportology:id/matchAwayLineupsFragment"
                )
            ) {
                action =
                    MatchLineupsFragmentDirections.actionMatchAwayLineupsFragmentToMatchStandingsFragment()
                startFragment(action)
            }
        }

        viewModel.fixtureByIdLiveData.observe(this) { fixtureById ->
            binding.matchDetails = fixtureById.data
            binding.dateTime.text = fixtureById.data?.response?.get(0)?.fixture?.date?.let {
                    date -> DateTimeUtils.formatDateTime(date)
            }
            binding.status.text = fixtureById.data?.response?.get(0)?.fixture?.status?.long
            binding.score.text = fixtureById.data?.response?.get(0)?.goals?.home.toString() + " - " +
                    fixtureById.data?.response?.get(0)?.goals?.away.toString()
            binding.matchDetailsLayout.root.visibility = View.VISIBLE
            binding.errorLayout.errorLayoutBody.visibility = View.GONE
        }
    }

    fun getFixtureById(args: Any?) {
        if(args is Intent) {
            viewModel.getFixtureById(
                timeZone = resources.getStringArray(R.array.time_zones)[0],
                fixtureId = args.getIntExtra("matchId", 867946),
            )
        }
        else if (args is MatchDetailsActivityArgs){
            viewModel.getFixtureById(
                timeZone = resources.getStringArray(R.array.time_zones)[0],
                fixtureId = args.matchId,
            )
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
                        MatchStatsFragmentDirections.actionMatchStatsFragmentToMatchAwayLineupsFragment(R.id.home_lineups)
                    startFragment(action)
                } else if (navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchStandingsFragment"
                    )
                ) {
                    action =
                        LeagueStandingsFragmentDirections.actionMatchStandingsFragmentToMatchAwayLineupsFragment(R.id.home_lineups)
                    startFragment(action)
                } else if(navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchAwayLineupsFragment"
                    )
                ) {
                    action =
                        MatchLineupsFragmentDirections.actionMatchAwayLineupsFragmentSelf2(R.id.home_lineups)
                    startFragment(action)
                }
            }
            R.id.away_lineups -> {
                if (navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchStatsFragment"
                    )
                ) {
                    action =
                        MatchStatsFragmentDirections.actionMatchStatsFragmentToMatchAwayLineupsFragment(R.id.away_lineups)
                    startFragment(action)
                } else if (navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchStandingsFragment"
                    )
                ) {
                    action =
                        LeagueStandingsFragmentDirections.actionMatchStandingsFragmentToMatchAwayLineupsFragment(R.id.away_lineups)
                    startFragment(action)
                } else if(navController.currentDestination?.displayName.equals(
                        "com.mobile.sportology:id/matchAwayLineupsFragment"
                    )
                ) {
                    action =
                        MatchLineupsFragmentDirections.actionMatchAwayLineupsFragmentSelf2(R.id.away_lineups)
                    startFragment(action)
                }
            }
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
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
}