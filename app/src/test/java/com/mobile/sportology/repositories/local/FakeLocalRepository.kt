package com.mobile.sportology.repositories.local

import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.models.football.MatchNotificationRoom
import com.mobile.sportology.models.football.TeamRoom
import com.mobile.sportology.repositories.LocalRepository

class FakeLocalRepository: LocalRepository {
    private val leagues = mutableListOf<LeagueRoom>()
    private val teams = mutableListOf<TeamRoom>()
    override suspend fun getLeagues(): List<LeagueRoom> {
        return leagues
    }

    override suspend fun upsertLeague(league: LeagueRoom) {
        leagues.add(league)
    }

    override suspend fun deleteLeague(league: LeagueRoom) {
        leagues.remove(league)
    }

    override suspend fun getLeagueById(id: Int): LeagueRoom? {
        var leagueRoom: LeagueRoom? = null
        leagues.forEach {
            if(it.id == id) leagueRoom = it
        }
        return leagueRoom
    }

    override suspend fun getTeams(): List<TeamRoom> {
        return teams
    }

    override suspend fun upsertTeam(team: TeamRoom) {
        teams.add(team)
    }

    override suspend fun deleteTeam(team: TeamRoom) {
        teams.remove(team)
    }

    override suspend fun getTeamById(id: Int): TeamRoom? {
        var teamRoom: TeamRoom? = null
        teams.forEach {
            if(it.id == id) teamRoom = it
        }
        return teamRoom
    }

    override suspend fun getMatchNotificationById(matchId: Int): MatchNotificationRoom {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMatchNotifications(): List<MatchNotificationRoom>? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMatchNotification(matchNotificationRoom: MatchNotificationRoom) {
        TODO("Not yet implemented")
    }

    override suspend fun upsertMatchNotification(matchNotificationRoom: MatchNotificationRoom) {
        TODO("Not yet implemented")
    }
}