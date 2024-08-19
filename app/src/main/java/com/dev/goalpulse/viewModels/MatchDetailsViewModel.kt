package com.dev.goalpulse.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.dev.goalpulse.R
import com.google.gson.JsonSyntaxException
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.Shared
import com.dev.goalpulse.models.football.FixtureById
import com.dev.goalpulse.models.football.Standings
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
    private val _fixtureByIdMutableLiveData = MutableLiveData<ResponseState<FixtureById>>()
    private val _standingsMutableLiveData = MutableLiveData<ResponseState<Standings>>()
    val standingsLiveData: LiveData<ResponseState<Standings>> = _standingsMutableLiveData
    val fixtureByIdLiveData: LiveData<ResponseState<FixtureById>> = _fixtureByIdMutableLiveData.map {
        when(it) {
            is ResponseState.Success -> {
                if(it.data?.response?.get(0)?.statistics?.isNotEmpty() == true) {
                    val homePossession = (it.data.response[0]?.statistics?.get(0)?.statisticsData
                            as MutableList).removeAt(9)
                    (it.data.response[0]?.statistics?.get(0)?.statisticsData
                            as MutableList).add(0, homePossession)
                    val awayPossession = (it.data.response[0]?.statistics?.get(1)?.statisticsData
                            as MutableList).removeAt(9)
                    (it.data.response[0]?.statistics?.get(1)?.statisticsData
                            as MutableList).add(0, awayPossession)
                }
                else {
                    it.data?.response?.get(0)?.goals?.home = 0
                    it.data?.response?.get(0)?.goals?.away = 0
                    it.data?.response?.get(0)?.fixture?.referee = ""
                }
                return@map it
            }
            is ResponseState.Loading -> ResponseState.Loading()
            is ResponseState.Error -> {
                it.data?.response?.get(0)?.goals?.home = 0
                it.data?.response?.get(0)?.goals?.away = 0
                it.data?.response?.get(0)?.fixture?.referee = ""
                it.data?.response?.get(0)?.fixture?.venue?.name = ""
                it.data?.response?.get(0)?.fixture?.venue?.city = ""
                return@map ResponseState.Error(it.message!!)
            }
        }
    }
    var teamLineups = 0

    fun getFixtureById(timeZone: String, fixtureId: Int = 867946) {
        _fixtureByIdMutableLiveData.postValue(ResponseState.Loading())
        if(Shared.isConnected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = remoteRepository.getFixtureById(timeZone, fixtureId)
                    _fixtureByIdMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
                }
                catch (exception: Exception) {
                    handleFixtureByIdExceptions(exception)
                }
            }
        }
        else _fixtureByIdMutableLiveData.postValue(ResponseState.Error(app.getString(R.string.unable_to_connect)))
    }

    suspend fun getStandings(leagueId: Int, season: Int) = viewModelScope.launch(Dispatchers.IO) {
        _standingsMutableLiveData.postValue(ResponseState.Loading())
        if(Shared.isConnected) {
            try {
                val response = remoteRepository.getStandings(season = season, leagueId = leagueId)
                _standingsMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
            }
            catch (exception: Exception) {
                Log.e("getStandings", exception.toString())
                handleStandingsExceptions(exception)
            }
        }
        else _standingsMutableLiveData.postValue(ResponseState.Error(app.getString(R.string.unable_to_connect)))
    }

    private fun handleFixtureByIdExceptions(exception: Exception) {
        Log.e("handleFixtureByIdExceptions()", exception.message!!)
        val message = when(exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        _fixtureByIdMutableLiveData.postValue(ResponseState.Error(message))
    }

    private fun handleStandingsExceptions(exception: Exception) {
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
}
