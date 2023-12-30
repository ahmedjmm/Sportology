package com.mobile.sportology.repositories

import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.models.football.MatchNotificationRoom
import com.mobile.sportology.models.football.TeamRoom
import com.mobile.sportology.rooms.LeagueDao
import com.mobile.sportology.rooms.MatchNotificationDao
import com.mobile.sportology.rooms.TeamDao
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

    override suspend fun upsertTeam(team: TeamRoom) = teamDao.upsertTeam(team)

    override suspend fun deleteTeam(team: TeamRoom) = teamDao.deleteTeam(team)

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