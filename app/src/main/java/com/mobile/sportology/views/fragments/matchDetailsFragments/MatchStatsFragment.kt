package com.mobile.sportology.views.fragments.matchDetailsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.databinding.FragmentStatsBinding
import com.mobile.sportology.models.football.FixtureById
import com.mobile.sportology.viewModels.MatchDetailsActivityViewModel
import com.mobile.sportology.views.activities.MatchDetailsActivity
import com.mobile.sportology.views.adapters.footballAdapters.TimeLineRecyclerViewAdapter
import com.mobile.sportology.views.adapters.matchDetailsAdapters.MatchStatsRecyclerViewAdapter
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.lang.reflect.InvocationTargetException

class MatchStatsFragment: Fragment(R.layout.fragment_stats) {
    lateinit var binding: FragmentStatsBinding
    private lateinit var matchDetailsViewModel: MatchDetailsActivityViewModel
    private lateinit var matchStatsRecyclerViewAdapter: MatchStatsRecyclerViewAdapter
    lateinit var activity: MatchDetailsActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding = FragmentStatsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timelineAdapter = TimeLineRecyclerViewAdapter()
        binding.stickyTimeline.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
            adapter = timelineAdapter
        }

        binding.statisticsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        }

        matchDetailsViewModel.fixtureByIdLiveData.observe(viewLifecycleOwner) { responseState ->
            when(responseState) {
                is ResponseState.Success -> {
                    binding.loadingIndicator.visibility = View.GONE
                    responseState.data?.response?.let { response ->
                        if(response[0]?.statistics?.isNotEmpty()!!) {
                            val homePossession = (response[0]?.statistics?.get(0)?.statisticsData
                                    as MutableList).removeAt(9)
                            val awayPossession = (response[0]?.statistics?.get(1)?.statisticsData
                                    as MutableList).removeAt(9)
                            (response[0]?.statistics?.get(0)?.statisticsData
                                    as MutableList).add(0, homePossession)
                            (response[0]?.statistics?.get(1)?.statisticsData
                                    as MutableList).add(0, awayPossession)

                            matchStatsRecyclerViewAdapter = MatchStatsRecyclerViewAdapter(
                                response[0]?.statistics?.get(0)?.statisticsData,
                                response[0]?.statistics?.get(1)?.statisticsData
                            )
                            binding.statisticsRecyclerView.adapter = matchStatsRecyclerViewAdapter
                            responseState.data.response[0]?.events.let { events ->
                                timelineAdapter.differ.submitList(events)
                                binding.stickyTimeline.addItemDecoration(getSectionCallback(events))
                            }
                            binding.stickyTimeline.visibility = View.VISIBLE
                            binding.statisticLayout.visibility = View.VISIBLE
                            binding.errorLayout.root.visibility = View.GONE
                        }
                        else {
                            handleEmptyDataState()
                        }
                    }
                }
                is ResponseState.Loading -> {
                    handleLoadingState()
                }
                is ResponseState.Error -> {
                    handleErrorState(responseState.message!!)
//                    binding.errorLayout.errorText.text = responseState.message
//                    binding.errorLayout.retryButton.setOnClickListener {
//                        activity.getFixtureById(activity.args)
//                    }
//                    binding.errorLayout.root.visibility = View.VISIBLE
//                    binding.statisticLayout.visibility = View.GONE
//                    binding.loadingIndicator.visibility = View.GONE
//                    binding.stickyTimeline.visibility = View.GONE
                }
            }
        }
    }

    private fun handleErrorState(errorMessage: String) {
        binding.errorLayout.errorText.text = errorMessage
        binding.errorLayout.retryButton.setOnClickListener {
            activity.getFixtureById(activity.args)
        }
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.statisticLayout.visibility = View.GONE
        binding.loadingIndicator.visibility = View.GONE
        binding.stickyTimeline.visibility = View.GONE
    }

    private fun handleLoadingState() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.stickyTimeline.visibility = View.GONE
        binding.statisticLayout.visibility = View.GONE
        binding.errorLayout.root.visibility = View.GONE
    }

    private fun handleEmptyDataState() {
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.errorLayout.errorText.text = context?.resources?.getString(R.string.data_not_provided)
        binding.errorLayout.retryButton.setOnClickListener {
            try { activity.getFixtureById(activity.args) }
            catch (_: InvocationTargetException) {}
        }
        binding.stickyTimeline.visibility = View.GONE
        binding.statisticLayout.visibility = View.GONE
        binding.loadingIndicator.visibility = View.GONE
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