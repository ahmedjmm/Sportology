package com.dev.goalpulse.views.fragments.matchDetails

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dev.goalpulse.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.databinding.ErrorLayoutBinding
import com.dev.goalpulse.models.football.Standing
import com.dev.goalpulse.viewModels.MatchDetailsViewModel
import com.dev.goalpulse.views.activities.MatchDetailsActivity
import com.dev.goalpulse.views.viewsUtilities.ViewCrossFadeAnimation
import com.dev.goalpulse.views.viewsUtilities.imageBinding
import hilt_aggregated_deps._com_dev_goalpulse_views_activities_AllSearchResultsActivity_GeneratedInjector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LeagueStandingFragment : Fragment(R.layout.fragment_standing), ViewCrossFadeAnimation {
    private lateinit var matchDetailsViewModel: MatchDetailsViewModel
    private lateinit var activity: MatchDetailsActivity

    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var standingsView: HorizontalScrollView
    private lateinit var tableLayout: TableLayout
    private var _errorLayoutBinding: ErrorLayoutBinding? = null

    override var shortAnimationDuration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shortAnimationDuration =
            requireContext().resources.getInteger(android.R.integer.config_shortAnimTime)

        activity = requireActivity() as MatchDetailsActivity
        matchDetailsViewModel = activity.viewModel
        // To avoid returning back to the previous fragment
        activity.onBackPressedDispatcher.addCallback(this) {
            activity.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _errorLayoutBinding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_standing, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        matchDetailsViewModel.standingsLiveData.observe(viewLifecycleOwner) { responseState ->
            when (responseState) {
                is ResponseState.Success -> handleSuccess(responseState)
                is ResponseState.Loading -> showLoading()
                is ResponseState.Error -> showError(responseState.message!!)
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

    private fun initializeViews(view: View) {
        val errorLayout = view.findViewById<View>(R.id.error_layout)
        _errorLayoutBinding = ErrorLayoutBinding.bind(errorLayout)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        standingsView = view.findViewById(R.id.standings_view)
        tableLayout = view.findViewById(R.id.table_layout)
    }

    private fun showLoading() {
        hideViewWithAnimation(standingsView)
        hideViewWithAnimation(_errorLayoutBinding!!.root)
        showViewWithAnimation(loadingIndicator)
    }

    private fun showError(errorMessage: String) {
        hideViewWithAnimation(standingsView)
        hideViewWithAnimation(loadingIndicator)
        _errorLayoutBinding?.apply {
            errorText.text = errorMessage
            retryButton.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    activity.getStandings()
                }
            }
            showViewWithAnimation(this.root)
        }
    }

    private fun handleSuccess(responseState: ResponseState.Success<Standing>) {
        if(responseState.data.isNullOrEmpty()) {
            showViewWithAnimation(_errorLayoutBinding!!.root)
            hideViewWithAnimation(standingsView)
        }
        else {
            val standingList = responseState.data[0].competitors
            standingList?.let { buildStandingItems(it) }
            showViewWithAnimation(standingsView)
            hideViewWithAnimation(_errorLayoutBinding!!.root)
        }
        hideViewWithAnimation(loadingIndicator)
    }

    private fun buildStandingItems(standing: List<Standing.StandingItem.Competitor?>) {
        val layoutParams = TableRow.LayoutParams(
            requireContext().resources.getDimensionPixelSize(R.dimen.other_text_width),
            TableRow.LayoutParams.MATCH_PARENT
        )

        standing.forEach { teamStanding ->
            val tableRow = createTableRow(teamStanding!!, layoutParams)
            tableLayout.addView(tableRow)
        }
    }

    private fun createTableRow(
        teamStanding: Standing.StandingItem.Competitor,
        layoutParams: TableRow.LayoutParams
    ): TableRow {
        val teamRankTextView = createTextView(teamStanding.position.toString(), layoutParams)
        val teamLogo = createImageView(teamStanding.teamHashImage)
        val teamNameTextView = createTextView(teamStanding.teamName, layoutParams)
        val pointsTextView = createTextView(teamStanding.points.toString(), layoutParams)
        val goalForTextView = createTextView(teamStanding.scoresFor.toString(), layoutParams)
        val goalAgainstTextView = createTextView(teamStanding.scoresAgainst.toString(), layoutParams)
        val againstFor = (teamStanding.scoresFor!! - teamStanding.scoresAgainst!!).toString()
        val againstForTextView = createTextView(againstFor, layoutParams)
        val playedTextView = createTextView(teamStanding.matches.toString(), layoutParams)
        val winTextView = createTextView(teamStanding.wins.toString(), layoutParams)
        val drawTextView = createTextView(teamStanding.draws.toString(), layoutParams)
        val loseTextView = createTextView(teamStanding.losses.toString(), layoutParams)

        return TableRow(context).apply {
            addView(teamRankTextView)
            addView(teamLogo)
            addView(teamNameTextView)
            addView(pointsTextView)
            addView(goalForTextView)
            addView(goalAgainstTextView)
            addView(againstForTextView)
            addView(playedTextView)
            addView(winTextView)
            addView(drawTextView)
            addView(loseTextView)
        }
    }

    private fun createTextView(text: String?, layoutParams: ViewGroup.LayoutParams): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            this.layoutParams = layoutParams
            gravity = Gravity.CENTER
        }
    }

    private fun createImageView(imageUrl: String?): ImageView {
        return ImageView(requireContext()).apply {
            this.layoutParams = TableRow.LayoutParams(requireContext().resources.getDimensionPixelSize(R.dimen.team_image_width), requireContext().resources.getDimensionPixelSize(R.dimen.team_image_height)).apply {
                setMargins(0, 10, 0, 10)
            }
            imageBinding(imageUrl)
        }
    }
}