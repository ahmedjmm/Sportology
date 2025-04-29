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
import com.dev.goalpulse.models.football.Leagues
import com.dev.goalpulse.models.football.MatchNotificationRoom
import com.dev.goalpulse.models.football.Matches
import com.dev.goalpulse.models.football.Seasons
import com.dev.goalpulse.repositories.DefaultLocalRepository
import com.dev.goalpulse.repositories.RemoteRepository
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

    private var _englandPremierLeagueMatchesDates = mutableListOf<String>()
    private val _englandPremierLeagueMatches = mutableListOf<Any>()

    private var _laLigaMatchesDates = mutableListOf<String>()
    private val _laLigaMatches = mutableListOf<Any>()

    private var _ligue1MatchesDates = mutableListOf<String>()
    private val _ligue1Matches = mutableListOf<Any>()

    private var _serieAMatchesDates = mutableListOf<String>()
    private val _serieAMatches = mutableListOf<Any>()

    private var _bundesLigaMatchesDates = mutableListOf<String>()
    private val _bundesLigaMatches = mutableListOf<Any>()

    private var _egyptianPremierLeagueMatchesDates = mutableListOf<String>()
    private val _egyptianPremierLeagueMatches = mutableListOf<Any>()

    private val _leagues: MutableList<Leagues> = mutableListOf()
    private val _leaguesMutableLiveData = MutableLiveData<MutableList<Leagues>>()
    val leaguesLiveData: LiveData<MutableList<Leagues>> = _leaguesMutableLiveData

    private val _englandPremierLeagueMatchesMutableLiveData = MutableLiveData<ResponseState<Matches>>()
    val englandPremierLeagueMatchesLiveData =
        _englandPremierLeagueMatchesMutableLiveData.map { responseState ->
            when (responseState) {
                is ResponseState.Success -> {
                    val formatedMatchesData = reformatMatchesData(
                        responseState,
                        _englandPremierLeagueMatchesDates,
                        _englandPremierLeagueMatches
                    )

                    if (responseState.data?.size!! < 50) {
                        return@map ResponseState.Success(
                            data = formatedMatchesData,
                            message = app.getString(R.string.all_data_has_been_fetched))
                    }
                    return@map ResponseState.Success(data = formatedMatchesData)
                }

                is ResponseState.Loading -> return@map ResponseState.Loading()
                is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
            }
        }


    private val _laLigaMatchesMutableLiveData = MutableLiveData<ResponseState<Matches>>()
    val laLigaMatchesLiveData = _laLigaMatchesMutableLiveData.map { responseState ->
        when (responseState) {
            is ResponseState.Success -> {
                val formatedMatchesData = reformatMatchesData(
                    responseState,
                    _laLigaMatchesDates,
                    _laLigaMatches
                )

                if (responseState.data?.size!! < 50) {
                    return@map ResponseState.Success(
                        data = formatedMatchesData,
                        message = app.getString(R.string.all_data_has_been_fetched))
                }
                return@map ResponseState.Success(data = formatedMatchesData)
            }

            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _ligue1MatchesMutableLiveData = MutableLiveData<ResponseState<Matches>>()
    val ligue1MatchesLiveData = _ligue1MatchesMutableLiveData.map { responseState ->
        when (responseState) {
            is ResponseState.Success -> {
                val formatedMatchesData = reformatMatchesData(
                    responseState,
                    _ligue1MatchesDates,
                    _ligue1Matches
                )

                if (responseState.data?.size!! < 50) {
                    return@map ResponseState.Success(
                        data = formatedMatchesData,
                        message = app.getString(R.string.all_data_has_been_fetched))
                }
                return@map ResponseState.Success(data = formatedMatchesData)
            }

            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _serieAMatchesMutableLiveData = MutableLiveData<ResponseState<Matches>>()
    val serieAMatchesLiveData = _serieAMatchesMutableLiveData.map { responseState ->
        when (responseState) {
            is ResponseState.Success -> {
                val formatedMatchesData = reformatMatchesData(
                    responseState,
                    _serieAMatchesDates,
                    _serieAMatches
                )

                if (responseState.data?.size!! < 50) {
                    return@map ResponseState.Success(
                        data = formatedMatchesData,
                        message = app.getString(R.string.all_data_has_been_fetched))
                }
                return@map ResponseState.Success(data = formatedMatchesData)
            }

            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _bundesLigaMatchesMutableLiveData = MutableLiveData<ResponseState<Matches>>()
    val bundesLigaMatchesLiveData = _bundesLigaMatchesMutableLiveData.map { responseState ->
        when (responseState) {
            is ResponseState.Success -> {
                val formatedMatchesData = reformatMatchesData(
                    responseState,
                    _bundesLigaMatchesDates,
                    _bundesLigaMatches
                )

                if (responseState.data?.size!! < 50) {
                    return@map ResponseState.Success(
                        data = formatedMatchesData,
                        message = app.getString(R.string.all_data_has_been_fetched))
                }
                return@map ResponseState.Success(data = formatedMatchesData)
            }

            is ResponseState.Loading -> return@map ResponseState.Loading()
            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
        }
    }

    private val _egyptianPremierLeagueMatchesMutableLiveData = MutableLiveData<ResponseState<Matches>>()
    val egyptianPremierLeagueMatchesLiveData =
        _egyptianPremierLeagueMatchesMutableLiveData.map { responseState ->
            when (responseState) {
                is ResponseState.Success -> {
                    val formatedMatchesData = reformatMatchesData(
                        responseState,
                        _egyptianPremierLeagueMatchesDates,
                        _egyptianPremierLeagueMatches
                    )

                    if (responseState.data?.size!! < 50) {
                        return@map ResponseState.Success(
                            data = formatedMatchesData,
                            message = app.getString(R.string.all_data_has_been_fetched))
                    }
                    return@map ResponseState.Success(data = formatedMatchesData)
                }

                is ResponseState.Loading -> return@map ResponseState.Loading()
                is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
            }
        }

//    private val _seasonsMutableLiveData = MutableLiveData<ResponseState<Seasons>>()
//    val seasonsLiveData = _seasonsMutableLiveData.map { responseState ->
//        when(responseState) {
//            is ResponseState.Success -> {
//                if(responseState.data?.isEmpty() == true)
//                    return@map ResponseState.Error("No data")
//
//                val size = responseState.data?.get(0)?.seasons?.size!!
//                val seasons = mutableListOf<Seasons.SeasonsItem.Season>()
//                responseState.data[0].seasons?.get(0)?.let { seasons.add(it) }
//                responseState.data[0].seasons?.get(size-1)?.let { seasons.add(it) }
//                return@map ResponseState.Success(seasons)
//            }
//            is ResponseState.Loading -> return@map ResponseState.Loading()
//            is ResponseState.Error -> return@map ResponseState.Error(responseState.message!!)
//        }
//    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (Shared.isConnected) {
                Shared.LEAGUES_IDS.forEach { leagueId ->
                    when (val responseState = getLeague(leagueId)) {
                        is ResponseState.Error -> {
                            val error = responseState.message
                            when (leagueId) {
                                "eq.16" -> _englandPremierLeagueMatchesMutableLiveData.postValue(
                                    ResponseState.Error(error!!))

                                "eq.55" -> _laLigaMatchesMutableLiveData.postValue(
                                    ResponseState.Error(error!!))

                                "eq.662" -> _ligue1MatchesMutableLiveData.postValue(
                                    ResponseState.Error(error!!))

                                "eq.57" -> _serieAMatchesMutableLiveData.postValue(
                                    ResponseState.Error(error!!))

                                "eq.21" -> _bundesLigaMatchesMutableLiveData.postValue(
                                    ResponseState.Error(error!!))

                                "eq.27" -> _egyptianPremierLeagueMatchesMutableLiveData.postValue(
                                    ResponseState.Error(error!!))
                            }
                        }
                        else -> {}
                    }
                }
                _leaguesMutableLiveData.postValue(_leagues)
            }
        }
    }

    suspend fun getSeasonByLeague(leagueId: String): ResponseState<out Response<Seasons>?> =
        viewModelScope.async(Dispatchers.IO) {
            if (Shared.isConnected) {
                try {
                    val response = remoteRepository.getSeasonsByLeague(leagueId)

                    if (response?.body()?.isNotEmpty() == true)
                        return@async ResponseState.Success(response)
                    else
                        return@async ResponseState.Error(app.getString(R.string.data_not_provided))
                } catch (exception: Exception) {
                    return@async handleSeasonsException(exception)
                }
            } else {
                return@async ResponseState.Error(app.getString(R.string.unable_to_connect))
            }
        }.await()

    suspend fun getLeague(id: String): ResponseState<out Response<Leagues>?> =
        viewModelScope.async(Dispatchers.IO) {
            if (Shared.isConnected) {
                try {
                    val response = remoteRepository.getLeague(id)
                    response?.body()?.let { _leagues.add(it) }
                    return@async ResponseState.Success(response)
                } catch (exception: Exception) {
                    return@async handleLeaguesException(exception)
                }
            } else {
                return@async ResponseState.Error(app.getString(R.string.unable_to_connect))
            }
        }.await()

    suspend fun getLeagueMatches(leagueId: String, seasonId: String = "eq.45769",
                                 matchStatus: String = "", offset: String = "0") =
        viewModelScope.launch(Dispatchers.IO) {
            when(leagueId) {
                Shared.LEAGUES_IDS[0] ->
                    _englandPremierLeagueMatchesMutableLiveData.postValue(ResponseState.Loading())
                Shared.LEAGUES_IDS[1] ->
                    _laLigaMatchesMutableLiveData.postValue(ResponseState.Loading())
                Shared.LEAGUES_IDS[2] ->
                    _ligue1MatchesMutableLiveData.postValue (ResponseState.Loading())
                Shared.LEAGUES_IDS[3] ->
                    _serieAMatchesMutableLiveData.postValue (ResponseState.Loading())
                Shared.LEAGUES_IDS[4] ->
                    _bundesLigaMatchesMutableLiveData.postValue (ResponseState.Loading())
                Shared.LEAGUES_IDS[5] ->
                    _egyptianPremierLeagueMatchesMutableLiveData.postValue (ResponseState.Loading())
            }
            val response = remoteRepository.getLeagueMatches(seasonId = seasonId,
                matchStatus = matchStatus, offset = offset)
            try {
                when(leagueId) {
                    Shared.LEAGUES_IDS[0] -> {
                        _englandPremierLeagueMatchesMutableLiveData.postValue(ResponseState.Success(response.data!!))
                    }
                    Shared.LEAGUES_IDS[1] -> {
                        _laLigaMatchesMutableLiveData.postValue(ResponseState.Success(response.data!!))
                    }
                    Shared.LEAGUES_IDS[2] -> {
                        _ligue1MatchesMutableLiveData.postValue(ResponseState.Success(response.data!!))
                    }
                    Shared.LEAGUES_IDS[3] -> {
                        _serieAMatchesMutableLiveData.postValue(ResponseState.Success(response.data!!))
                    }
                    Shared.LEAGUES_IDS[4] -> {
                        _bundesLigaMatchesMutableLiveData.postValue(ResponseState.Success(response.data!!))
                    }
                    Shared.LEAGUES_IDS[5] -> {
                        _egyptianPremierLeagueMatchesMutableLiveData.postValue(ResponseState.Success(response.data!!))
                    }
                }
            } catch (exception: Exception) {
                Log.e("getLeagueMatches()", exception.toString())
                response.message?.let {
                    handleMatchesException(_englandPremierLeagueMatchesMutableLiveData, it)
                    handleMatchesException(_laLigaMatchesMutableLiveData, it)
                    handleMatchesException(_ligue1MatchesMutableLiveData, it)
                    handleMatchesException(_serieAMatchesMutableLiveData, it)
                    handleMatchesException(_bundesLigaMatchesMutableLiveData, it)
                    handleMatchesException(_egyptianPremierLeagueMatchesMutableLiveData, it)
                }
            }
        }.join()


    private fun handleMatchesException(
        liveData: MutableLiveData<ResponseState<Matches>>,
        exception: String
    ) {
        Log.e("handleMatchesException()", exception)
        liveData.postValue(ResponseState.Error(exception))
    }

    private fun handleLeaguesException(exception: Exception): ResponseState<Response<Leagues>> {
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

    private fun handleSeasonsException(exception: Exception): ResponseState<Response<Seasons>> {
        val errorMessage = when (exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        Log.e("handleSeasonsException()", exception.toString())
        return ResponseState.Error(errorMessage)
    }

    suspend fun getMatchNotificationById(matchId: Int) = defaultLocalRepository.getMatchNotificationById(matchId)

    suspend fun getAllMatchNotifications() = defaultLocalRepository.getAllMatchNotifications()

    suspend fun deleteMatchNotification(matchNotificationRoom: MatchNotificationRoom) =
        defaultLocalRepository.deleteMatchNotification(matchNotificationRoom)

    suspend fun upsertMatchNotification(matchNotificationRoom: MatchNotificationRoom) =
        defaultLocalRepository.upsertMatchNotification(matchNotificationRoom)

    private fun reformatMatchesData(
        responseState: ResponseState<Matches>,
        dates: MutableList<String>,
        matches: MutableList<Any>
    ): MutableList<Any> {
        responseState.data?.forEach {
            dates.add(it.startTime?.substring(0, 10)!!)
        }
        sortDatesList(dates)
        for (datesIndex in dates.indices) {
            matches.add(dates[datesIndex])
            for (fixtureIndex in responseState.data?.indices!!) {
                if (responseState.data[fixtureIndex].startTime!!.contains(dates[datesIndex]))
                    matches.add(responseState.data[fixtureIndex])
            }
        }
        val newMatches = matches.distinct().toMutableList()
        return newMatches
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}