package com.mobile.sportology.views.fragments

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.Shared
import com.mobile.sportology.Shared.isLiveMatches
import com.mobile.sportology.models.football.Fixtures
import com.mobile.sportology.models.football.MatchNotificationRoom
import com.mobile.sportology.servicesAndUtilities.DateTimeUtils
import com.mobile.sportology.servicesAndUtilities.MatchStartNotificationUtility
import com.mobile.sportology.viewModels.FootBallViewModel
import com.mobile.sportology.views.activities.MainActivity
import com.mobile.sportology.views.adapters.footballAdapters.LeagueRecyclerViewAdapter
import com.mobile.sportology.views.adapters.footballAdapters.MatchRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.error_layout.view.errorText
import kotlinx.android.synthetic.main.error_layout.view.retry_button
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DynamicLeagueFragment: Fragment(R.layout.fragment_dynamic_league),
    MatchRecyclerViewAdapter.OnCheckedChangeListener {
    private lateinit var footBallViewModel: FootBallViewModel

    private lateinit var leagueRV: RecyclerView
    private lateinit var circularProgressIndicator: CircularProgressIndicator
    private lateinit var leagueRecyclerViewAdapter: LeagueRecyclerViewAdapter
    private lateinit var errorLayout: View
    var leagueOrder = 0 //league order in the tab view

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var matchStartNotificationUtility: MatchStartNotificationUtility

    //required to initialize notificationPermissionLauncher
    private var isNotificationPermissionGranted = false
    private var isItemChecked = false
    private lateinit var fixture: Fixtures.Response

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted: Boolean ->
            if (isGranted) {
                this.isItemChecked
                onCheckChange(
                    fixture = fixture,
                    isChecked = isItemChecked,
                    action = resources.getString(R.string.MATCH_START_NOTIFICATION_SHOW)
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        footBallViewModel = (activity as MainActivity).footBallViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dynamic_league, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupLeagueRecyclerView()
        observeLeaguesLiveData()
        when(leagueOrder) {
            0 -> observeLeagueLiveData(footBallViewModel.englandPremierLeagueDatesLiveData)
            1 -> observeLeagueLiveData(footBallViewModel.laLigaDatesLiveData)
            2 -> observeLeagueLiveData(footBallViewModel.ligue1DatesLiveData)
            3 -> observeLeagueLiveData(footBallViewModel.serieADatesLiveData)
            4 -> observeLeagueLiveData(footBallViewModel.bundesLigaDatesLiveData)
            5 -> observeLeagueLiveData(footBallViewModel.egyptianPremierLeagueDatesLiveData)
        }
    }

    private fun initializeViews(view: View) {
        errorLayout = view.findViewById(R.id.error_layout)
        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator)
        leagueRV = view.findViewById(R.id.league_RV)
    }

    private fun observeLeagueLiveData(liveData: LiveData<ResponseState<MutableList<String>>>) {
        liveData.observe(viewLifecycleOwner) { responseState ->
            when(responseState) {
                is ResponseState.Success -> { showMatchesUI(responseState.data) }
                is ResponseState.Loading -> { showLoadingUI() }
                is ResponseState.Error -> { showErrorUI(responseState.message!!) }
            }
        }
    }

    private fun showMatchesUI(data: MutableList<String>?) {
        leagueRV.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        leagueRecyclerViewAdapter.differ.submitList(data)
        circularProgressIndicator.visibility = View.GONE
    }

    private fun observeLeaguesLiveData() {
        footBallViewModel.leaguesLiveData.observe(viewLifecycleOwner) { responseState ->
            when(responseState) {
                is ResponseState.Success -> {
                    if(Shared.isConnected) {
                        val season = responseState.data?.response?.get(0)?.seasons?.get(0)?.year!!
                        val isLiveMatches = checkLiveMatches()
                        lifecycleScope.launch(Dispatchers.IO) {
                            fetchLeagueMatches(Shared.LEAGUES_IDS[leagueOrder], season, isLiveMatches)
                        }
                    }
                }
                is ResponseState.Loading -> { showLoadingUI() }
                is ResponseState.Error -> {
                    showErrorUI(responseState.message!!) }
            }
        }
    }

    private fun checkLiveMatches(): String? {
        return if(isLiveMatches) resources.getString(R.string.live_matches)
        else null
    }

    private fun showErrorUI(errorMessage: String) {
        errorLayout.visibility = View.VISIBLE
        errorLayout.errorText.text = errorMessage
        errorLayout.retry_button.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                footBallViewModel.getLeague(Shared.LEAGUES_IDS[leagueOrder])
            }
        }
        circularProgressIndicator.visibility = View.GONE
        leagueRV.visibility = View.GONE
    }

    private fun showLoadingUI() {
        circularProgressIndicator.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        leagueRV.visibility = View.GONE
    }

    private suspend fun fetchLeagueMatches(leagueId: Int, season: Int, isLiveMatches: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            footBallViewModel.getLeagueMatches(leagueId, season, leagueOrder, isLiveMatches)
        }.join()
    }

    private fun setupLeagueRecyclerView() {
        leagueRecyclerViewAdapter = LeagueRecyclerViewAdapter(
            footBallViewModel,
            leagueOrder,
            this
        )
        leagueRV.apply {
            adapter = leagueRecyclerViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = null
        }
    }

    override fun checkNotificationPermission(
        fixture: Fixtures.Response,
        isChecked: Boolean
    ): Boolean {
        this.fixture = fixture
        this.isItemChecked = isChecked
        when {
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                this.isNotificationPermissionGranted = true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                showPermissionRationaleDialog()
            }
            else -> { requestNotificationPermission() }
        }
        return this.isNotificationPermissionGranted
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            isNotificationPermissionGranted = true
        }
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.notification_permission))
            .setMessage(R.string.match_start_notification_body)
            .setCancelable(false)
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton(resources.getString(R.string.request_permission)) { _, _ ->
                requestNotificationPermission()
            }.show()
    }

    override fun onCheckChange(
        fixture: Fixtures.Response,
        isChecked: Boolean,
        action: String
    ) {
        val time = DateTimeUtils.getTimeInMilliSeconds(fixture.fixture?.date!!)
        lifecycleScope.launch(Dispatchers.IO) {
            val matchNotificationRoom = MatchNotificationRoom(
                matchId = fixture.fixture.id!!,
                home = fixture.teams?.home?.name!!,
                away = fixture.teams.away?.name!!,
                matchTime = time
            )
            if (isChecked) {
                footBallViewModel.upsertMatchNotification(matchNotificationRoom)
                Shared.SAVED_MATCHES_NOTIFICATIONS_IDS.add(matchNotificationRoom.matchId)
            }
            else {
                footBallViewModel.deleteMatchNotification(matchNotificationRoom)
                Shared.SAVED_MATCHES_NOTIFICATIONS_IDS.remove(matchNotificationRoom.matchId)
            }

            matchStartNotificationUtility.scheduleNotification(
                time = time,
                body = "${fixture.teams.home.name} VS ${fixture.teams.away.name}",
                isChecked = isChecked,
                matchId = fixture.fixture.id,
                seasonId = fixture.league?.season!!,
                leagueId = fixture.league.id!!,
                action = action
            )
        }
    }
}