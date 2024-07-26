package com.mobile.sportology.views.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.Shared
import com.mobile.sportology.models.football.Fixtures
import com.mobile.sportology.models.football.MatchNotificationRoom
import com.mobile.sportology.views.viewsUtilities.DateTimeUtils
import com.mobile.sportology.servicesAndUtilities.MatchStartNotificationUtility
import com.mobile.sportology.viewModels.FootBallViewModel
import com.mobile.sportology.views.activities.HomeActivity
import com.mobile.sportology.views.adapters.footballAdapters.LeagueRecyclerViewAdapter
import com.mobile.sportology.views.viewsUtilities.ViewCrossFadeAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.error_layout.view.errorText
import kotlinx.android.synthetic.main.error_layout.view.retry_button
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DynamicLeagueFragment: Fragment(R.layout.fragment_dynamic_league),
    LeagueRecyclerViewAdapter.OnCheckedChangeListener, ViewCrossFadeAnimation {
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
        when(leagueOrder) {
            39 -> footBallViewModel.englandPremierLeagueMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> showMatchesUI(it.data)
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            140 -> footBallViewModel.laLigaMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data) }
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            61 -> footBallViewModel.ligue1MatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data) }
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            135 -> footBallViewModel.serieAMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data) }
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            78 -> footBallViewModel.bundesLigaMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data) }
                    is ResponseState.Loading -> { showLoadingUI() }
                    is ResponseState.Error -> { showErrorUI(it.message!!) }
                }
            }
            233 -> footBallViewModel.egyptianPremierLeagueMatchesLiveData.observe(viewLifecycleOwner) {
                when(it) {
                    is ResponseState.Success -> { showMatchesUI(it.data) }
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

    private fun showMatchesUI(data: MutableList<Any>?) {
        leagueRecyclerViewAdapter.differ.submitList(data)
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
                                if(Shared.isLiveMatches)
                                    footBallViewModel.getLeagueMatches(
                                        leagueId = Shared.LEAGUES_IDS[leagueOrder],
                                        liveMatches = "all",
                                        season = responseState.data?.body()!!.response?.get(0)?.seasons?.get(0)?.year!!
                                    )
                                else
                                    footBallViewModel.getLeagueMatches(
                                        leagueId = Shared.LEAGUES_IDS[leagueOrder],
                                        liveMatches = null,
                                        season = responseState.data?.body()!!.response?.get(0)?.seasons?.get(0)?.year!!
                                    )
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
        leagueRecyclerViewAdapter = LeagueRecyclerViewAdapter(this)
        leagueRV.apply {
            adapter = leagueRecyclerViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = null
            val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
            divider.setDividerColorResource(requireContext(), R.color.WhiteSmoke)
            divider.isLastItemDecorated = false
            addItemDecoration(divider)
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