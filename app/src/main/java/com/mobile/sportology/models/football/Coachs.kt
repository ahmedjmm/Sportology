package com.mobile.sportology.models.football


import com.google.gson.annotations.SerializedName

data class Coachs(
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
) {
    data class Paging(
        @SerializedName("current")
        val current: Int?,
        @SerializedName("total")
        val total: Int?
    )

    data class Parameters(
        @SerializedName("team")
        val team: String?
    )

    data class Response(
        @SerializedName("age")
        val age: Int?,
        @SerializedName("birth")
        val birth: Birth?,
        @SerializedName("career")
        val career: List<Career?>?,
        @SerializedName("firstname")
        val firstname: String?,
        @SerializedName("height")
        val height: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("lastname")
        val lastname: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("nationality")
        val nationality: String?,
        @SerializedName("photo")
        val photo: String?,
        @SerializedName("team")
        val team: Team?,
        @SerializedName("weight")
        val weight: String?
    ) {
        data class Birth(
            @SerializedName("country")
            val country: String?,
            @SerializedName("date")
            val date: String?,
            @SerializedName("place")
            val place: String?
        )

        data class Career(
            @SerializedName("end")
            val end: String?,
            @SerializedName("start")
            val start: String?,
            @SerializedName("team")
            val team: Team?
        ) {
            data class Team(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("logo")
                val logo: String?,
                @SerializedName("name")
                val name: String?
            )
        }

        data class Team(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("logo")
            val logo: String?,
            @SerializedName("name")
            val name: String?
        )
    }
}