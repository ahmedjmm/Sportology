package com.dev.goalpulse.viewModels

import androidx.lifecycle.ViewModel
import com.dev.goalpulse.models.football.LeagueRoom
import com.dev.goalpulse.models.football.Team
import com.dev.goalpulse.repositories.LocalRepository
import javax.inject.Inject

class AllSearchActivityViewModel @Inject constructor(
    private val localRepository: LocalRepository?
): ViewModel() {
    suspend fun deleteLeague(league: LeagueRoom) = localRepository?.deleteLeague(league)

    suspend fun upsertLeague(league: LeagueRoom) = localRepository?.upsertLeague(league)

    suspend fun getLeagues() = localRepository?.getLeagues()

    suspend fun getLeagueById(id: Int) = localRepository?.getLeagueById(id)

    suspend fun deleteTeam(team: Team) =  localRepository?.deleteTeam(team)

    suspend fun upsertTeam(team: Team) = localRepository?.upsertTeam(team)

    suspend fun getTeams() = localRepository?.getTeams()

    suspend fun getTeamById(id: Int) = localRepository?.getTeamById(id)
}