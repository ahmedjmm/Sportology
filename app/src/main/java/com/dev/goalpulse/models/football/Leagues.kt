package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName

class Leagues : ArrayList<Leagues.LeagueItem>(){
    data class LeagueItem(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("importance")
        val importance: Int?,
        @SerializedName("level")
        val level: Int?,
        @SerializedName("current_champion_team_id")
        val currentChampionTeamId: Int?,
        @SerializedName("current_champion_team_name")
        val currentChampionTeamName: String?,
        @SerializedName("current_champion_team_hash_image")
        val currentChampionTeamHashImage: String?,
        @SerializedName("current_champion_team_num_titles")
        val currentChampionTeamNumTitles: Int?,
        @SerializedName("teams_most_titles")
        val teamsMostTitles: List<TeamsMostTitle?>?,
        @SerializedName("most_titles")
        val mostTitles: Int?,
        @SerializedName("primary_color")
        val primaryColor: String?,
        @SerializedName("secondary_color")
        val secondaryColor: String?,
        @SerializedName("lower_leagues")
        val lowerLeagues: List<LowerLeague?>?,
        @SerializedName("start_league")
        val startLeague: String?,
        @SerializedName("end_league")
        val endLeague: String?,
        @SerializedName("hash_image")
        val hashImage: String?,
        @SerializedName("class_id")
        val classId: Int?,
        @SerializedName("class_name")
        val className: String?,
        @SerializedName("class_hash_image")
        val classHashImage: String?
    ) {
        data class TeamsMostTitle(
            @SerializedName("team_id")
            val teamId: Int?,
            @SerializedName("team_name")
            val teamName: String?,
            @SerializedName("team_hash_image")
            val teamHashImage: String?
        )
    
        data class LowerLeague(
            @SerializedName("league_id")
            val leagueId: Int?,
            @SerializedName("league_name")
            val leagueName: String?,
            @SerializedName("league_hash_image")
            val leagueHashImage: String?
        )
    }
}