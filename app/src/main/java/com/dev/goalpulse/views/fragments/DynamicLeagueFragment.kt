package com.dev.goalpulse.views.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.goalpulse.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.Shared
import com.dev.goalpulse.models.football.MatchNotificationRoom
import com.dev.goalpulse.models.football.Matches
import com.dev.goalpulse.views.viewsUtilities.DateTimeUtils
import com.dev.goalpulse.servicesAndUtilities.MatchStartNotificationUtility
import com.dev.goalpulse.viewModels.FootBallViewModel
import com.dev.goalpulse.views.activities.HomeActivity
import com.dev.goalpulse.views.adapters.footballAdapters.LeagueMatchesRecyclerViewAdapter
import com.dev.goalpulse.views.viewsUtilities.ViewCrossFadeAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.error_layout.view.errorText
import kotlinx.android.synthetic.main.error_layout.view.retry_button
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DynamicLeagueFragment: Fragment(R.layout.fragment_dynamic_league),
    LeagueMatchesRecyclerViewAdapter.OnCheckedChangeListener, ViewCrossFadeAnimation {
    private lateinit var footBallViewModel: FootBallViewModel

    private lateinit var leagueRV: RecyclerView
    private lateinit var circularProgressIndicator: CircularProgressIndicator
    private lateinit var leagueMatchesRecyclerViewAdapter: LeagueMatchesRecyclerViewAdapter
    private lateinit var errorLayout: View
    var leagueOrder = 0 //league order in the tab view
    private var offset = 0
    private var season = ""
    private var lastResponseMessage: String? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var matchStartNotificationUtility: MatchStartNotificationUtility

    //required to initialize notificationPermissionLauncher
    private var isNotificationPermissionGranted = false
    private var isItemChecked = false
    private lateinit var fixture: Matches.MatchesItem

    override var shortAnimationDuration: Int = 0

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                onCheckChange(
                    fixture = fixture,
                    isChecked = isItemChecked,
                    action = resources.getString(R.string.MATCH_START_NOTIFICATION_SHOW)
                )
            }
        }

    override fun hideViewWithAnimation(view: View){
        view.animate().alpha(0f).setDuration(shortAnimationDuration.toLong())
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
    }

    override fun showViewWithAnimation(view: View) {
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(shortAnimationDuration.toLong()).setListener(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shortAnimationDuration = requireContext().resources.getInteger(android.R.integer.config_shortAnimTime)
        footBallViewModel = (activity as HomeActivity).footBallViewModel
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
        lifecycleScope.launch(Dispatchers.IO) {
            val seasons = footBallViewModel.getSeasonByLeague(Shared.LEAGUES_IDS[this@DynamicLeagueFragment.leagueOrder])
            val seasonsListSize = seasons.data?.body()?.get(0)?.seasons?.size

            seasonsListSize?.let { listSize ->
                seasons.data.body()?.getOrNull(0)?.let { league ->
                    val id = if (league.leagueId != 27) {
                        league.seasons?.getOrNull(listSize - 1)?.id
                    } else {
                        league.seasons?.getOrNull(0)?.id
                    }
                    id?.let { this@DynamicLeagueFragment.season = "eq.$it" }
                }
            }

            footBallViewModel.getLeagueMatches(leagueId = Shared.LEAGUES_IDS[leagueOrder],
                seasonId = season, offset = this@DynamicLeagueFragment.offset.toString())


//            footBallViewModel.getLeagueMatches(leagueId = Shared.LEAGUES_IDS[leagueOrder],
//                seasonId = "eq.45769", offset = this@DynamicLeagueFragment.offset.toString())
        }

        when(leagueOrder) {
            0 -> footBallViewModel.englandPremierLeagueMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> showMatchesUI(it.data, it.message)
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            1 -> footBallViewModel.laLigaMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data, it.message) }
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            2 -> footBallViewModel.ligue1MatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data, it.message) }
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            3 -> footBallViewModel.serieAMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data, it.message) }
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            4 -> footBallViewModel.bundesLigaMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data, it.message) }
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            5 -> footBallViewModel.egyptianPremierLeagueMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data, it.message) }
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
        }
    }

    private fun initializeViews(view: View) {
        errorLayout = view.findViewById(R.id.error_layout)
        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator)
        leagueRV = view.findViewById(R.id.league_RV)
    }

    private fun showMatchesUI(data: MutableList<Any>?, message: String?) {
        this.lastResponseMessage = message
        leagueMatchesRecyclerViewAdapter.differ.submitList(data)
        hideViewWithAnimation(errorLayout)
        hideViewWithAnimation(circularProgressIndicator)
        showViewWithAnimation(leagueRV)
    }

    private fun showErrorUI(errorMessage: String) {
        errorLayout.apply {
            errorText.text = errorMessage
            retry_button.setOnClickListener {
                if(Shared.isConnected)
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val responseState = footBallViewModel.getLeague(Shared.LEAGUES_IDS[leagueOrder])
                            if(responseState is ResponseState.Success) {
                                footBallViewModel.getLeagueMatches(leagueId = Shared.LEAGUES_IDS[leagueOrder])
                            }
                        }
                        catch (_: Exception) {}
                    }
                else (requireActivity() as HomeActivity).snackBar.show()
            }
            showViewWithAnimation(this)
        }
        hideViewWithAnimation(circularProgressIndicator)
        hideViewWithAnimation(leagueRV)
    }

    private fun showLoadingUI() {
        showViewWithAnimation(circularProgressIndicator)
        hideViewWithAnimation(errorLayout)
        hideViewWithAnimation(leagueRV)
    }

    private fun setupLeagueRecyclerView() {
        leagueMatchesRecyclerViewAdapter = LeagueMatchesRecyclerViewAdapter(this)
        leagueRV.apply {
            adapter = leagueMatchesRecyclerViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = null
            val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
            divider.setDividerColorResource(requireContext(), R.color.WhiteSmoke)
            divider.isLastItemDecorated = false
            addItemDecoration(divider)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        if (lastResponseMessage == context.getString(R.string.all_data_has_been_fetched)) {
                            Toast.makeText(context, lastResponseMessage, Toast.LENGTH_LONG).show()
                        }
                        else {
                            lifecycleScope.launch (Dispatchers.IO){
                                this@DynamicLeagueFragment.offset += 50
                                footBallViewModel.getLeagueMatches(
                                    leagueId = Shared.LEAGUES_IDS[leagueOrder],
                                    seasonId = "eq.45769", ///////////////////////////////////// seasonId = this@DynamicLeagueFragment.season
                                    offset = this@DynamicLeagueFragment.offset.toString()
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    override fun checkNotificationPermission(
        fixture: Matches.MatchesItem,
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
        fixture: Matches.MatchesItem,
        isChecked: Boolean,
        action: String
    ) {
        val time = DateTimeUtils.getTimeInMilliSeconds(fixture.startTime!!)
        lifecycleScope.launch(Dispatchers.IO) {
            val matchNotificationRoom = MatchNotificationRoom(
                matchId = fixture.id!!,
                home = fixture.homeTeamName!!,
                away = fixture.awayTeamName!!,
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
                body = "${fixture.homeTeamName} VS ${fixture.awayTeamName}",
                isChecked = isChecked,
                matchId = fixture.id,
                seasonId = fixture.seasonId!!,
                leagueId = fixture.leagueId!!,
                action = action
            )
        }
    }
}