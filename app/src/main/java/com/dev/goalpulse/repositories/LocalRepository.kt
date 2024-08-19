package com.dev.goalpulse.repositories

import com.dev.goalpulse.models.football.LeagueRoom
import com.dev.goalpulse.models.football.MatchNotificationRoom
import com.dev.goalpulse.models.football.Team

interface LocalRepository {
    suspend fun getLeagues(): List<LeagueRoom>

    suspend fun upsertLeague(league: LeagueRoom)

    suspend fun deleteLeague(league: LeagueRoom)

    suspend fun getLeagueById(id: Int): LeagueRoom?

    suspend fun getTeams(): List<Team>

    suspend fun upsertTeam(team: Team)

    suspend fun deleteTeam(team: Team)

    suspend fun getTeamById(id: Int): Team?

    suspend fun getMatchNotificationById(matchId: Int): MatchNotificationRoom

    suspend fun getAllMatchNotifications(): List<MatchNotificationRoom>?

    suspend fun deleteMatchNotification(matchNotificationRoom: MatchNotificationRoom)

    suspend fun upsertMatchNotification(matchNotificationRoom: MatchNotificationRoom)

    suspend fun deleteMatchStartNotificationById(id: Int)
}