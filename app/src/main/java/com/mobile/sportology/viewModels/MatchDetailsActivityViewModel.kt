package com.mobile.sportology.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.mobile.sportology.BuildConfig
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.models.football.FixtureById
import com.mobile.sportology.models.football.Standings
import com.mobile.sportology.repositories.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class MatchDetailsActivityViewModel @Inject constructor(
    private val app: Application,
    private val remoteRepository: RemoteRepository,
): ViewModel() {
    private val _fixtureByIdMutableLiveData = MutableLiveData<ResponseState<FixtureById>>()
    private val _standingsMutableLiveData = MutableLiveData<ResponseState<Standings>>()
    val standingsLiveData: LiveData<ResponseState<Standings>> = _standingsMutableLiveData
    val fixtureByIdLiveData: LiveData<ResponseState<FixtureById>> = _fixtureByIdMutableLiveData
    var teamLineups = 0

    // we used http client in this request to change each null value to 0
    fun getFixtureById(timeZone: String, fixtureId: Int) {
        _fixtureByIdMutableLiveData.postValue(ResponseState.Loading())
        viewModelScope.launch {
            try {
                val builder = HttpUrl.Builder()
                builder.scheme("https")
                builder.host("v3.football.api-sports.io")
                builder.addPathSegment("fixtures")
                builder.addQueryParameter("timezone", timeZone)
                builder.addQueryParameter("id", fixtureId.toString())
                val url = builder.build().toString()
                val request = Request.Builder()
                    .url(url)
                    .addHeader("x-rapidapi-key", BuildConfig.FOOTBALL_API_KEY)
                    .build()

                val client = OkHttpClient()
                client.newCall(request).enqueue(object: okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        Log.i("failedResponse", e.toString())
                        _fixtureByIdMutableLiveData.postValue(ResponseState.Error(app.getString(R.string.unable_to_connect)))
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        val json = response.body?.string()
                        val fixture = Gson().fromJson(json, FixtureById::class.java)
                        Log.i("fixtureID", fixtureId.toString())
                        if(fixture.response?.get(0)?.statistics?.isNotEmpty() == true){
                            fixture.response[0]?.statistics?.get(0)?.statisticsData?.let{ statisticsData ->
                                statisticsData.forEach {
                                    if(it?.value == null) it?.value = "0"
                                }
                            }
                            fixture.response[0]?.statistics?.get(1)?.statisticsData?.let{ statisticsData ->
                                statisticsData.forEach {
                                    if(it?.value == null) it?.value = "0"
                                }
                            }
                        }
                        _fixtureByIdMutableLiveData.postValue(ResponseState.Success(fixture))
                    }
                })
            }
            catch (jsonSyntaxException: JsonSyntaxException) {
                Log.i("getFixtureById()", jsonSyntaxException.message!!)
                _fixtureByIdMutableLiveData.postValue(ResponseState.Error(
                    app.resources.getString(R.string.limit_reached)))
            }
            catch (exception: Exception) {
                Log.i("getFixtureById()", exception.message!!)
                _fixtureByIdMutableLiveData.postValue(ResponseState.Error(
                    app.resources.getString(R.string.unknown_error)))
            }
            catch (timeOutException: SocketTimeoutException) {
                Log.i("getFixtureById()", timeOutException.message!!)
                _fixtureByIdMutableLiveData.postValue(timeOutException.message?.let {
                    ResponseState.Error(
                        it
                    )
                })
            }
        }
    }

    suspend fun getStandings(leagueId: Int, season: Int) = viewModelScope.launch {
        try {
            val response = remoteRepository.getStandings(season = season, leagueId = leagueId)
            _standingsMutableLiveData.postValue(ResponseState.Success(response?.body()!!))
        }
        catch (jsonSyntaxException: JsonSyntaxException) {
            _fixtureByIdMutableLiveData.postValue(ResponseState.Error(
                app.resources.getString(R.string.limit_reached)))
        }
        catch (exception: Exception) {
            _fixtureByIdMutableLiveData.postValue(ResponseState.Error(
                app.resources.getString(R.string.unknown_error)))
        }
    }
}

//fun updateTeamLineups(teamLineups: FixtureById.Response.Lineup) {
//    _teamLineupsMutableLiveData.value = teamLineups
//}
