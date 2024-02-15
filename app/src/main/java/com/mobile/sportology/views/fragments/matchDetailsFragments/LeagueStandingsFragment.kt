package com.mobile.sportology.views.fragments.matchDetailsFragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.models.football.Standings
import com.mobile.sportology.viewModels.MatchDetailsViewModel
import com.mobile.sportology.views.activities.MatchDetailsActivity
import com.mobile.sportology.views.viewsUtilities.ViewCrossFadeAnimation
import com.mobile.sportology.views.viewsUtilities.imageBinding
import kotlinx.android.synthetic.main.error_layout.view.errorText
import kotlinx.android.synthetic.main.error_layout.view.retry_button
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LeagueStandingsFragment : Fragment(R.layout.fragment_standing), ViewCrossFadeAnimation {
    private lateinit var matchDetailsViewModel: MatchDetailsViewModel
    private lateinit var activity: MatchDetailsActivity

    private lateinit var errorLayout: View
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var standingsView: HorizontalScrollView
    private lateinit var tableLayout: TableLayout

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_standing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        matchDetailsViewModel.standingsLiveData.observe(viewLifecycleOwner) { responseState ->
            when (responseState) {
                is ResponseState.Success -> handleSuccess(responseState.data)
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
        errorLayout = view.findViewById(R.id.error_layout)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        standingsView = view.findViewById(R.id.standings_view)
        tableLayout = view.findViewById(R.id.table_layout)
    }

    private fun showLoading() {
        hideViewWithAnimation(standingsView)
        hideViewWithAnimation(errorLayout)
        showViewWithAnimation(loadingIndicator)
    }

    private fun showError(errorMessage: String) {
        hideViewWithAnimation(standingsView)
        hideViewWithAnimation(loadingIndicator)
        errorLayout.apply {
            errorText.text = errorMessage
            retry_button.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    activity.intent?.let {
                        activity.getStandings(it)
                    }?: run {
                        activity.getStandings(activity.args)
                    }
                }
            }
            showViewWithAnimation(this)
        }
    }

    private fun handleSuccess(data: Standings?) {
        hideViewWithAnimation(loadingIndicator)
        val standingsList = data?.response?.get(0)?.league?.standings?.get(0)
        standingsList?.let { buildStandingItems(it) }
        hideViewWithAnimation(errorLayout)
        showViewWithAnimation(standingsView)
    }

    private fun buildStandingItems(standings: List<Standings.Response.League.Standing?>) {
        var teamRank = 1
        val layoutParams = TableRow.LayoutParams(
            requireContext().resources.getDimensionPixelSize(R.dimen.other_text_width),
            TableRow.LayoutParams.MATCH_PARENT
        )

        standings.forEach { teamStanding ->
            val tableRow = createTableRow(teamRank.toString(), teamStanding, layoutParams)
            tableLayout.addView(tableRow)
            teamRank++
        }
    }

    private fun createTableRow(
        teamRank: String,
        teamStanding: Standings.Response.League.Standing?,
        layoutParams: TableRow.LayoutParams
    ): TableRow {
        val context = requireContext()
        val teamRankTextView = createTextView(teamRank, layoutParams).apply {
            when(teamStanding?.description) {
                "Promotion - Champions League (Group Stage: )", "CAF Champions League" ->
                    this.background = ContextCompat.getDrawable(requireContext(), R.drawable.champions_league_qualified)
                "Promotion - Europa League (Group Stage: )", "CAF Confederation Cup" ->
                    this.background = ContextCompat.getDrawable(requireContext(), R.drawable.europa_league_qualified)
                "Relegation - Championship", "Relegation" ->
                    this.background = ContextCompat.getDrawable(requireContext(), R.drawable.relegation_championship)
            }
        }
        val teamLogo = createImageView(teamStanding?.team?.logo)
        val teamNameTextView = createTextView(teamStanding?.team?.name, layoutParams)
        val pointsTextView = createTextView(teamStanding?.points.toString(), layoutParams)
        val againstForTextView = createTextView(teamStanding?.all?.goals?.against.toString(), layoutParams)
        val goalDifferenceTextView = createTextView(teamStanding?.goalsDiff.toString(), layoutParams)
        val playedTextView = createTextView(teamStanding?.all?.played.toString(), layoutParams)
        val winTextView = createTextView(teamStanding?.all?.win.toString(), layoutParams)
        val drawTextView = createTextView(teamStanding?.all?.draw.toString(), layoutParams)
        val loseTextView = createTextView(teamStanding?.all?.lose.toString(), layoutParams)
        val formLayout = createFormLayout(context, teamStanding?.form)

        return TableRow(context).apply {
            addView(teamRankTextView)
            addView(teamLogo)
            addView(teamNameTextView)
            addView(pointsTextView)
            addView(againstForTextView)
            addView(goalDifferenceTextView)
            addView(playedTextView)
            addView(winTextView)
            addView(drawTextView)
            addView(loseTextView)
            addView(formLayout)
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
            this.layoutParams = TableRow.LayoutParams(
                requireContext().resources.getDimensionPixelSize(R.dimen.team_image_width),
                requireContext().resources.getDimensionPixelSize(R.dimen.team_image_height)
            ).apply {
                setMargins(0, 10, 0, 10)
            }
            imageBinding(imageUrl)
        }
    }

    private fun createFormLayout(context: Context, form: String?): LinearLayout {
        return LinearLayout(context).apply {
            gravity = Gravity.CENTER
            this.layoutParams = TableRow.LayoutParams(
                context.resources.getDimensionPixelSize(R.dimen.form_layout_width),
                TableRow.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.HORIZONTAL
            form?.forEach {
                val formationImage = createFormationImage(context, it)
                addView(formationImage)
            }
        }
    }

    private fun createFormationImage(context: Context, formation: Char): ImageView {
        return ImageView(context).apply {
            this.layoutParams = LinearLayout.LayoutParams(
                context.resources.getDimensionPixelSize(R.dimen.formation_image_width),
                context.resources.getDimensionPixelSize(R.dimen.formation_image_width)
            )
            when (formation) {
                'W' -> setImageResource(R.mipmap.ic_w)
                'D' -> setImageResource(R.mipmap.ic_d)
                'L' -> setImageResource(R.mipmap.ic_l)
            }
        }
    }
}