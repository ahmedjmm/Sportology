package com.mobile.sportology.repositories

import com.mobile.sportology.api.FootballApi
import com.mobile.sportology.api.NewsApi
import org.intellij.lang.annotations.Language
import javax.inject.Inject

class RemoteRepository @Inject constructor(
    private val footballApi: FootballApi?,
    private val newsApi: NewsApi?
) {

    suspend fun getLeague(id: Int) = footballApi?.getLeague(leagueId = id)

    suspend fun getLeagueMatches(leagueId: Int, season: Int) =
        footballApi?.getLeagueMatches(leagueId = leagueId, season = season)

    suspend fun getLeagueLiveMatches(leagueId: Int, season: Int, liveMatches: String?) =
        footballApi?.getLeagueLiveMatches(leagueId = leagueId, season = season, liveMatches = liveMatches)

    suspend fun getStandings(leagueId: Int, season:Int) =
        footballApi?.getStandings(leagueId = leagueId, season = season)

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