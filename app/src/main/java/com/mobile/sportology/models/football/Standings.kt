package com.mobile.sportology.models.football


import com.google.gson.annotations.SerializedName

data class Standings(
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
        @SerializedName("league")
        val league: String?,
        @SerializedName("season")
        val season: String?
    )

    data class Response(
        @SerializedName("league")
        val league: League?
    ) {
        data class League(
            @SerializedName("country")
            val country: String?,
            @SerializedName("flag")
            val flag: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("logo")
            val logo: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("season")
            val season: Int?,
            @SerializedName("standings")
            val standings: List<List<Standing?>?>?
        ) {
            data class Standing(
                @SerializedName("all")
                val all: All?,
                @SerializedName("away")
                val away: Away?,
                @SerializedName("description")
                val description: String?,
                @SerializedName("form")
                val form: String?,
                @SerializedName("goalsDiff")
                val goalsDiff: Int?,
                @SerializedName("group")
                val group: String?,
                @SerializedName("home")
                val home: Home?,
                @SerializedName("points")
                val points: Int?,
                @SerializedName("rank")
                val rank: Int?,
                @SerializedName("status")
                val status: String?,
                @SerializedName("team")
                val team: Team?,
                @SerializedName("update")
                val update: String?
            ) {
                data class All(
                    @SerializedName("draw")
                    val draw: Int?,
                    @SerializedName("goals")
                    val goals: Goals?,
                    @SerializedName("lose")
                    val lose: Int?,
                    @SerializedName("played")
                    val played: Int?,
                    @SerializedName("win")
                    val win: Int?
                ) {
                    data class Goals(
                        @SerializedName("against")
                        val against: Int?,
                        @SerializedName("for")
                        val forX: Int?
                    )
                }

                data class Away(
                    @SerializedName("draw")
                    val draw: Int?,
                    @SerializedName("goals")
                    val goals: Goals?,
                    @SerializedName("lose")
                    val lose: Int?,
                    @SerializedName("played")
                    val played: Int?,
                    @SerializedName("win")
                    val win: Int?
                ) {
                    data class Goals(
                        @SerializedName("against")
                        val against: Int?,
                        @SerializedName("for")
                        val forX: Int?
                    )
                }

                data class Home(
                    @SerializedName("draw")
                    val draw: Int?,
                    @SerializedName("goals")
                    val goals: Goals?,
                    @SerializedName("lose")
                    val lose: Int?,
                    @SerializedName("played")
                    val played: Int?,
                    @SerializedName("win")
                    val win: Int?
                ) {
                    data class Goals(
                        @SerializedName("against")
                        val against: Int?,
                        @SerializedName("for")
                        val forX: Int?
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
    }
}