package com.dev.goalpulse.views.fragments.matchDetailsFragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.goalpulse.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.models.football.FixtureById
import com.dev.goalpulse.viewModels.MatchDetailsViewModel
import com.dev.goalpulse.views.activities.MatchDetailsActivity
import com.dev.goalpulse.views.adapters.footballAdapters.TimeLineRecyclerViewAdapter
import com.dev.goalpulse.views.adapters.matchDetailsAdapters.MatchStatsRecyclerViewAdapter
import com.dev.goalpulse.views.viewsUtilities.ViewCrossFadeAnimation
import kotlinx.android.synthetic.main.error_layout.view.errorText
import kotlinx.android.synthetic.main.error_layout.view.retry_button
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.model.SectionInfo

class MatchStatsFragment: Fragment(R.layout.fragment_stats), ViewCrossFadeAnimation {
    private lateinit var matchDetailsViewModel: MatchDetailsViewModel
    private lateinit var matchStatsRecyclerViewAdapter: MatchStatsRecyclerViewAdapter
    private lateinit var statisticLayout: LinearLayout
    private lateinit var timeLineRecyclerView: TimeLineRecyclerView
    private lateinit var statisticsRecyclerView: RecyclerView
    private lateinit var circularProgress: CircularProgressIndicator
    private lateinit var errorLayout: View
    lateinit var activity: MatchDetailsActivity

    override var shortAnimationDuration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as MatchDetailsActivity
        shortAnimationDuration = requireContext().resources.getInteger(android.R.integer.config_shortAnimTime)
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
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)

        statisticsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        }

        matchDetailsViewModel.fixtureByIdLiveData.observe(viewLifecycleOwner) { responseState ->
            when(responseState) {
                is ResponseState.Success -> handleSuccessState(responseState)
                is ResponseState.Loading -> handleLoadingState()
                is ResponseState.Error -> handleErrorState(responseState.message!!)
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
        circularProgress = view.findViewById(R.id.loadingIndicator)
        errorLayout = view.findViewById(R.id.error_layout)
        statisticsRecyclerView = view.findViewById(R.id.statistics_recycler_view)
        timeLineRecyclerView = view.findViewById(R.id.sticky_timeline)
        statisticLayout = view.findViewById(R.id.statistic_layout)
    }

    private fun handleSuccessState(responseState: ResponseState.Success<FixtureById>) {
        hideViewWithAnimation(circularProgress)
        hideViewWithAnimation(errorLayout)
        responseState.data?.response?.let { response ->
            if(response[0]?.statistics?.isNotEmpty()!!) {
                matchStatsRecyclerViewAdapter = MatchStatsRecyclerViewAdapter(
                    response[0]?.statistics?.get(0)?.statisticsData,
                    response[0]?.statistics?.get(1)?.statisticsData
                )
                statisticsRecyclerView.adapter = matchStatsRecyclerViewAdapter
                responseState.data.response[0]?.events?.let { events ->
                    val timelineAdapter = TimeLineRecyclerViewAdapter().apply {
                        differ.submitList(events)
                    }
                    timeLineRecyclerView.apply {
                        layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
                        adapter = timelineAdapter
                        addItemDecoration(getSectionCallback(events))
                    }
                }
                showViewWithAnimation(statisticLayout)
            }
            else handleEmptyDataState()
        }
    }

    private fun handleErrorState(errorMessage: String) {
        hideViewWithAnimation(statisticLayout)
        hideViewWithAnimation(circularProgress)
        errorLayout.apply {
            errorText.text = errorMessage
            retry_button.setOnClickListener {
                activity.intent?.let {
                    activity.getFixtureById(it)
                }?: run {
                    activity.getFixtureById(activity.args)
                }
            }
            showViewWithAnimation(this)
        }
    }

    private fun handleLoadingState() {
        hideViewWithAnimation(statisticLayout)
        hideViewWithAnimation(errorLayout)
        showViewWithAnimation(circularProgress)
    }

    private fun handleEmptyDataState() {
        hideViewWithAnimation(statisticLayout)
        hideViewWithAnimation(circularProgress)
        errorLayout.apply {
            errorText.text = context?.resources?.getString(R.string.data_not_provided)
            retry_button.setOnClickListener {
                activity.intent?.let {
                    activity.getFixtureById(it)
                }?: run {
                    activity.getFixtureById(activity.args)
                }
            }
            showViewWithAnimation(this)
        }
    }

    private fun getSectionCallback(eventList: List<FixtureById.Response.Event?>?): SectionCallback {
        return object: SectionCallback {
            //Implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                eventList?.get(position)?.time?.elapsed != eventList?.get(position - 1)?.time?.elapsed

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo =
                SectionInfo(eventList?.get(position)?.type!!,
                    eventList[position]?.time?.elapsed.toString())
        }
    }
}