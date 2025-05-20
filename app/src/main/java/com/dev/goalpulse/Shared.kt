package com.dev.goalpulse

import okhttp3.internal.immutableListOf

object Shared {
    var isConnected = true

    const val NEWS_BASE_URL = "https://newsapi.org"
    const val FOOTBALL_BASE_URL = "https://football.sportdevs.com"
    val SAVED_MATCHES_NOTIFICATIONS_IDS = mutableListOf<Int>()
    //england, spain, italy, germany, france, egypt
    val LEAGUES_IDS = immutableListOf("eq.16", "eq.55", "eq.57", "eq.21", "eq.662", "eq.27")
}