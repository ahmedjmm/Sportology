package com.dev.goalpulse.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.goalpulse.R
import com.google.gson.JsonSyntaxException
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.Shared
import com.dev.goalpulse.models.football.LeagueSearchResult
import com.dev.goalpulse.models.football.TeamSearchResult
import com.dev.goalpulse.repositories.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SearchActivityViewModel @Inject constructor(
    private val app: Application,
    private val remoteRepository: RemoteRepository
): ViewModel() {
    private val _teamSearchMutableLiveData = MutableLiveData<ResponseState<TeamSearchResult>>()
    private val _leagueSearchMutableLiveData = MutableLiveData<ResponseState<LeagueSearchResult>>()
    val teamSearchLiveData: LiveData<ResponseState<TeamSearchResult>> = _teamSearchMutableLiveData
    val leagueSearchLiveData: LiveData<ResponseState<LeagueSearchResult>> = _leagueSearchMutableLiveData

    fun searchTeam(query: String) = viewModelScope.launch(Dispatchers.IO) {
        if(Shared.isConnected) {
            _teamSearchMutableLiveData.postValue(ResponseState.Loading())
            try{
                val response = remoteRepository.getTeamSearchResults(query)
                if(response?.body()?.response?.isEmpty() == true)
                    _teamSearchMutableLiveData.postValue(ResponseState.Error(app.getString(
                        R.string.no_results)))
                else _teamSearchMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
            }
            catch (exception: Exception) {
                handleTeamSearchException(exception)
            }
        }
        else _teamSearchMutableLiveData.postValue(ResponseState.Error(app.getString(R.string.unable_to_connect)))
    }

    fun searchLeague(query: String) = viewModelScope.launch(Dispatchers.IO) {
        if(Shared.isConnected) {
            _leagueSearchMutableLiveData.postValue(ResponseState.Loading())
            try{
                val response = remoteRepository.getLeagueSearchResults(query)
                if(response?.body()?.response?.isEmpty() == true)
                    _leagueSearchMutableLiveData.postValue(ResponseState.Error(app.getString(
                        R.string.no_results)))
                else _leagueSearchMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
            }
            catch (exception: Exception) {
                handleLeagueSearchException(exception)
            }
        }
        else _leagueSearchMutableLiveData.postValue(ResponseState.Error(app.resources.getString(R.string.unable_to_connect)))
    }

    private fun handleLeagueSearchException(
        exception: Exception
    ) {
        val errorMessage = when (exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        Log.e("handleLeagueSearchException()", exception.toString())
        _leagueSearchMutableLiveData.postValue(ResponseState.Error(errorMessage))
    }

    private fun handleTeamSearchException(
        exception: Exception
    ) {
        val errorMessage = when (exception) {
            is JsonSyntaxException -> app.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> app.getString(R.string.unknown_error)
            else -> app.getString(R.string.unknown_error)
        }
        Log.e("handleTeamSearchException()", exception.toString())
        _teamSearchMutableLiveData.postValue(ResponseState.Error(errorMessage))
    }
}