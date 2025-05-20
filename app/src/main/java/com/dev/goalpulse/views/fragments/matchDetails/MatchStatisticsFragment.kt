package com.dev.goalpulse.views.fragments.matchDetails

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.goalpulse.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.databinding.ErrorLayoutBinding
import com.dev.goalpulse.models.football.MatchStatistics
import com.dev.goalpulse.viewModels.MatchDetailsViewModel
import com.dev.goalpulse.views.activities.MatchDetailsActivity
import com.dev.goalpulse.views.adapters.matchDetailsAdapters.MatchStatisticsRecyclerViewAdapter
import com.dev.goalpulse.views.viewsUtilities.ViewCrossFadeAnimation
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import com.dev.goalpulse.models.football.MatchGraphs
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.card.MaterialCardView
import kotlin.math.abs

class MatchStatisticsFragment: Fragment(R.layout.fragment_match_statistics), ViewCrossFadeAnimation {
    private lateinit var matchStatisticsRecyclerViewAdapter: MatchStatisticsRecyclerViewAdapter
    private lateinit var statisticLayout: LinearLayout
    private lateinit var timeLineChartView: LineChart
    private lateinit var statisticsRecyclerView: RecyclerView
    private lateinit var circularProgress: CircularProgressIndicator
    private var _errorLayoutBinding: ErrorLayoutBinding? = null

    lateinit var activity: MatchDetailsActivity

    private lateinit var matchDetailsViewModel: MatchDetailsViewModel

    override var shortAnimationDuration = 0


    // Expandable card views (nullable)
    private var chartCard: MaterialCardView? = null
    private var chartHeader: LinearLayout? = null
    private var expandCollapseIcon: ImageView? = null

    private var isChartExpanded = false

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
    ): View = inflater.inflate(R.layout.fragment_match_statistics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeBaseViews(view)
        initializeObservers()
        setupChartView(view.findViewById(R.id.chart_container))
    }

    private fun initializeObservers() {
        matchDetailsViewModel.matchStatisticsLiveData.observe(viewLifecycleOwner) { responseState ->
            when(responseState) {
                is ResponseState.Success -> handleSuccessState(responseState)
                is ResponseState.Loading -> handleLoadingState()
                is ResponseState.Error -> handleErrorState(responseState.message!!)
            }
        }

        matchDetailsViewModel.matchGraphsLiveData.observe(viewLifecycleOwner) { responseState ->
            when(responseState) {
                is ResponseState.Success -> {
                    responseState.data?.firstOrNull()?.points?.let { points ->
                        handleTimeLineGraph(points)
                    }
                }
                is ResponseState.Loading -> handleLoadingState()
                is ResponseState.Error -> handleErrorState(responseState.message!!)
            }
        }
    }

    private fun initializeBaseViews(view: View) {
        _errorLayoutBinding = ErrorLayoutBinding.bind(view.findViewById(R.id.error_layout))
        circularProgress = view.findViewById(R.id.loadingIndicator)
        statisticLayout = view.findViewById(R.id.statistic_layout)

        matchStatisticsRecyclerViewAdapter = MatchStatisticsRecyclerViewAdapter()
        statisticsRecyclerView = view.findViewById<RecyclerView>(R.id.statistics_recycler_view).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = matchStatisticsRecyclerViewAdapter
        }
    }

    private fun setupChartView(container: ViewGroup) {
        container.removeAllViews() // Clear any existing views

        val chartLayout = if (shouldShowExpandableCard()) {
            R.layout.chart_expandable
        } else {
            R.layout.chart_full
        }

        LayoutInflater.from(requireContext()).inflate(chartLayout, container, true)

        // Initialize the chart view from the inflated layout
        timeLineChartView = container.findViewById<LineChart>(R.id.line_chart).apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            xAxis.position = XAxis.XAxisPosition.TOP
            axisRight.isEnabled = false
            legend.isEnabled = true
            data = LineData()
            if (shouldShowExpandableCard()) hideViewWithAnimation(this)
            else showViewWithAnimation(this)
        }

        // Initialize expandable views if needed
        if (shouldShowExpandableCard()) {
            chartCard = container.findViewById(R.id.chartCard)
            chartHeader = container.findViewById(R.id.chartHeader)
            expandCollapseIcon = container.findViewById(R.id.expandCollapseIcon)
            setupExpandableBehavior()
        }
    }

    private fun toggleChartVisibility() {
        TransitionManager.beginDelayedTransition(chartCard ?: return)
        if (isChartExpanded) showViewWithAnimation(timeLineChartView)
        else hideViewWithAnimation(timeLineChartView)
        expandCollapseIcon?.setImageResource(
            if (isChartExpanded) R.drawable.ic_expand_less
            else R.drawable.ic_expand_more
        )
    }

    private fun setupExpandableBehavior() {
        chartHeader?.setOnClickListener {
            isChartExpanded = !isChartExpanded
            toggleChartVisibility()
        }
    }

    private fun shouldShowExpandableCard(): Boolean {
        val config = resources.configuration
        val isTabletPortrait = config.smallestScreenWidthDp >= 600 &&
                config.orientation == Configuration.ORIENTATION_PORTRAIT
        return !isTabletPortrait
    }

    private fun handleTimeLineGraph(points: List<MatchGraphs.MatchGraphsItem.Point?>) {
        val entries = processData(points)
        renderGraphUI(entries)
        timeLineChartView.visibility = View.VISIBLE
        timeLineChartView.invalidate()
    }


    private fun renderGraphUI(entries: Array<ArrayList<Entry>>) {
        val homeSet = LineDataSet(entries[0], "Home Pressure").apply {
            color = Color.BLUE
            lineWidth = 2f
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            fillAlpha = 110
            setDrawFilled(true)
            fillColor = Color.BLUE
        }

        val awaySet = LineDataSet(entries[1], "Away Pressure").apply {
            color = Color.RED
            lineWidth = 2f
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            fillAlpha = 110
            setDrawFilled(true)
            fillColor = Color.RED
        }

        // Combine sets and set data
        val data = LineData(homeSet, awaySet)
        timeLineChartView.apply {
            this.data = data
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            xAxis.position = XAxis.XAxisPosition.TOP
            axisRight.isEnabled = false
            legend.isEnabled = true
            // Clear any previous animations
            clearAnimation()

            // Update the chart with the new data
            notifyDataSetChanged()
            // Post the animation to ensure the view is laid out
            post {
                animateX(800)
                animateY(800)
            }
        }
        showViewWithAnimation(timeLineChartView)
    }

    private fun processData(points: List<MatchGraphs.MatchGraphsItem.Point?>): Array<ArrayList<Entry>> {
        val homeEntries = ArrayList<Entry>()
        val awayEntries = ArrayList<Entry>()
        points.forEach { point ->
            val minute = point?.minute?.toFloat()
            when {
                point!!.value!! > 0 -> homeEntries.add(Entry(minute!!, point.value!!.toFloat()))
                point.value!! < 0 -> awayEntries.add(Entry(minute!!, abs(point.value.toFloat())))
            }
        }
        // Sort entries by X value (minute)
        homeEntries.sortBy { it.x }
        awayEntries.sortBy { it.x }
        return arrayOf(homeEntries, awayEntries)
    }

    // Override onResume to refresh chart if data exists
    override fun onResume() {
        super.onResume()

        // If the chart already has data, refresh it
        if (::timeLineChartView.isInitialized &&
            timeLineChartView.data != null &&
            timeLineChartView.data.dataSetCount > 0) {

            timeLineChartView.notifyDataSetChanged()
            timeLineChartView.invalidate()
        }
    }

    override fun onDestroyView() {
        if (::timeLineChartView.isInitialized) {
            timeLineChartView.clear()
        }
        _errorLayoutBinding = null
        super.onDestroyView()
    }

    override fun hideViewWithAnimation(view: View) {
        if (view.isVisible) {
            view.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (isAdded) { // Fragment safety check
                            view.visibility = View.GONE
                        }
                    }
                })
        }
    }

    override fun showViewWithAnimation(view: View) {
        if (view.visibility != View.VISIBLE) {
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
                .start()
        }
    }

    private fun handleSuccessState(responseState: ResponseState<out MatchStatistics>) {
        // Cancel any ongoing animations to prevent conflicts
        statisticLayout.animate().cancel()
        circularProgress.animate().cancel()
        _errorLayoutBinding?.root?.animate()?.cancel()

        if(responseState.data != null) {
            hideViewWithAnimation(circularProgress)
            hideViewWithAnimation(_errorLayoutBinding?.root!!)
            val stats = responseState.data[0].statistics
            statisticsRecyclerView.post {
                matchStatisticsRecyclerViewAdapter.differ.submitList(stats)
                showViewWithAnimation(statisticLayout)
            }
        }
        else handleEmptyDataState()
    }

    private fun handleErrorState(errorMessage: String) {
        hideViewWithAnimation(statisticLayout)
        hideViewWithAnimation(circularProgress)
        _errorLayoutBinding!!.apply {
            errorText.text = errorMessage
            retryButton.setOnClickListener { activity.getMatchStatistics() }
            showViewWithAnimation(this.root)
        }
    }

    private fun handleLoadingState() {
        hideViewWithAnimation(statisticLayout)
        hideViewWithAnimation(_errorLayoutBinding!!.root)
        showViewWithAnimation(circularProgress)
    }

    private fun handleEmptyDataState() {
        hideViewWithAnimation(statisticLayout)
        hideViewWithAnimation(circularProgress)
        _errorLayoutBinding!!.apply {
            errorText.text = context?.resources?.getString(R.string.data_not_provided)
            retryButton.setOnClickListener { activity.getMatchStatistics() }
            showViewWithAnimation(this.root)
        }
    }
}