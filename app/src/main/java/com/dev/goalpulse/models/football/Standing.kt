package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName

class Standing : ArrayList<Standing.StandingItem>(){
    data class StandingItem(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("tournament_id")
        val tournamentId: Int?,
        @SerializedName("tournament_name")
        val tournamentName: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("season_id")
        val seasonId: Int?,
        @SerializedName("season_name")
        val seasonName: String?,
        @SerializedName("league_id")
        val leagueId: Int?,
        @SerializedName("league_name")
        val leagueName: String?,
        @SerializedName("league_hash_image")
        val leagueHashImage: String?,
        @SerializedName("competitors")
        val competitors: List<Competitor?>?
    ) {
        data class Competitor(
            @SerializedName("wins")
            val wins: Int?,
            @SerializedName("draws")
            val draws: Int?,
            @SerializedName("losses")
            val losses: Int?,
            @SerializedName("points")
            val points: Int?,
            @SerializedName("matches")
            val matches: Int?,
            @SerializedName("team_id")
            val teamId: Int?,
            @SerializedName("position")
            val position: Int?,
            @SerializedName("team_name")
            val teamName: String?,
            @SerializedName("scores_for")
            val scoresFor: Int?,
            @SerializedName("scores_against")
            val scoresAgainst: Int?,
            @SerializedName("team_hash_image")
            val teamHashImage: String?
        )
    }
}