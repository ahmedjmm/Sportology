package com.dev.goalpulse.repositories.local

import com.dev.goalpulse.models.football.LeagueRoom
import com.dev.goalpulse.models.football.MatchNotificationRoom
import com.dev.goalpulse.models.football.Team
import com.dev.goalpulse.repositories.LocalRepository

class FakeLocalRepository: LocalRepository {
    private val leagues = mutableListOf<LeagueRoom>()
    private val teams = mutableListOf<Team>()
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
            return@forEach
        }
        return leagueRoom
    }

    override suspend fun getTeams(): List<Team> {
        return teams
    }

    override suspend fun upsertTeam(team: Team) {
        teams.add(team)
    }

    override suspend fun deleteTeam(team: Team) {
        teams.remove(team)
    }

    override suspend fun getTeamById(id: Int): Team? {
        var team: Team? = null
        teams.forEach {
            if(it.id == id) team = it
        }
        return team
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

    override suspend fun deleteMatchStartNotificationById(id: Int) {
        TODO("Not yet implemented")
    }
}