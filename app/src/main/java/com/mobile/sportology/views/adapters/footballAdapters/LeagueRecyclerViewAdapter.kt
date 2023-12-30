package com.mobile.sportology.views.adapters.footballAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sportology.databinding.LeagueMatchDateItemBinding
import com.mobile.sportology.viewModels.FootBallViewModel
import kotlinx.android.synthetic.main.league_match_date_item.view.childRecyclerView

class LeagueRecyclerViewAdapter(
    private val viewModel: FootBallViewModel,
    private val leaguePosition: Int,
    private val onCheckedChangeListener: MatchRecyclerViewAdapter.OnCheckedChangeListener,
): RecyclerView.Adapter<LeagueRecyclerViewAdapter.DateViewHolder>() {

    private var datePosition = 0
    private val viewPool = RecyclerView.RecycledViewPool()
    private val _diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean = oldItem == newItem
    }
    val differ = AsyncListDiffer(this, _diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val leagueMatchDateItemBinding = LeagueMatchDateItemBinding.inflate(inflater, parent, false)
        val dateViewHolder = DateViewHolder(leagueMatchDateItemBinding)
        dateViewHolder.itemView.childRecyclerView.setRecycledViewPool(viewPool)
        val matchRecyclerViewAdapter = MatchRecyclerViewAdapter(parent.context)
        matchRecyclerViewAdapter.onCheckedChangeListener = onCheckedChangeListener
        leagueMatchDateItemBinding.childRecyclerView.apply {
            adapter = matchRecyclerViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        matchRecyclerViewAdapter.differ.submitList(
            viewModel.getMatchesOfLeague(date = differ.currentList[datePosition], position = leaguePosition)
        )
        datePosition++
        return dateViewHolder
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int = position

    /////////////////////////////////////////////////////////////////////////////////////////////////

    inner class DateViewHolder(
        private val _itemViewBinding: LeagueMatchDateItemBinding,
    ): RecyclerView.ViewHolder(_itemViewBinding.root) {
        fun bind(item: String) {
            _itemViewBinding.date = item
        }
    }
}