package com.dev.goalpulse.models.football


import com.dev.goalpulse.models.football.TVChannels.TVChannelsItem
import com.google.gson.annotations.SerializedName

class TVChannels : ArrayList<TVChannelsItem>(){
    data class TVChannelsItem(
        @SerializedName("match_id")
        val matchId: Int?,
        @SerializedName("country_id")
        val countryId: Int?,
        @SerializedName("country_name")
        val countryName: String?,
        @SerializedName("country_hash_image")
        val countryHashImage: String?,
        @SerializedName("alpha")
        val alpha: String?,
        @SerializedName("tv_channels")
        val tvChannels: List<TvChannel?>?
    ) {
        data class TvChannel(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("name")
            val name: String?
        )
    }
}