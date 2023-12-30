package com.mobile.sportology.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.models.football.LeagueSearchResult
import com.mobile.sportology.models.football.TeamSearchResult
import com.mobile.sportology.repositories.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
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

    suspend fun searchTeam(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _teamSearchMutableLiveData.postValue(ResponseState.Loading())
        try{
            val response = remoteRepository.getTeamSearchResults(query)
            if(response?.body()?.response?.isEmpty() == true)
                _teamSearchMutableLiveData.postValue(ResponseState.Error(app.getString(
                    R.string.no_results)))
            else _teamSearchMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
        }
        catch (timeOutException: SocketTimeoutException) {
            Log.e("searchTeam()", timeOutException.toString())
            _teamSearchMutableLiveData.postValue(timeOutException.message?.let {
                ResponseState.Error(
                    it
                )
            })
        }
        catch (jsonSyntaxException: JsonSyntaxException) {
            Log.e("searchTeam()", jsonSyntaxException.toString())
            _teamSearchMutableLiveData.postValue(
                ResponseState.Error(app.resources.getString(R.string.limit_reached)))
        }
        catch (exception: Exception) {
            Log.e("searchTeam()", exception.toString())
            _teamSearchMutableLiveData.postValue(ResponseState.Error(app.resources.getString(R.string.unknown_error)))
        }
    }

    suspend fun searchLeague(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _leagueSearchMutableLiveData.postValue(ResponseState.Loading())
        try{
            val response = remoteRepository.getLeagueSearchResults(query)
            if(response?.body()?.response?.isEmpty() == true)
                _leagueSearchMutableLiveData.postValue(ResponseState.Error(app.getString(
                    R.string.no_results)))
            else _leagueSearchMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
        }
        catch (timeOutException: SocketTimeoutException) {
            Log.e("searchLeague()", timeOutException.toString())
            _leagueSearchMutableLiveData.postValue(timeOutException.message?.let {
                ResponseState.Error(
                    it
                )
            })
        }
        catch (jsonSyntaxException: JsonSyntaxException) {
            Log.e("searchLeague()", jsonSyntaxException.toString())
            _leagueSearchMutableLiveData.postValue(
                ResponseState.Error(app.resources.getString(R.string.limit_reached)))
        }
        catch (exception: Exception) {
            Log.e("searchLeague()", exception.toString())
            _leagueSearchMutableLiveData.postValue(ResponseState.Error(app.resources.getString(R.string.unknown_error)))
        }
    }
}