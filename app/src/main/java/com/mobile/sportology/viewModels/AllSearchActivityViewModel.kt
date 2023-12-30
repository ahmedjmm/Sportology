package com.mobile.sportology.viewModels

import androidx.lifecycle.ViewModel
import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.models.football.TeamRoom
import com.mobile.sportology.repositories.LocalRepository
import javax.inject.Inject

class AllSearchActivityViewModel @Inject constructor(
    private val localRepository: LocalRepository?
): ViewModel() {
    suspend fun deleteLeague(league: LeagueRoom) = localRepository?.deleteLeague(league)

    suspend fun upsertLeague(league: LeagueRoom) = localRepository?.upsertLeague(league)

    suspend fun getLeagues() = localRepository?.getLeagues()

    suspend fun getLeagueById(id: Int) = localRepository?.getLeagueById(id)

    suspend fun deleteTeam(team: TeamRoom) =  localRepository?.deleteTeam(team)

    suspend fun upsertTeam(team: TeamRoom) = localRepository?.upsertTeam(team)

    suspend fun getTeams() = localRepository?.getTeams()

    suspend fun getTeamById(id: Int) = localRepository?.getTeamById(id)
}