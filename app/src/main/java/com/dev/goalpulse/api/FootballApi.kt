package com.dev.goalpulse.api

import com.dev.goalpulse.models.football.*
import com.dev.goalpulse.models.football.Coachs
import com.dev.goalpulse.models.football.LeagueSearchResult
import com.dev.goalpulse.models.football.Standings
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApi {
    @GET("/matches")
    suspend fun getLeagueMatches(
        @Query("season_id")
        seasonId: String,
        @Query("status_type")
        matchStatus: String,
        @Query("offset")
        offset: String = "0"
    ): Response<Matches>

    @GET("/leagues")
    suspend fun getLeague(
        @Query("id")
        leagueId: String
    ): Response<Leagues>

    @GET("/seasons-by-league")
    suspend fun getSeasonsByLeague(
        @Query("league_id")
        leagueId: String
    ): Response<Seasons>

    @GET("/fixtures")
    suspend fun getMatchStatisticsById(
        @Query("match_id")
        matchId: String
    ): Response<MatchStatisticsById>

    @GET("/standings")
    suspend fun getStandings(
        @Query("league")
        leagueId: Int,
        @Query("season")
        season: Int
    ): Response<Standings>

    @GET("/teams")
    suspend fun getTeamSearchResult(
        @Query("search")
        query: String
    ): Response<TeamSearchResult>

    @GET("/leagues")
    suspend fun getLeagueSearchResult(
        @Query("search")
        query: String
    ): Response<LeagueSearchResult>

    @GET("/coachs")
    suspend fun getCoaches(
        @Query("team")
        teamId: Int
    ): Response<Coachs>
}