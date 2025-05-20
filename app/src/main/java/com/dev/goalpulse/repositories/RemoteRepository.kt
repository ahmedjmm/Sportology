package com.dev.goalpulse.repositories

import com.dev.goalpulse.ApiResponseHandler
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.api.FootballApi
import com.dev.goalpulse.api.NewsApi
import com.dev.goalpulse.models.football.MatchGraphs
import com.dev.goalpulse.models.football.MatchPositions
import com.dev.goalpulse.models.football.MatchStatistics
import javax.inject.Inject

class RemoteRepository @Inject constructor(
    private val footballApi: FootballApi?,
    private val newsApi: NewsApi?,
    private val cache: DataCache
) {

    suspend fun getLeague(id: String) = footballApi?.getLeague(leagueId = id)

    suspend fun getLeagueMatches(
        seasonId: String,
        matchStatus: String,
        offset: String = "0"
    ) = ApiResponseHandler.handleResponse {
        footballApi?.getLeagueMatches(
            seasonId = seasonId,
            matchStatus = matchStatus,
            offset = offset
        )!!
    }

    suspend fun getSeasonsByLeague(leagueId: String) = footballApi?.getSeasonsByLeague(leagueId)

    suspend fun getStandings(season:String) = ApiResponseHandler.handleResponse {
        footballApi?.getStandings(season = season)!!
    }

    suspend fun getMatchGraphs(graphId: String): ResponseState<MatchGraphs> {
        cache.getGraphs(graphId)?.let {
            return ResponseState.Success(it)
        }?: run {
            val responseState = ApiResponseHandler.handleResponse {
                footballApi?.getMatchGraphs(graphId)!!
            }
            if(!responseState.data.isNullOrEmpty()) {
                cache.saveGraphs(graphId, responseState.data)
            }
            return responseState
        }
    }

    suspend fun getMatchStatistics(matchId: String): ResponseState<MatchStatistics> {
        cache.getStats(matchId)?.let {
            return ResponseState.Success(it)
        }?: run {
            val responseState = ApiResponseHandler.handleResponse {
                footballApi?.getMatchStatistics(matchId = matchId)!!
            }
            if(!responseState.data.isNullOrEmpty()) {
                cache.saveStats(matchId, responseState.data)
            }
            return responseState
        }
    }

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

    suspend fun getMatchPlayersPositions(matchId: String):ResponseState<MatchPositions> {
        cache.getMatchPlayerPositions(matchId)?.let {
            return ResponseState.Success(it)
        }?: run {
            val responseState = ApiResponseHandler.handleResponse {
                footballApi?.getMatchPlayersPositions(matchId = matchId)!!
            }
            if(!responseState.data.isNullOrEmpty()) {
                cache.saveMatchPlayerPositions(matchId, responseState.data)
            }
            return responseState
        }
    }
}