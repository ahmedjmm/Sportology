package com.mobile.sportology.views.adapters.matchDetailsAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sportology.databinding.OtherStatsLayoutBinding
import com.mobile.sportology.databinding.PossessionStatisticLayoutBinding
import com.mobile.sportology.models.football.FixtureById

class MatchStatsRecyclerViewAdapter(
    private val homeStatisticsData: List<FixtureById.Response.Statistic.StatisticData?>?,
    private val awayStatisticsData: List<FixtureById.Response.Statistic.StatisticData?>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class PossessionStatisticViewHolder(private val _itemViewBinding: PossessionStatisticLayoutBinding) :
        RecyclerView.ViewHolder(_itemViewBinding.root) {
        fun bind(awayStatisticsData: FixtureById.Response.Statistic.StatisticData?,
                 homeStatisticsData: FixtureById.Response.Statistic.StatisticData?) {
            _itemViewBinding.awayStatistic?.text = awayStatisticsData?.value
            _itemViewBinding.homeStatistic?.text = homeStatisticsData?.value
            var awayPossession = 0
            awayStatisticsData?.value?.let {
                awayPossession = it.removeSuffix("%").toIntOrNull() ?: 0
            }
            _itemViewBinding.possessionProgress.max = 100
            _itemViewBinding.possessionProgress.progress = awayPossession
        }
    }

    inner class OtherStatsViewHolder(private val _itemViewBinding: OtherStatsLayoutBinding) :
        RecyclerView.ViewHolder(_itemViewBinding.root) {
        fun bind(
            position: Int,
            homeStats: List<FixtureById.Response.Statistic.StatisticData?>?,
            awayStats: List<FixtureById.Response.Statistic.StatisticData?>?
        ) {
            val homeStatistic = homeStats?.get(position)
            val awayStatistic = awayStats?.get(position)

            _itemViewBinding.homeStatisticText.text = homeStatistic?.value
            _itemViewBinding.awayStatisticText.text = awayStatistic?.value
            _itemViewBinding.statisticsType.text = awayStatistic?.type

            val homeProgress = homeStatistic?.value?.removeSuffix("%")?.toIntOrNull() ?: 0
            val awayProgress = awayStatistic?.value?.removeSuffix("%")?.toIntOrNull() ?: 0

            // Set max values for progress bars only once
            when (awayStats?.get(position)?.type) {
                "Passes %" -> {
                    _itemViewBinding.progressHome.max = 100
                    _itemViewBinding.progressAway.max = 100
                }
                "Passes accurate" -> {
                    _itemViewBinding.progressHome.max =
                        (homeStats?.get(position-1)?.value?.removeSuffix("%")?.toIntOrNull() ?: 0)
                    _itemViewBinding.progressAway.max =
                        (awayStats[position-1]?.value?.removeSuffix("%")?.toIntOrNull() ?: 0)
                }
                else -> {
                    _itemViewBinding.progressHome.max =
                        homeProgress + (awayStats?.get(position)?.value?.
                        removeSuffix("%")?.toIntOrNull() ?: 0)
                    _itemViewBinding.progressAway.max =
                        awayProgress + (homeStats?.get(position)?.value?.
                        removeSuffix("%")?.toIntOrNull() ?: 0)
                }
            }
            _itemViewBinding.progressHome.progress = homeProgress
            _itemViewBinding.progressAway.progress = awayProgress
        }
    }

    private val POSSESSION_STATISTIC_VIEW_TYPE = 0
    private val OTHERS_STATS_VIEW_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val possessionStatisticBinding =
            PossessionStatisticLayoutBinding.inflate(LayoutInflater.from(parent.context))
        val otherStatsBinding = OtherStatsLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return when (viewType) {
            POSSESSION_STATISTIC_VIEW_TYPE -> PossessionStatisticViewHolder(possessionStatisticBinding)
            else -> OtherStatsViewHolder(otherStatsBinding)
        }
    }

    override fun getItemCount(): Int = (awayStatisticsData?.size ?: 0) - 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PossessionStatisticViewHolder -> holder.bind(homeStatisticsData?.get(0), awayStatisticsData?.get(0))
            is OtherStatsViewHolder -> holder.bind(position, homeStatisticsData, awayStatisticsData)
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (position == 0) POSSESSION_STATISTIC_VIEW_TYPE
        else OTHERS_STATS_VIEW_TYPE
}