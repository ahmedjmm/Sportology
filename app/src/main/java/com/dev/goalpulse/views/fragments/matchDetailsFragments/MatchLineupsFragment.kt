package com.dev.goalpulse.views.fragments.matchDetailsFragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.dev.goalpulse.R
import com.dev.goalpulse.databinding.FragmentLineupsBinding
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.models.football.FixtureById
import com.dev.goalpulse.views.viewsUtilities.DateTimeUtils
import com.dev.goalpulse.viewModels.MatchDetailsViewModel
import com.dev.goalpulse.views.activities.MatchDetailsActivity
import com.dev.goalpulse.views.viewsUtilities.ViewCrossFadeAnimation
import com.dev.goalpulse.views.viewsUtilities.imageBinding
import kotlinx.android.synthetic.main.error_layout.retry_button

class MatchLineupsFragment: Fragment(R.layout.fragment_lineups), ViewCrossFadeAnimation {
    private lateinit var fragmentBinding: FragmentLineupsBinding
    private lateinit var matchDetailsViewModel: MatchDetailsViewModel
    lateinit var activity: MatchDetailsActivity
    private val args: MatchLineupsFragmentArgs? by navArgs()

    override var shortAnimationDuration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shortAnimationDuration =
            requireContext().resources.getInteger(android.R.integer.config_shortAnimTime)
        activity = requireActivity() as MatchDetailsActivity
        matchDetailsViewModel = activity.viewModel
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
        fragmentBinding = FragmentLineupsBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args?.let {
            matchDetailsViewModel.teamLineups = it.teamLineups
            Log.i("lineupID", matchDetailsViewModel.teamLineups.toString())
        }
        matchDetailsViewModel.fixtureByIdLiveData.observe(viewLifecycleOwner) { responseState ->
            when (responseState) {
                is ResponseState.Success -> { handleSuccess(responseState.data!!) }
                is ResponseState.Loading -> { showLoading() }
                is ResponseState.Error -> { showError(responseState.message!!) }
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

    private fun handleSuccess(data: FixtureById) {
        hideViewWithAnimation(fragmentBinding.loadingIndicator)
        hideViewWithAnimation(fragmentBinding.errorLayout.root)
        if (DateTimeUtils.getTimeInMilliSeconds(data.response?.get(0)?.fixture?.date!!) > System.currentTimeMillis())
            showError(getString(R.string.data_not_provided))
        else {
            val awayLineup = if(matchDetailsViewModel.teamLineups == R.id.home_lineups)
                data.response[0]?.lineups?.get(0)
            else data.response[0]?.lineups?.get(1)

            awayLineup?.let {
                fragmentBinding.coachName.text = it.coach?.name
                fragmentBinding.coachImage.imageBinding(it.coach?.photo)
                fragmentBinding.formation.text = it.formation
                buildLineupsViews(it)
                buildSubstitutesView(it.substitutes)
            }
            showViewWithAnimation(fragmentBinding.coachAndFormation)
            showViewWithAnimation(fragmentBinding.lineupsLayout)
            showViewWithAnimation(fragmentBinding.substitutionsView)
        }
    }

    private fun showLoading() {
        hideViewWithAnimation(fragmentBinding.fragmentViews)
        hideViewWithAnimation(fragmentBinding.fragmentViews)
        hideViewWithAnimation(fragmentBinding.errorLayout.root)
        hideViewWithAnimation(fragmentBinding.lineupsLayout)
        showViewWithAnimation(fragmentBinding.loadingIndicator)
    }

    private fun showError(errorMessage: String) {
        hideViewWithAnimation(fragmentBinding.fragmentViews)
        hideViewWithAnimation(fragmentBinding.loadingIndicator)
        hideViewWithAnimation(fragmentBinding.lineupsLayout)
        hideViewWithAnimation(fragmentBinding.coachAndFormation)
        hideViewWithAnimation(fragmentBinding.substitutionsView)
        fragmentBinding.errorLayout.apply {
            errorText.text = errorMessage
            retry_button.setOnClickListener {
                activity.intent?.let {
                    activity.getFixtureById(it)
                }?: run {
                    activity.getFixtureById(activity.args)
                }
            }
            showViewWithAnimation(this.root)
        }
    }

    private fun buildSubstitutesView(substituteList: List<FixtureById.Response.Lineup.Substitute?>?) {
        substituteList?.forEach {
            val playerImage = ImageView(this.context).apply {
                layoutParams = RelativeLayout.LayoutParams(120, 160)
                setPadding(0, 0, 10, 0)
                imageBinding("imageURL")
            }
            val playerName = TextView(this.context).apply {
                text = it?.player?.name
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 5f)
                setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                setTypeface(null, Typeface.BOLD)
            }
            val playerPosition = TextView(this.context).apply {
                text = it?.player?.pos
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                setTypeface(null, Typeface.BOLD)
            }
            val playerLayout = LinearLayout(this.requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                setPadding(10, 0, 10, 0)
                addView(playerImage)
                addView(playerName)
                addView(playerPosition)
            }
            fragmentBinding.substitutionsView.addView(playerLayout)
        }
    }

    private fun buildLineupsViews(lineups: FixtureById.Response.Lineup) {
        buildGoalKeeperView(lineups.startXI?.get(0)?.player)
        buildOtherPlayersViews(lineups)
    }

    private fun buildOtherPlayersViews(lineups: FixtureById.Response.Lineup) {
        val formationDigitsList = prepareForLineupsView(lineups.formation!!)
        var playersCount = 1
        for(formationDigitsIndex in formationDigitsList.indices){
            val lineLayout = LinearLayout(this.context).apply {
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0, 0.1f)
                orientation = LinearLayout.HORIZONTAL
            }

            val playersToAddCount = formationDigitsList[formationDigitsIndex]
            for(playersIndex in 1..playersToAddCount) {
                val playerLayout = LinearLayout(this.context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, .1f)
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                }
                val playerImage = ImageView(this.context).apply {
                    layoutParams = RelativeLayout.LayoutParams(120, 160)
                    imageBinding("imageURL")
                }
                val playerName = TextView(this.context).apply {
                    text = lineups.startXI?.get(playersCount)?.player?.name
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                    setTypeface(null, Typeface.BOLD)
                }
                playerLayout.addView(playerImage)
                playerLayout.addView(playerName)
                lineLayout.addView(playerLayout)
                playersCount++
            }
            fragmentBinding.lineupsLayout.addView(lineLayout)
        }
    }

    private fun prepareForLineupsView(formation: String): MutableList<Int> {
        val formationDigitsList = mutableListOf<Int>()
        val formationDigits = formation.split("-")
        formationDigits.forEach {
            formationDigitsList.add(it.toInt())
        }
        return formationDigitsList
    }

    private fun buildGoalKeeperView(player: FixtureById.Response.Lineup.StartXI.Player?) {
        val lineLayout = LinearLayout(this.context).apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.1f)
            orientation = LinearLayout.HORIZONTAL
        }
        val playerLayout = LinearLayout(this.context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, .1f)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val playerImage = ImageView(this.context).apply {
            layoutParams = RelativeLayout.LayoutParams(120, 160)
            imageBinding("imageURL")
        }
        val playerName = TextView(this.context).apply {
            text = player?.name
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            setTypeface(null, Typeface.BOLD)
        }
        playerLayout.addView(playerImage)
        playerLayout.addView(playerName)
        lineLayout.addView(playerLayout)
        fragmentBinding.lineupsLayout.addView(lineLayout)
    }
}