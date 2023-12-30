package com.mobile.sportology.views.adapters.footballAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sportology.databinding.AllSearchResultsItemBinding
import com.mobile.sportology.models.football.LeagueSearchResult
import com.mobile.sportology.models.football.TeamSearchResult
import com.mobile.sportology.views.viewsUtilities.imageBinding

class AllSearchResultsRecyclerViewAdapter(
    private val type: String,
    val allSearchResultsInterface: AllSearchResultsInterface
    ): RecyclerView.Adapter<AllSearchResultsRecyclerViewAdapter.SearchResultsViewHolder>() {
    private lateinit var allSearchResultsItemBinding: AllSearchResultsItemBinding

    inner class SearchResultsViewHolder(
        private val _itemView: AllSearchResultsItemBinding
    ): RecyclerView.ViewHolder(_itemView.root) {
        fun bindLeague(league: LeagueSearchResult.Response) {
            _itemView.itemName.text = league.league?.name
            _itemView.itemCountry.text = league.country?.name
            allSearchResultsInterface.isItemChecked(league) { isChecked ->
                _itemView.followCheck.isChecked = isChecked
            }
            _itemView.logo.apply {
                imageBinding(league.league?.logo)
            }
        }

        fun bindTeam(team: TeamSearchResult.Response) {
            _itemView.itemCountry.visibility = View.GONE
            _itemView.itemName.text = team.team?.name
            allSearchResultsInterface.isItemChecked(team) { isChecked ->
                _itemView.followCheck.isChecked = isChecked
            }
            _itemView.logo.apply {
                imageBinding(team.team?.logo)
            }
        }
    }

    interface AllSearchResultsInterface {
        suspend fun onFollowLeagueButtonClick(league: LeagueSearchResult.Response, isChecked: Boolean)

        suspend fun onFollowTeamButtonClick(team: TeamSearchResult.Response, isChecked: Boolean)

        fun isItemChecked(item: Any, callback: (Boolean) -> Unit)
    }

    private val _leaguesDiffCallback = object : DiffUtil.ItemCallback<LeagueSearchResult.Response>() {
        override fun areItemsTheSame(
            oldItem: LeagueSearchResult.Response,
            newItem: LeagueSearchResult.Response
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: LeagueSearchResult.Response,
            newItem: LeagueSearchResult.Response
        ): Boolean = oldItem == newItem
    }
    val leaguesDiffer = AsyncListDiffer(this, _leaguesDiffCallback)

    private val _teamsDiffCallback = object : DiffUtil.ItemCallback<TeamSearchResult.Response>() {
        override fun areItemsTheSame(
            oldItem: TeamSearchResult.Response,
            newItem: TeamSearchResult.Response
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: TeamSearchResult.Response,
            newItem: TeamSearchResult.Response
        ): Boolean = oldItem == newItem
    }
    val teamsDiffer = AsyncListDiffer(this, _teamsDiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        allSearchResultsItemBinding = AllSearchResultsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return SearchResultsViewHolder(allSearchResultsItemBinding)
    }

    override fun getItemCount(): Int = if(type == "Competitions") {
        leaguesDiffer.currentList.size
    } else {
        teamsDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: AllSearchResultsRecyclerViewAdapter.SearchResultsViewHolder, position: Int) {
        if(type == "Competitions")
            holder.bindLeague(leaguesDiffer.currentList[position])
        else
            holder.bindTeam(teamsDiffer.currentList[position])
    }
}