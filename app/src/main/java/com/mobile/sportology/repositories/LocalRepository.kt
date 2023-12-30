package com.mobile.sportology.repositories

import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.models.football.MatchNotificationRoom
import com.mobile.sportology.models.football.TeamRoom

interface LocalRepository {
    suspend fun getLeagues(): List<LeagueRoom>

    suspend fun upsertLeague(league: LeagueRoom)

    suspend fun deleteLeague(league: LeagueRoom)

    suspend fun getLeagueById(id: Int): LeagueRoom?

    suspend fun getTeams(): List<TeamRoom>

    suspend fun upsertTeam(team: TeamRoom)

    suspend fun deleteTeam(team: TeamRoom)

    suspend fun getTeamById(id: Int): TeamRoom?

    suspend fun getMatchNotificationById(matchId: Int): MatchNotificationRoom

    suspend fun getAllMatchNotifications(): List<MatchNotificationRoom>?

    suspend fun deleteMatchNotification(matchNotificationRoom: MatchNotificationRoom)

    suspend fun upsertMatchNotification(matchNotificationRoom: MatchNotificationRoom)

    suspend fun deleteMatchStartNotificationById(id: Int)
}