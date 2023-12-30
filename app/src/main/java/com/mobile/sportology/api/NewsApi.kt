package com.mobile.sportology.api

import com.mobile.sportology.models.football.*
import com.mobile.sportology.models.news.News
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/everything")
    suspend fun getEverythingNews(
        @Query("q")
        q: String,
        @Query("language")
        language: String,
        @Query("sortBy")
        sortBy: String,
        @Query("sources")
        sources: String,
        @Query("searchIn")
        searchIn: String,
    ): Response<News>

    @GET("v2/top-headlines")
    suspend fun getTopHeadLinesNews(
        @Query("language")
        language: String,
        @Query("category")
        category: String = "sports"
    ): Response<News>
}