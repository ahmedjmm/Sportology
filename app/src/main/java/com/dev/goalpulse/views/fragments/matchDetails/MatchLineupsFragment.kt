package com.dev.goalpulse.views.fragments.matchDetails

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.dev.goalpulse.R
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.databinding.FragmentLineupsBinding
import com.dev.goalpulse.models.football.MatchPositions
import com.dev.goalpulse.viewModels.MatchDetailsViewModel
import com.dev.goalpulse.views.activities.MatchDetailsActivity
import com.dev.goalpulse.views.viewsUtilities.ViewCrossFadeAnimation

class MatchLineupsFragment: Fragment(R.layout.fragment_lineups), ViewCrossFadeAnimation {
    private lateinit var _fragmentBinding: FragmentLineupsBinding
    private lateinit var _matchDetailsViewModel: MatchDetailsViewModel
    lateinit var activity: MatchDetailsActivity
    private val args: MatchLineupsFragmentArgs? by navArgs()

    override var shortAnimationDuration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shortAnimationDuration =
            requireContext().resources.getInteger(android.R.integer.config_shortAnimTime)
        activity = requireActivity() as MatchDetailsActivity
        _matchDetailsViewModel = activity.viewModel
        activity.getMatchPositions()
        //to avoid returning back to previous fragment
        activity.onBackPressedDispatcher.addCallback(this) {
            activity.finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding = FragmentLineupsBinding.inflate(inflater, container, false)
        return _fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _matchDetailsViewModel.playerPositionsLiveData.observe(viewLifecycleOwner) { responseState ->
            Log.i("playerPositionsLiveData", responseState.data.toString())
            when(responseState) {
                is ResponseState.Success -> {
                    handleSuccess(responseState)
                }
                is ResponseState.Loading -> {
                    showLoading()
                }
                is ResponseState.Error -> {
                    showError(responseState.message!!)
                }
            }
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

    private fun updatePitchWithPlayers(positions: MatchPositions) {
        val teamPositions: List<MatchPositions.MatchPositionsItem.Position?> = when(args?.teamPositions) {
            R.id.home_lineups -> positions[1].positions ?: emptyList()
            R.id.away_lineups -> positions[0].positions ?: emptyList()
            else -> emptyList()
        }
        // Set players on pitch
        _fragmentBinding.FootballFieldView.setPlayerPositions(teamPositions)

        // You can also update other UI elements like coach info, formation, etc.
    }

    private fun handleSuccess(responseState: ResponseState<MatchPositions>) {
        responseState.data?.let {
            hideViewWithAnimation(_fragmentBinding.loadingIndicator)
            hideViewWithAnimation(_fragmentBinding.errorLayout.root)
            showViewWithAnimation(_fragmentBinding.coachAndFormation)
            showViewWithAnimation(_fragmentBinding.lineupsLayout)
            showViewWithAnimation(_fragmentBinding.substitutionsView)
            updatePitchWithPlayers(it)
        } ?: run {
            handleEmptyData(responseState.message!!)
        }
    }

    private fun handleEmptyData(message: String) = showError(message)

    private fun showLoading() {
        hideViewWithAnimation(_fragmentBinding.fragmentViews)
        hideViewWithAnimation(_fragmentBinding.fragmentViews)
        hideViewWithAnimation(_fragmentBinding.errorLayout.root)
        hideViewWithAnimation(_fragmentBinding.lineupsLayout)
        showViewWithAnimation(_fragmentBinding.loadingIndicator)
    }

    private fun showError(errorMessage: String) {
        hideViewWithAnimation(_fragmentBinding.fragmentViews)
        hideViewWithAnimation(_fragmentBinding.loadingIndicator)
        hideViewWithAnimation(_fragmentBinding.lineupsLayout)
        hideViewWithAnimation(_fragmentBinding.coachAndFormation)
        hideViewWithAnimation(_fragmentBinding.substitutionsView)
        _fragmentBinding.errorLayout.apply {
            errorText.text = errorMessage
            retryButton.setOnClickListener {
                activity.getMatchStatistics()
            }
            showViewWithAnimation(this.root)
        }
    }
}