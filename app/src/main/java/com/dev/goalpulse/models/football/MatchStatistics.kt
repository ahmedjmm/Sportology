package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName

class MatchStatistics : ArrayList<MatchStatistics.MatchStatisticsItem>() {
    data class MatchStatisticsItem(
        @SerializedName("match_id")
        var matchId: Int?,
        @SerializedName("statistics")
        var statistics: List<Statistic?>?
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
            val homeTeam: String?,

            var homeProgressbarCurrentValue: Int = 0,
            var homeProgressbarMaxValue: Int = 100,
            var awayProgressbarCurrentValue: Int = 0,
            var awayProgressbarMaxValue: Int = 100
        ) {
            fun copy(
                homeProgressbarCurrentValue: Int = this.homeProgressbarCurrentValue,
                homeProgressbarMaxValue: Int = this.homeProgressbarMaxValue,
                awayProgressbarCurrentValue: Int = this.awayProgressbarCurrentValue,
                awayProgressbarMaxValue: Int = this.awayProgressbarMaxValue
            ): Statistic {
                return Statistic(
                    type = this.type,
                    period = this.period,
                    category = this.category,
                    awayTeam = this.awayTeam,
                    homeTeam = this.homeTeam,
                    homeProgressbarCurrentValue = homeProgressbarCurrentValue,
                    homeProgressbarMaxValue = homeProgressbarMaxValue,
                    awayProgressbarCurrentValue = awayProgressbarCurrentValue,
                    awayProgressbarMaxValue = awayProgressbarMaxValue
                )
            }
        }
    }
}