package com.dev.goalpulse.repositories

import com.dev.goalpulse.ApiResponseHandler
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.api.FootballApi
import com.dev.goalpulse.api.NewsApi
import com.dev.goalpulse.models.football.Matches
import javax.inject.Inject

class RemoteRepository @Inject constructor(
    private val footballApi: FootballApi?,
    private val newsApi: NewsApi?
) {

    suspend fun getLeague(id: String) = footballApi?.getLeague(leagueId = id)

    suspend fun getLeagueMatches(
        seasonId: String,
        matchStatus: String,
        offset: String = "0"
    ): ResponseState<Matches> =
        ApiResponseHandler.handleResponse {
            footballApi?.getLeagueMatches(
                seasonId = seasonId,
                matchStatus = matchStatus,
                offset = offset
            )!!
        }

    suspend fun getSeasonsByLeague(leagueId: String) = footballApi?.getSeasonsByLeague(leagueId)

    suspend fun getStandings(leagueId: Int, season:Int) =
        footballApi?.getStandings(leagueId = leagueId, season = season)

    suspend fun getFixtureById(timeZone: String, fixtureId: Int) =
        footballApi?.getFixtureById(timeZone = timeZone, fixtureId = fixtureId)

    suspend fun getTeamSearchResults(query: String) = footballApi?.getTeamSearchResult(query)

    suspend fun getLeagueSearchResults(query: String) = footballApi?.getLeagueSearchResult(query)

    suspend fun getCoaches(teamId: Int) = footballApi?.getCoaches(teamId = teamId)

    suspend fun getEveryThingNews(
        q: String,
        language: String,
        sortBy: String,
        sources: String,
        searchIn: String,
    ) = newsApi?.getEverythingNews(
        q = q,
        language = language,
        sortBy = sortBy,
        sources = sources,
        searchIn = searchIn,
    )

    suspend fun getTopHeadLinesNews(
        language: String
    ) = newsApi?.getTopHeadLinesNews(language = language)
}