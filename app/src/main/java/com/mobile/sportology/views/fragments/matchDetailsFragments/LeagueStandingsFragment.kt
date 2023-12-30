package com.mobile.sportology.views.fragments.matchDetailsFragments

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.databinding.FragmentStandingBinding
import com.mobile.sportology.models.football.Standings
import com.mobile.sportology.viewModels.MatchDetailsActivityViewModel
import com.mobile.sportology.views.activities.MatchDetailsActivity
import com.mobile.sportology.views.viewsUtilities.imageBinding


class LeagueStandingsFragment : Fragment(R.layout.fragment_standing) {
    private lateinit var binding: FragmentStandingBinding
    private lateinit var matchDetailsViewModel: MatchDetailsActivityViewModel
    private lateinit var activity: MatchDetailsActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as MatchDetailsActivity
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
        matchDetailsViewModel = activity.viewModel
        binding = FragmentStandingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        matchDetailsViewModel.standingsLiveData.observe(viewLifecycleOwner) { responseState ->
            when (responseState) {
                is ResponseState.Success -> handleSuccess(responseState.data)
                is ResponseState.Loading -> showLoading()
                is ResponseState.Error -> showError(responseState.message!!)
                else -> { }
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            loadingIndicator.visibility = View.VISIBLE
            standingsView.visibility = View.GONE
            errorLayout.root.visibility = View.GONE
        }
    }

    private fun showError(errorMessage: String) {
        with(binding.errorLayout) {
            errorText.text = errorMessage
            root.visibility = View.VISIBLE
        }
        with(binding) {
            standingsView.visibility = View.GONE
            loadingIndicator.visibility = View.GONE
        }
    }

    private fun handleSuccess(data: Standings?) {
        with(binding) {
            loadingIndicator.visibility = View.GONE
            if (data?.response?.isNotEmpty() == true) {
                val standingsList = data.response[0]?.league?.standings?.get(0)
                standingsList?.let { buildStandingItems(it) }
                standingsView.visibility = View.VISIBLE
                errorLayout.root.visibility = View.GONE
            }
        }
    }

    private fun buildStandingItems(standings: List<Standings.Response.League.Standing?>) {
        var teamRank = 1
        val layoutParams = TableRow.LayoutParams(
            requireContext().resources.getDimensionPixelSize(R.dimen.other_text_width),
            TableRow.LayoutParams.MATCH_PARENT
        )

        standings.forEach { teamStanding ->
            val tableRow = createTableRow(teamRank.toString(), teamStanding, layoutParams)
            binding.tableLayout.addView(tableRow)
            teamRank++
        }
    }

    private fun createTableRow(
        teamRank: String,
        teamStanding: Standings.Response.League.Standing?,
        layoutParams: TableRow.LayoutParams
    ): TableRow {
        val context = requireContext()
        val teamRankTextView = createTextView(teamRank, layoutParams)
        val teamLogo = createImageView(teamStanding?.team?.logo, R.dimen.team_image_width, R.dimen.team_image_height)
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

    private fun createImageView(imageUrl: String?, widthDimen: Int, heightDimen: Int): ImageView {
        return ImageView(requireContext()).apply {
            this.layoutParams = TableRow.LayoutParams(
                requireContext().resources.getDimensionPixelSize(widthDimen),
                requireContext().resources.getDimensionPixelSize(heightDimen)
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