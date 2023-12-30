package com.mobile.sportology.models.news


import com.google.gson.annotations.SerializedName

data class Sources(
    @SerializedName("sources")
    val sources: List<Source?>?,
    @SerializedName("status")
    val status: String?
) {
    data class Source(
        @SerializedName("category")
        val category: String?,
        @SerializedName("country")
        val country: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("language")
        val language: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("url")
        val url: String?
    )
}