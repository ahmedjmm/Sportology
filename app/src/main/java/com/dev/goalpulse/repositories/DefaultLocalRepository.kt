package com.dev.goalpulse.repositories

import com.dev.goalpulse.models.football.LeagueRoom
import com.dev.goalpulse.models.football.MatchNotificationRoom
import com.dev.goalpulse.models.football.Team
import com.dev.goalpulse.rooms.LeagueDao
import com.dev.goalpulse.rooms.MatchNotificationDao
import com.dev.goalpulse.rooms.TeamDao
import javax.inject.Inject

class DefaultLocalRepository @Inject constructor(
    private val leagueDao: LeagueDao,
    private val teamDao: TeamDao,
    private val matchNotificationDao: MatchNotificationDao
    ): LocalRepository {
    override suspend fun getLeagues() = leagueDao.getLeaguesOrderedByName()

    override suspend fun upsertLeague(league: LeagueRoom) = leagueDao.upsertLeague(league)

    override suspend fun deleteLeague(league: LeagueRoom) = leagueDao.deleteLeague(league)

    override suspend fun getLeagueById(id: Int) = leagueDao.getLeague(id)

    override suspend fun getTeams() = teamDao.getTeamsOrderedByName()

    override suspend fun upsertTeam(team: Team) = teamDao.upsertTeam(team)

    override suspend fun deleteTeam(team: Team) = teamDao.deleteTeam(team)

    override suspend fun getTeamById(id: Int) = teamDao.getTeam(id)

    override suspend fun upsertMatchNotification(matchNotificationRoom: MatchNotificationRoom) =
        matchNotificationDao.upsertMatchNotification(matchNotificationRoom)

    override suspend fun deleteMatchStartNotificationById(id: Int) =
        matchNotificationDao.deleteMatchNotificationById(id)

    override suspend fun getMatchNotificationById(matchId: Int) =
        matchNotificationDao.getMatchNotificationById(matchId)
    override suspend fun getAllMatchNotifications() = matchNotificationDao.getAllMatchNotifications()

    override suspend fun deleteMatchNotification(matchNotificationRoom: MatchNotificationRoom) =
        matchNotificationDao.deleteMatchNotification(matchNotificationRoom)
}