package com.mobile.sportology.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.mobile.sportology.R
import com.mobile.sportology.databinding.ActivityAllSearchResultsBinding
import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.models.football.LeagueSearchResult
import com.mobile.sportology.models.football.TeamRoom
import com.mobile.sportology.models.football.TeamSearchResult
import com.mobile.sportology.repositories.DefaultLocalRepository
import com.mobile.sportology.viewModels.AllSearchActivityViewModel
import com.mobile.sportology.viewModels.MyViewModelProvider
import com.mobile.sportology.views.adapters.footballAdapters.AllSearchResultsRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AllSearchResultsActivity : AppCompatActivity(),
    AllSearchResultsRecyclerViewAdapter.AllSearchResultsInterface {
    private lateinit var binding: ActivityAllSearchResultsBinding
    @Inject
    lateinit var defaultLocalRepository: DefaultLocalRepository
    private val viewModel: AllSearchActivityViewModel by lazy {
        ViewModelProvider(this,
            MyViewModelProvider(application, defaultLocalRepository = defaultLocalRepository,
                remoteRepository = null))[AllSearchActivityViewModel::class.java]
    }
    private lateinit var queryType: String
    private lateinit var queryValue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllSearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        queryType = intent.getStringExtra("queryType")!!
        queryValue = intent.getStringExtra("queryValue")!!

        binding.toolbar.title = "$queryType: $queryValue"

        val allSearchResultsRecyclerViewAdapter = AllSearchResultsRecyclerViewAdapter(
            queryType, this)
        allSearchResultsRecyclerViewAdapter.apply {
            if(queryType == resources.getString(R.string.competitions)){
                val queryResult = intent.getSerializableExtra("queryResult") as List<LeagueSearchResult.Response>
                leaguesDiffer.submitList(queryResult)
            } else{
                val queryResult = intent.getSerializableExtra("queryResult") as List<TeamSearchResult.Response>
                teamsDiffer.submitList(queryResult)
            }
        }
        binding.searchResults.apply {
            adapter = allSearchResultsRecyclerViewAdapter
            layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
            val divider = MaterialDividerItemDecoration(this@AllSearchResultsActivity,
                LinearLayoutManager.VERTICAL)
            divider.isLastItemDecorated = false
            addItemDecoration(divider)
        }
    }

    override suspend fun onFollowLeagueButtonClick(
        league: LeagueSearchResult.Response,
        isChecked: Boolean
    ) {
        if(isChecked) {
            val myLeague = LeagueRoom(id = league.league?.id!!, name = league.league.name!!,
                country = league.country?.name!!, league.league.logo!!)
            viewModel.upsertLeague(myLeague)
        }
        else {
            val myLeague = LeagueRoom(id = league.league?.id!!, name = league.league.name!!,
                country = league.country?.name!!, league.league.logo!!)
            viewModel.deleteLeague(myLeague)
        }
    }

    override fun isItemChecked(item: Any, callback: (Boolean)-> Unit) {
        if(queryType == resources.getString(R.string.competitions)) {
            val league = item as LeagueSearchResult.Response
            lifecycleScope.launch(Dispatchers.IO) {
                val leagueRoom = viewModel.getLeagueById(league.league?.id!!)
                val isChecked = leagueRoom != null
                withContext(Dispatchers.Main) {
                    callback(isChecked)
                }
            }
        } else {
            val team = item as TeamSearchResult.Response
            lifecycleScope.launch(Dispatchers.IO) {
                val teamRoom = viewModel.getTeamById(team.team?.id!!)
                val isChecked = teamRoom != null
                withContext(Dispatchers.Main) {
                    callback(isChecked)
                }
            }
        }
    }

    override suspend fun onFollowTeamButtonClick(team: TeamSearchResult.Response, isChecked: Boolean) {
        if(isChecked) {
            val myTeam = TeamRoom(id = team.team?.id!!, name = team.team.name!!, team.team.logo!!)
            viewModel.upsertTeam(myTeam)
        }
        else {
            val myTeam = TeamRoom(id = team.team?.id!!, name = team.team.name!!, team.team.logo!!)
            viewModel.deleteTeam(myTeam)
        }
    }
}