package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LeagueSearchResult(
    @SerializedName("errors")
    val errors: List<Any?>?,
    @SerializedName("get")
    val `get`: String?,
    @SerializedName("paging")
    val paging: Paging?,
    @SerializedName("parameters")
    val parameters: Parameters?,
    @SerializedName("response")
    val response: List<Response?>?,
    @SerializedName("results")
    val results: Int?
): Serializable {
    data class Paging(
        @SerializedName("current")
        val current: Int?,
        @SerializedName("total")
        val total: Int?
    ): Serializable

    data class Parameters(
        @SerializedName("search")
        val search: String?
    ): Serializable

    data class Response(
        @SerializedName("country")
        val country: Country?,
        @SerializedName("league")
        val league: League?,
        @SerializedName("seasons")
        val seasons: List<Season?>?
    ): Serializable {
        data class Country(
            @SerializedName("code")
            val code: String?,
            @SerializedName("flag")
            val flag: String?,
            @SerializedName("name")
            val name: String?
        ): Serializable

        data class League(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("logo")
            val logo: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("type")
            val type: String?
        ): Serializable

        data class Season(
            @SerializedName("coverage")
            val coverage: Coverage?,
            @SerializedName("current")
            val current: Boolean?,
            @SerializedName("end")
            val end: String?,
            @SerializedName("start")
            val start: String?,
            @SerializedName("year")
            val year: Int?
        ): Serializable {
            data class Coverage(
                @SerializedName("fixtures")
                val fixtures: Fixtures?,
                @SerializedName("injuries")
                val injuries: Boolean?,
                @SerializedName("odds")
                val odds: Boolean?,
                @SerializedName("players")
                val players: Boolean?,
                @SerializedName("predictions")
                val predictions: Boolean?,
                @SerializedName("standings")
                val standings: Boolean?,
                @SerializedName("top_assists")
                val topAssists: Boolean?,
                @SerializedName("top_cards")
                val topCards: Boolean?,
                @SerializedName("top_scorers")
                val topScorers: Boolean?
            ): Serializable {
                data class Fixtures(
                    @SerializedName("events")
                    val events: Boolean?,
                    @SerializedName("lineups")
                    val lineups: Boolean?,
                    @SerializedName("statistics_fixtures")
                    val statisticsFixtures: Boolean?,
                    @SerializedName("statistics_players")
                    val statisticsPlayers: Boolean?
                ): Serializable
            }
        }
    }
}