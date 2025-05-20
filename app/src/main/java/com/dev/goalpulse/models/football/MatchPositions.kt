package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName

class MatchPositions : ArrayList<MatchPositions.MatchPositionsItem>(){
    data class MatchPositionsItem(
        @SerializedName("match_id")
        val matchId: Int?,
        @SerializedName("team_id")
        val teamId: Int?,
        @SerializedName("positions")
        val positions: List<Position?>?
    ) {
        data class Position(
            @SerializedName("points")
            val points: Int?,
            @SerializedName("average_x")
            val averageX: Double?,
            @SerializedName("average_y")
            val averageY: Double?,
            @SerializedName("player_id")
            val playerId: Int?,
            @SerializedName("player_name")
            val playerName: String?,
            @SerializedName("player_hash_image")
            val playerHashImage: String?
        )
    }
}