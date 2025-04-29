package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName

class MatchStatistics : ArrayList<MatchStatistics.MatchStatisticsItem>(){
    data class MatchStatisticsItem(
        @SerializedName("match_id")
        val matchId: Int?,
        @SerializedName("statistics")
        val statistics: List<Statistic?>?
    ) {
        data class Statistic(
            @SerializedName("type")
            val type: String?,
            @SerializedName("period")
            val period: String?,
            @SerializedName("category")
            val category: String?,
            @SerializedName("away_team")
            val awayTeam: String?,
            @SerializedName("home_team")
            val homeTeam: String?
        )
    }
}