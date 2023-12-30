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
import com.mobile.sportology.models.football.Fixtures
import com.mobile.sportology.models.football.Leagues
import com.mobile.sportology.models.football.MatchNotificationRoom
import com.mobile.sportology.repositories.DefaultLocalRepository
import com.mobile.sportology.repositories.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class FootBallViewModel @Inject constructor(
    private val app: Application,
    private val remoteRepository: RemoteRepository,
    private val defaultLocalRepository: DefaultLocalRepository
): ViewModel() {
    private val _leaguesMutableLiveData = MutableLiveData<ResponseState<Leagues>>()
    val leaguesLiveData: LiveData<ResponseState<Leagues>> = _leaguesMutableLiveData
    private val _englandPremierLeagueMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val englandPremierLeagueDatesLiveData = _englandPremierLeagueMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        when (responseState) {
            is ResponseState.Success -> {
                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                return@map ResponseState.Success(matchesDates)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _laLigaMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val laLigaDatesLiveData = _laLigaMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        when (responseState) {
            is ResponseState.Success -> {
                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                return@map ResponseState.Success(matchesDates)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _ligue1MatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val ligue1DatesLiveData = _ligue1MatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        when (responseState) {
            is ResponseState.Success -> {
                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                return@map ResponseState.Success(matchesDates)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _serieAMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val serieADatesLiveData = _serieAMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        when (responseState) {
            is ResponseState.Success -> {
                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                return@map ResponseState.Success(matchesDates)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _bundesLigaMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val bundesLigaDatesLiveData = _bundesLigaMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        when (responseState) {
            is ResponseState.Success -> {
                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                return@map ResponseState.Success(matchesDates)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _egyptianPremierLeagueMatchesMutableLiveData = MutableLiveData<ResponseState<Fixtures>>()
    val egyptianPremierLeagueDatesLiveData = _egyptianPremierLeagueMatchesMutableLiveData.map { responseState ->
        var matchesDates = mutableListOf<String>()
        when (responseState) {
            is ResponseState.Success -> {
                responseState.data?.response?.forEach{
                    matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                }
                matchesDates = matchesDates.distinct().toMutableList()
                sortDatesList(matchesDates)
                return@map ResponseState.Success(matchesDates)
            }
            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private inline fun createLiveData() = MutableLiveData<ResponseState<Fixtures>>().map { responseState ->
            var matchesDates = mutableListOf<String>()
            when (responseState) {
                is ResponseState.Success -> {
                    responseState.data?.response?.forEach{
                        matchesDates.add(it?.fixture?.date?.substring(0, 10)!!)
                    }
                    matchesDates = matchesDates.distinct().toMutableList()
                    sortDatesList(matchesDates)
                    return@map ResponseState.Success(matchesDates)
                }
                is ResponseState.Loading -> return@map ResponseState.Loading()
                is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
            }
        }

    private suspend fun postValueOfMatches(
        liveData: MutableLiveData<ResponseState<Fixtures>>,
        leagueId: Int,
        season: Int,
        liveMatches: String?
    ) = viewModelScope.launch(Dispatchers.IO) {
        liveData.postValue(ResponseState.Loading())
        try {
            val response = liveMatches?.let {
                remoteRepository.getLeagueLiveMatches(
                    leagueId = leagueId,
                    season = season,
                    liveMatches = it
                )
            } ?: remoteRepository.getLeagueMatches(leagueId, season)

            if (response?.body()?.response.isNullOrEmpty()) {
                liveData.postValue(ResponseState.Error(app.getString(R.string.no_results)))
            }
            else {
                liveData.postValue(ResponseState.Success(response?.body()!!))
            }
        }
        catch (exception: Exception) {
            handleMatchesException(liveData, exception)
        }
    }

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

    suspend fun getLeague(id: Int): ResponseState<Response<Leagues>> = viewModelScope.async {
        _leaguesMutableLiveData.postValue(ResponseState.Loading())
        try {
            val response = remoteRepository.getLeague(id)
            _leaguesMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
            Log.i("leaguesCount", response.body()?.response?.size.toString())
            return@async ResponseState.Success(response)
        }
        catch(exception: Exception) {
            return@async handleLeaguesException(exception)
        }
    }.await()

    private fun handleMatchesException(
        liveData: MutableLiveData<ResponseState<Fixtures>>,
        exception: Exception
    ) {
        val errorMessage = when (exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.toString()
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        Log.e("handleMatchesException()", exception.toString())
        liveData.postValue(ResponseState.Error(errorMessage))
    }

    private fun handleLeaguesException(exception: Exception): ResponseState<Response<Leagues>> {
        val errorMessage = when (exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.toString()
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        _leaguesMutableLiveData.postValue(ResponseState.Error(errorMessage))
        return ResponseState.Error(errorMessage)
    }

    suspend fun getLeagueMatches(
        leagueId: Int,
        season: Int,
        leagueOrder: Int,
        liveMatches: String?
    ) {
        when (leagueOrder) {
            0 -> postValueOfMatches(_englandPremierLeagueMatchesMutableLiveData, leagueId, season, liveMatches)
            1 -> postValueOfMatches(_laLigaMatchesMutableLiveData, leagueId, season, liveMatches)
            2 -> postValueOfMatches(_ligue1MatchesMutableLiveData, leagueId, season, liveMatches)
            3 -> postValueOfMatches(_serieAMatchesMutableLiveData, leagueId, season, liveMatches)
            4 -> postValueOfMatches(_bundesLigaMatchesMutableLiveData, leagueId, season, liveMatches)
            5 -> postValueOfMatches(_egyptianPremierLeagueMatchesMutableLiveData, leagueId, season, liveMatches)
        }
    }

    fun getMatchesOfLeague(date: String, position: Int): MutableList<Fixtures.Response> {
        val fixtures = mutableListOf<Fixtures.Response>()
        when(position) {
            0 -> {
                _englandPremierLeagueMatchesMutableLiveData.value?.data?.response?.forEach {
                    if (it?.fixture?.date!!.contains(date)) fixtures.add(it)
                }
                return fixtures
            }
            1 -> {
                _laLigaMatchesMutableLiveData.value?.data?.response?.forEach {
                    if (it?.fixture?.date!!.contains(date)) fixtures.add(it)
                }
                return fixtures
            }
            2 -> {
                _ligue1MatchesMutableLiveData.value?.data?.response?.forEach {
                    if (it?.fixture?.date!!.contains(date)) fixtures.add(it)
                }
                return fixtures
            }
            3 -> {
                _serieAMatchesMutableLiveData.value?.data?.response?.forEach {
                    if (it?.fixture?.date!!.contains(date)) fixtures.add(it)
                }
                return fixtures
            }
            4 -> {
                _bundesLigaMatchesMutableLiveData.value?.data?.response?.forEach {
                    if (it?.fixture?.date!!.contains(date)) fixtures.add(it)
                }
                return fixtures
            }
            5 -> {
                _egyptianPremierLeagueMatchesMutableLiveData.value?.data?.response?.forEach {
                    if (it?.fixture?.date!!.contains(date)) fixtures.add(it)
                }
                return fixtures
            }
            else -> return fixtures
        }
    }

    suspend fun getMatchNotificationById(matchId: Int) = defaultLocalRepository.getMatchNotificationById(matchId)

    suspend fun getAllMatchNotifications() = defaultLocalRepository.getAllMatchNotifications()

    suspend fun deleteMatchNotification(matchNotificationRoom: MatchNotificationRoom) =
        defaultLocalRepository.deleteMatchNotification(matchNotificationRoom)

    suspend fun upsertMatchNotification(matchNotificationRoom: MatchNotificationRoom) =
        defaultLocalRepository.upsertMatchNotification(matchNotificationRoom)
}