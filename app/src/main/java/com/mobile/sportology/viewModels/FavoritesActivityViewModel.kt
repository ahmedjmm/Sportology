package com.mobile.sportology.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.models.football.TeamRoom
import com.mobile.sportology.repositories.DefaultLocalRepository
import com.mobile.sportology.repositories.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FavoritesActivityViewModel @Inject constructor(
    private val defaultLocalRepository: DefaultLocalRepository,
    private val remoteRepository: RemoteRepository
): ViewModel() {
    suspend fun getTeams(): List<TeamRoom> = defaultLocalRepository.getTeams()

    suspend fun getLeagues(): List<LeagueRoom> = defaultLocalRepository.getLeagues()

    suspend fun getCoaches(teamId: Int): Any? = viewModelScope.async {
        val currentDate = getCurrentDate()
        try {
            val response = remoteRepository.getCoaches(teamId)
            response?.body()?.response?.forEach {
                it?.career?.forEach { career ->
                    career?.end?.let { endDate ->
                        if (compareDate(endDate, currentDate)) {
                            return@async it.name
                        }
                    }
                }
            }
        }
        catch (jsonSyntaxException: JsonSyntaxException) {
            Log.e("getCoaches()", jsonSyntaxException.toString())
        }
        catch (exception: Exception) {
            Log.e("getCoaches()", exception.toString())
        }
        catch (timeOutException: SocketTimeoutException) {
            Log.e("getCoaches()", timeOutException.toString())
        }
    }.await()

    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return dateFormatter.format(currentDate)
    }

    private fun compareDate(endDate: String, currentDate: String): Boolean {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val myEndDate = LocalDate.parse(endDate, dateFormatter)
        val myCurrentDate = LocalDate.parse(currentDate, dateFormatter)
        return if (myEndDate > myCurrentDate) true
        else myEndDate >= myCurrentDate
    }
}