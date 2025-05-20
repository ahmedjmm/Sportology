package com.dev.goalpulse.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.dev.goalpulse.R
import com.google.gson.JsonSyntaxException
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.Shared
import com.dev.goalpulse.dataUtils.StatisticsProcessor
import com.dev.goalpulse.models.football.MatchGraphs
import com.dev.goalpulse.models.football.MatchPositions
import com.dev.goalpulse.models.football.MatchStatistics
import com.dev.goalpulse.models.football.Standing
import com.dev.goalpulse.repositories.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


@HiltViewModel
class MatchDetailsViewModel @Inject constructor(
    private val app: Application,
    private val remoteRepository: RemoteRepository,
): ViewModel() {
    private val _standingsMutableLiveData = MutableLiveData<ResponseState<Standing>>()
    val standingsLiveData: LiveData<ResponseState<Standing>> =
        _standingsMutableLiveData.map { responseState ->
            when(responseState) {
                is ResponseState.Success -> {
                    if(responseState.data.isNullOrEmpty())
                        ResponseState.Success(data = null, message = app.getString(R.string.data_not_provided))
                    else {
                        val myCompetitor = responseState.data.first().competitors?.reversed()

                        val myStanding = Standing().apply {
                            add(
                                Standing.StandingItem(
                                    id = responseState.data.first().id,
                                    tournamentId = responseState.data.first().tournamentId,
                                    tournamentName = responseState.data.first().tournamentName,
                                    type = responseState.data.first().type,
                                    name = responseState.data.first().name,
                                    seasonId = responseState.data.first().seasonId,
                                    seasonName = responseState.data.first().seasonName,
                                    leagueId = responseState.data.first().leagueId,
                                    leagueName = responseState.data.first().leagueName,
                                    leagueHashImage = responseState.data.first().leagueHashImage,
                                    competitors = myCompetitor,
                                )
                            )
                        }
                        ResponseState.Success(data = myStanding)
                    }
                }
                is ResponseState.Loading -> ResponseState.Loading()
                is ResponseState.Error -> ResponseState.Error(message = responseState.message!!)
            }
        }

    private val _matchStatisticsMutableLiveData = MutableLiveData<ResponseState<MatchStatistics>>()
    val matchStatisticsLiveData: LiveData<ResponseState<out MatchStatistics>> =
        _matchStatisticsMutableLiveData.map { responseState ->
            when (responseState) {
                is ResponseState.Success -> {
                    if(responseState.data.isNullOrEmpty())
                        ResponseState.Success(data = null, message = app.getString(R.string.data_not_provided))
                    else
                        StatisticsProcessor.processStatistics(
                            responseState.data[0].statistics
                        )
                }
                is ResponseState.Loading -> ResponseState.Loading()
                is ResponseState.Error -> ResponseState.Error(responseState.message!!)
            }
        }.distinctUntilChanged()

    private val _matchGraphsMutableLiveData = MutableLiveData<ResponseState<MatchGraphs>>()
    val matchGraphsLiveData: LiveData<ResponseState<MatchGraphs>> =
        _matchGraphsMutableLiveData.map { responseState ->
            when(responseState) {
                is ResponseState.Success -> {
                    if(responseState.data.isNullOrEmpty())
                        ResponseState.Success(data = null, message = app.getString(R.string.data_not_provided))
                    else {
                        val myMatchGraphs = MatchGraphs()
                        val points = responseState.data[0].points?.sortedBy { it?.minute }
                        val matchGraphsItem = MatchGraphs.MatchGraphsItem(
                            id = responseState.data[0].id,
                            periodTime = responseState.data[0].periodTime,
                            periodCount = responseState.data[0].periodCount,
                            points = points
                        )
                        myMatchGraphs.add(matchGraphsItem)
                        ResponseState.Success(data = myMatchGraphs)
                    }
                }
                is ResponseState.Loading -> ResponseState.Loading()
                is ResponseState.Error -> ResponseState.Error(message = responseState.message!!)
            }
        }

    private val _playerPositionsMutableLiveData = MutableLiveData<ResponseState<MatchPositions>>()
    val playerPositionsLiveData: LiveData<ResponseState<MatchPositions>> = _playerPositionsMutableLiveData

    fun getMatchPlayerPositions(matchId: String = "eq.2470375") {
        _playerPositionsMutableLiveData.postValue(ResponseState.Loading())
        if(Shared.isConnected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = remoteRepository.getMatchPlayersPositions(matchId)
                    if(response.data.isNullOrEmpty())
                        _playerPositionsMutableLiveData.postValue(ResponseState.Success(data = null,
                            message = app.getString(R.string.data_not_provided)))
                    else
                        _playerPositionsMutableLiveData.postValue(ResponseState.Success(response.data))
                }
                catch (exception: Exception) {
                    handleMatchPlayerPositionsException(exception)
                }
            }
        }
        else _playerPositionsMutableLiveData.postValue(ResponseState.Error(app.getString(R.string.unable_to_connect)))
    }

    private fun handleMatchPlayerPositionsException(exception: Exception) {
        Log.e("handleMatchPlayerPositionsException()", exception.toString())
        val message = when(exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        _playerPositionsMutableLiveData.postValue(ResponseState.Error(message))
    }

    fun getMatchStatistics(matchId: String = "eq.2470375") {
        _matchStatisticsMutableLiveData.postValue(ResponseState.Loading())
        if(Shared.isConnected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = remoteRepository.getMatchStatistics(matchId)
                    _matchStatisticsMutableLiveData.postValue(ResponseState.Success(response.data!!))
                }
                catch (exception: Exception) {
                    handleMatchStatisticsException(exception)
                }
            }
        }
        else _matchStatisticsMutableLiveData.postValue(ResponseState.Error(app.getString(R.string.unable_to_connect)))
    }

    fun getStandings(season: String) = viewModelScope.launch {
        _standingsMutableLiveData.postValue(ResponseState.Loading())
        if(Shared.isConnected) {
            try {
                val response = remoteRepository.getStandings(season = season)
                _standingsMutableLiveData.postValue(ResponseState.Success(response.data))
            }
            catch (exception: Exception) {
                Log.e("getStandings", exception.toString())
                handleStandingException(exception)
            }
        }
        else _standingsMutableLiveData.postValue(ResponseState.Error(app.getString(R.string.unable_to_connect)))
    }

    private fun handleMatchStatisticsException(exception: Exception) {
        Log.e("handleFixtureByIdExceptions()", exception.toString())
        val message = when(exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        _matchStatisticsMutableLiveData.postValue(ResponseState.Error(message))
    }

    private fun handleStandingException(exception: Exception) {
        Log.e("handleStandingsExceptions()", exception.message!!)
        val message = when(exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        _standingsMutableLiveData.postValue(ResponseState.Error(message))
    }

    fun convertArgumentFromIntToString(argument: Int): String = "eq.$argument"

    fun getMatchGraphs(graphId: String) = viewModelScope.launch(Dispatchers.IO) {
        _matchGraphsMutableLiveData.postValue(ResponseState.Loading())
            try {
                val response = remoteRepository.getMatchGraphs(graphId)
                _matchGraphsMutableLiveData.postValue(response)
            }
            catch (_: Exception) {

            }
        }

}
