package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName

class MatchGraphs : ArrayList<MatchGraphs.MatchGraphsItem>(){
    data class MatchGraphsItem(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("period_time")
        val periodTime: Int?,
        @SerializedName("period_count")
        val periodCount: Int?,
        @SerializedName("points")
        val points: List<Point?>?
    ) {
        data class Point(
            @SerializedName("value")
            val value: Int?,
            @SerializedName("minute")
            val minute: Int?
        )
    }
}