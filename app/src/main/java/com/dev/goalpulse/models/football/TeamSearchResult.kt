package com.dev.goalpulse.models.football


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TeamSearchResult(
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
        @SerializedName("team")
        val team: Team?,
        @SerializedName("venue")
        val venue: Venue?
    ): Serializable {
        data class Team(
            @SerializedName("code")
            val code: String?,
            @SerializedName("country")
            val country: String?,
            @SerializedName("founded")
            val founded: Int?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("logo")
            val logo: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("national")
            val national: Boolean?
        ): Serializable

        data class Venue(
            @SerializedName("address")
            val address: String?,
            @SerializedName("capacity")
            val capacity: Int?,
            @SerializedName("city")
            val city: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("surface")
            val surface: String?
        ): Serializable
    }
}