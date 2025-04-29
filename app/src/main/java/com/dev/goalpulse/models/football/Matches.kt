package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName

class Matches : ArrayList<Matches.MatchesItem>(){
    data class MatchesItem(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("tournament_id")
        val tournamentId: Int?,
        @SerializedName("tournament_name")
        val tournamentName: String?,
        @SerializedName("tournament_importance")
        val tournamentImportance: Int?,
        @SerializedName("season_id")
        val seasonId: Int?,
        @SerializedName("season_name")
        val seasonName: String?,
        @SerializedName("round_id")
        val roundId: Int?,
        @SerializedName("round")
        val round: Round?,
        @SerializedName("status")
        val status: Status?,
        @SerializedName("status_type")
        val statusType: String?,
        @SerializedName("time")
        val time: String?,
        @SerializedName("arena_id")
        val arenaId: Int?,
        @SerializedName("arena_name")
        val arenaName: String?,
        @SerializedName("arena_hash_image")
        val arenaHashImage: String?,
        @SerializedName("referee_id")
        val refereeId: Int?,
        @SerializedName("referee_name")
        val refereeName: String?,
        @SerializedName("referee_hash_image")
        val refereeHashImage: String?,
        @SerializedName("home_team_id")
        val homeTeamId: Int?,
        @SerializedName("home_team_name")
        val homeTeamName: String?,
        @SerializedName("home_team_hash_image")
        val homeTeamHashImage: String?,
        @SerializedName("away_team_id")
        val awayTeamId: Int?,
        @SerializedName("away_team_name")
        val awayTeamName: String?,
        @SerializedName("away_team_hash_image")
        val awayTeamHashImage: String?,
        @SerializedName("home_team_score")
        val homeTeamScore: HomeTeamScore?,
        @SerializedName("away_team_score")
        val awayTeamScore: AwayTeamScore?,
        @SerializedName("times")
        val times: Times?,
        @SerializedName("coaches")
        val coaches: Coaches?,
        @SerializedName("specific_start_time")
        val specificStartTime: String?,
        @SerializedName("start_time")
        val startTime: String?,
        @SerializedName("duration")
        val duration: Int?,
        @SerializedName("season_statistics_type")
        val seasonStatisticsType: String?,
        @SerializedName("lineups_id")
        val lineupsId: Int?,
        @SerializedName("coaches_id")
        val coachesId: Int?,
        @SerializedName("class_id")
        val classId: Int?,
        @SerializedName("class_name")
        val className: String?,
        @SerializedName("class_hash_image")
        val classHashImage: String?,
        @SerializedName("league_id")
        val leagueId: Int?,
        @SerializedName("league_name")
        val leagueName: String?,
        @SerializedName("league_hash_image")
        val leagueHashImage: String?,
        @SerializedName("last_period")
        val lastPeriod: String?
    ) {
        data class Round(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("round")
            val round: Int?,
            @SerializedName("end_time")
            val endTime: String?,
            @SerializedName("start_time")
            val startTime: String?
        )
    
        data class Status(
            @SerializedName("type")
            val type: String?,
            @SerializedName("reason")
            val reason: String?
        )
    
        data class HomeTeamScore(
            @SerializedName("current")
            val current: Int?,
            @SerializedName("display")
            val display: Int?,
            @SerializedName("period_1")
            val period1: Int?,
            @SerializedName("period_2")
            val period2: Int?,
            @SerializedName("default_time")
            val defaultTime: Int?
        )
    
        data class AwayTeamScore(
            @SerializedName("current")
            val current: Int?,
            @SerializedName("display")
            val display: Int?,
            @SerializedName("period_1")
            val period1: Int?,
            @SerializedName("period_2")
            val period2: Int?,
            @SerializedName("default_time")
            val defaultTime: Int?
        )
    
        data class Times(
            @SerializedName("extend_time_1")
            val extendTime1: Int?,
            @SerializedName("extend_time_2")
            val extendTime2: Int?,
            @SerializedName("specific_start_time")
            val specificStartTime: String?
        )
    
        data class Coaches(
            @SerializedName("away_coach_id")
            val awayCoachId: Int?,
            @SerializedName("home_coach_id")
            val homeCoachId: Int?,
            @SerializedName("away_coach_name")
            val awayCoachName: String?,
            @SerializedName("home_coach_name")
            val homeCoachName: String?,
            @SerializedName("away_coach_hash_image")
            val awayCoachHashImage: String?,
            @SerializedName("home_coach_hash_image")
            val homeCoachHashImage: String?
        )
    }
}