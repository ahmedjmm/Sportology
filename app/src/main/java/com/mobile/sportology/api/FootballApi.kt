package com.mobile.sportology.api

import com.mobile.sportology.Shared
import com.mobile.sportology.models.football.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApi {
    @GET("/fixtures")
    suspend fun getLeagueMatches(
        @Query("league")
        leagueId: Int = Shared.LEAGUES_IDS[0],
        @Query("timezone")
        timezone: String = Shared.TIME_ZONE[0],
        @Query("season")
        season: Int = 2022,
    ): Response<Fixtures>

    @GET("/fixtures")
    suspend fun getLeagueLiveMatches(
        @Query("league")
        leagueId: Int = Shared.LEAGUES_IDS[0],
        @Query("timezone")
        timezone: String = Shared.TIME_ZONE[0],
        @Query("season")
        season: Int = 2022,
        @Query("live")
        liveMatches: String?,
    ): Response<Fixtures>

    @GET("/leagues")
    suspend fun getLeague(
        @Query("id")
        leagueId: Int = Shared.LEAGUES_IDS[0],
        @Query("current")
        current: String = "true"
    ): Response<League>

    @GET("/fixtures")
    suspend fun getFixtureById(
        @Query("timezone")
        timeZone: String = Shared.TIME_ZONE[0],
        @Query("id")
        fixtureId: Int
    ): Response<FixtureById>

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