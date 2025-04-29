package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName

class Seasons : ArrayList<Seasons.SeasonsItem>(){
    data class SeasonsItem(
        @SerializedName("league_id")
        val leagueId: Int?,
        @SerializedName("league_name")
        val leagueName: String?,
        @SerializedName("league_hash_image")
        val leagueHashImage: String?,
        @SerializedName("seasons")
        val seasons: List<Season?>?
    ) {
        data class Season(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("year")
            val year: String?,
            @SerializedName("start_time")
            val startTime: String?
        )
    }
}