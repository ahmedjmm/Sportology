package com.dev.goalpulse.views.adapters.matchDetailsAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dev.goalpulse.databinding.MatchStatisticsItemBinding
import com.dev.goalpulse.models.football.MatchStatistics

class MatchStatisticsRecyclerViewAdapter:
    RecyclerView.Adapter<MatchStatisticsRecyclerViewAdapter.MatchStatisticsViewHolder>() {

    val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<MatchStatistics.MatchStatisticsItem.Statistic>() {
            override fun areItemsTheSame(
                oldItem: MatchStatistics.MatchStatisticsItem.Statistic,
                newItem: MatchStatistics.MatchStatisticsItem.Statistic
            ): Boolean {
                return oldItem.type == newItem.type &&
                        oldItem.period == newItem.period &&
                        oldItem.awayTeam == newItem.awayTeam &&
                        oldItem.homeTeam == newItem.homeTeam
            }

            override fun areContentsTheSame(
                oldItem: MatchStatistics.MatchStatisticsItem.Statistic,
                newItem: MatchStatistics.MatchStatisticsItem.Statistic
            ): Boolean {
                return oldItem == newItem
            }
        })

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchStatisticsViewHolder {
        val matchStatisticsViewHolder = MatchStatisticsItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchStatisticsViewHolder(matchStatisticsViewHolder)
    }

    override fun onViewDetachedFromWindow(holder: MatchStatisticsViewHolder) {
        holder.itemView.animate().cancel()
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemId(position: Int): Long {
        return differ.currentList[position].run {
            "$type$period$awayTeam$homeTeam".hashCode().toLong()
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: MatchStatisticsViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    inner class MatchStatisticsViewHolder(private val _itemViewBinding: MatchStatisticsItemBinding) :
        RecyclerView.ViewHolder(_itemViewBinding.root) {

        fun bind(matchStatistic: MatchStatistics.MatchStatisticsItem.Statistic) {
            _itemViewBinding.statistic = matchStatistic
        }
    }
}