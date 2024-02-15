package com.mobile.sportology.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.Shared
import com.mobile.sportology.models.football.Fixtures
import com.mobile.sportology.models.football.League
import com.mobile.sportology.models.football.MatchNotificationRoom
import com.mobile.sportology.repositories.DefaultLocalRepository
import com.mobile.sportology.repositories.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


@HiltViewModel
class FootBallViewModel @Inject constructor(
    private val app: Application,
    private val remoteRepository: RemoteRepository,
    private val defaultLocalRepository: DefaultLocalRepository
): ViewModel() {
    private val leagues: MutableList<League> = mutableListOf()
    private val _leaguesMutableLiveData = MutableLiveData<MutableList<League>>()
    val leaguesLiveData: LiveData<MutableList<League>> = _leaguesMutableLiveData
    private val _englandPremierLeagueMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val englandPremierLeagueMatchesLiveData = _englandPremierLeagueMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        val leagueMatches = mutableListOf<Any>()
        when (responseState) {
            is ResponseState.Success -> {
                if(responseState.data?.response?.isEmpty() == true)
                    return@map ResponseState.Error(app.getString(R.string.no_live_matches))

                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                for(datesIndex in matchesDates.indices) {
                    leagueMatches.add(matchesDates[datesIndex])
                    for(fixtureIndex in responseState.data?.response!!.indices) {
                        if(responseState.data.response[fixtureIndex]?.fixture?.date!!.contains(matchesDates[datesIndex]))
                            leagueMatches.add(responseState.data.response[fixtureIndex]!!)
                    }
                }
                return@map ResponseState.Success(leagueMatches)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _laLigaMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val laLigaMatchesLiveData = _laLigaMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        val leagueMatches = mutableListOf<Any>()
        when (responseState) {
            is ResponseState.Success -> {
                if(responseState.data?.response?.isEmpty() == true)
                    return@map ResponseState.Error(app.getString(R.string.no_live_matches))

                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                for(datesIndex in matchesDates.indices) {
                    leagueMatches.add(matchesDates[datesIndex])
                    for(fixtureIndex in responseState.data?.response!!.indices) {
                        if(responseState.data.response[fixtureIndex]?.fixture?.date!!.contains(matchesDates[datesIndex]))
                            leagueMatches.add(responseState.data.response[fixtureIndex]!!)
                    }
                }
                return@map ResponseState.Success(leagueMatches)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _ligue1MatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val ligue1MatchesLiveData = _ligue1MatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        val leagueMatches = mutableListOf<Any>()
        when (responseState) {
            is ResponseState.Success -> {
                if(responseState.data?.response?.isEmpty() == true)
                    return@map ResponseState.Error(app.getString(R.string.no_live_matches))

                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                for(datesIndex in matchesDates.indices) {
                    leagueMatches.add(matchesDates[datesIndex])
                    for(fixtureIndex in responseState.data?.response!!.indices) {
                        if(responseState.data.response[fixtureIndex]?.fixture?.date!!.contains(matchesDates[datesIndex]))
                            leagueMatches.add(responseState.data.response[fixtureIndex]!!)
                    }
                }
                return@map ResponseState.Success(leagueMatches)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _serieAMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val serieAMatchesLiveData = _serieAMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        val leagueMatches = mutableListOf<Any>()
        when (responseState) {
            is ResponseState.Success -> {
                if(responseState.data?.response?.isEmpty() == true)
                    return@map ResponseState.Error(app.getString(R.string.no_live_matches))

                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                for(datesIndex in matchesDates.indices) {
                    leagueMatches.add(matchesDates[datesIndex])
                    for(fixtureIndex in responseState.data?.response!!.indices) {
                        if(responseState.data.response[fixtureIndex]?.fixture?.date!!.contains(matchesDates[datesIndex]))
                            leagueMatches.add(responseState.data.response[fixtureIndex]!!)
                    }
                }
                return@map ResponseState.Success(leagueMatches)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _bundesLigaMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val bundesLigaMatchesLiveData = _bundesLigaMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        val leagueMatches = mutableListOf<Any>()
        when (responseState) {
            is ResponseState.Success -> {
                if(responseState.data?.response?.isEmpty() == true)
                    return@map ResponseState.Error(app.getString(R.string.no_live_matches))

                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                for(datesIndex in matchesDates.indices) {
                    leagueMatches.add(matchesDates[datesIndex])
                    for(fixtureIndex in responseState.data?.response!!.indices) {
                        if(responseState.data.response[fixtureIndex]?.fixture?.date!!.contains(matchesDates[datesIndex]))
                            leagueMatches.add(responseState.data.response[fixtureIndex]!!)
                    }
                }
                return@map ResponseState.Success(leagueMatches)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _egyptianPremierLeagueMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val egyptianPremierLeagueMatchesLiveData = _egyptianPremierLeagueMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        val leagueMatches = mutableListOf<Any>()
        when (responseState) {
            is ResponseState.Success -> {
                if(responseState.data?.response?.isEmpty() == true)
                    return@map ResponseState.Error(app.getString(R.string.no_live_matches))

                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                for(datesIndex in matchesDates.indices) {
                    leagueMatches.add(matchesDates[datesIndex])
                    for(fixtureIndex in responseState.data?.response!!.indices) {
                        if(responseState.data.response[fixtureIndex]?.fixture?.date!!.contains(matchesDates[datesIndex]))
                            leagueMatches.add(responseState.data.response[fixtureIndex]!!)
                    }
                }
                return@map ResponseState.Success(leagueMatches)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if(Shared.isConnected) {
                Shared.LEAGUES_IDS.forEach { leagueId ->
                    when(val responseState = getLeague(leagueId)) {
                        is ResponseState.Success -> {
                            if(Shared.isLiveMatches)
                                responseState.data?.body()?.response?.get(0)?.seasons?.get(0)?.year?.let { season ->
                                    getLeagueMatches(
                                        leagueId,
                                        season,
                                        app.getString(R.string.live_matches)
                                    )
                                }
                            else
                                responseState.data?.body()?.response?.get(0)?.seasons?.get(0)?.year?.let { season ->
                                    getLeagueMatches(
                                        leagueId,
                                        season,
                                        null
                                    )
                                }
                        }
                        is ResponseState.Loading -> {}
                        is ResponseState.Error -> {
                            val error = responseState.message
                            _englandPremierLeagueMatchesMutableLiveData.postValue(ResponseState.Error(error!!))
                            _laLigaMatchesMutableLiveData.postValue(ResponseState.Error(error))
                            _ligue1MatchesMutableLiveData.postValue(ResponseState.Error(error))
                            _serieAMatchesMutableLiveData.postValue(ResponseState.Error(error))
                            _bundesLigaMatchesMutableLiveData.postValue(ResponseState.Error(error))
                            _egyptianPremierLeagueMatchesMutableLiveData.postValue(ResponseState.Error(error))
                        }
                    }
                }
                _leaguesMutableLiveData.postValue(leagues)
            }
        }
    }

    suspend fun getLeague(id: Int): ResponseState<out Response<League>?> = viewModelScope.async(Dispatchers.IO) {
        if (Shared.isConnected) {
            try {
                val response = remoteRepository.getLeague(id)
                response?.body()?.let { leagues.add(it) }
                return@async ResponseState.Success(response)
            } catch (exception: Exception) {
                return@async handleLeaguesException(exception)
            }
        }
        else {
            return@async ResponseState.Error(app.getString(R.string.unable_to_connect))
        }
    }.await()

    suspend fun getLeagueMatches(leagueId: Int, season: Int, liveMatches: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            when(leagueId) {
                39 -> _englandPremierLeagueMatchesMutableLiveData.postValue(ResponseState.Loading())
                140 -> _laLigaMatchesMutableLiveData.postValue(ResponseState.Loading())
                61 -> _ligue1MatchesMutableLiveData.postValue(ResponseState.Loading())
                135 -> _serieAMatchesMutableLiveData.postValue(ResponseState.Loading())
                78 -> _bundesLigaMatchesMutableLiveData.postValue(ResponseState.Loading())
                233 -> _egyptianPremierLeagueMatchesMutableLiveData.postValue(ResponseState.Loading())
            }
            try {
                val response = liveMatches?.let {
                    remoteRepository.getLeagueLiveMatches(
                        leagueId = leagueId,
                        season = season,
                        liveMatches = it
                    )
                } ?: remoteRepository.getLeagueMatches(leagueId, season)
                when(leagueId) {
                    39 -> _englandPremierLeagueMatchesMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
                    140 -> _laLigaMatchesMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
                    61 -> _ligue1MatchesMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
                    135 -> _serieAMatchesMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
                    78 ->_bundesLigaMatchesMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
                    233 -> _egyptianPremierLeagueMatchesMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
                }
            }
            catch (exception: Exception) {
                when(leagueId) {
                    39 -> handleMatchesException(_englandPremierLeagueMatchesMutableLiveData, exception)
                    140 -> handleMatchesException(_laLigaMatchesMutableLiveData, exception)
                    61 -> handleMatchesException(_ligue1MatchesMutableLiveData, exception)
                    135 -> handleMatchesException(_serieAMatchesMutableLiveData, exception)
                    78 -> handleMatchesException(_bundesLigaMatchesMutableLiveData, exception)
                    233 -> handleMatchesException(_egyptianPremierLeagueMatchesMutableLiveData, exception)
                }
            }
        }.join()
    }

    private fun handleMatchesException(
        liveData: MutableLiveData<ResponseState<Fixtures>>,
        exception: Exception
    ) {
        val errorMessage = when (exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        Log.e("handleMatchesException()", exception.toString())
        liveData.postValue(ResponseState.Error(errorMessage))
    }

    private fun handleLeaguesException(exception: Exception): ResponseState<Response<League>> {
        val errorMessage = when (exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        Log.e("handleLeagueException()", exception.toString())
        return ResponseState.Error(errorMessage)
    }

    suspend fun getMatchNotificationById(matchId: Int) = defaultLocalRepository.getMatchNotificationById(matchId)

    suspend fun getAllMatchNotifications() = defaultLocalRepository.getAllMatchNotifications()

    suspend fun deleteMatchNotification(matchNotificationRoom: MatchNotificationRoom) =
        defaultLocalRepository.deleteMatchNotification(matchNotificationRoom)

    suspend fun upsertMatchNotification(matchNotificationRoom: MatchNotificationRoom) =
        defaultLocalRepository.upsertMatchNotification(matchNotificationRoom)

    private fun sortDatesList(matchesDates: MutableList<String>) {
        matchesDates.sortWith { date1, date2 ->
            val parts1 = date1.split("-").map { it.toInt() }
            val parts2 = date2.split("-").map { it.toInt() }
            when {
                parts1[0] != parts2[0] -> parts1[0] - parts2[0]
                parts1[1] != parts2[1] -> parts1[1] - parts2[1]
                else -> parts1[2] - parts2[2] // compare day
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}