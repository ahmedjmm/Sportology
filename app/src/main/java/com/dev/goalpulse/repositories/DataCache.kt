package com.dev.goalpulse.repositories

import android.util.LruCache
import com.dev.goalpulse.models.football.MatchGraphs
import com.dev.goalpulse.models.football.MatchPositions
import com.dev.goalpulse.models.football.MatchStatistics

/**
 * Manages caching of various types of match-related data using LRU (Least Recently Used) caches.
 * This class provides methods to get and save match statistics, graphs, and player positions.
 *
 * Each cache has a maximum number of entries and a timeout for how long entries are considered valid.
 *
 * @property statsCache Cache for [MatchStatistics].
 * @property graphsCache Cache for [MatchGraphs].
 * @property positionsCache Cache for [MatchPositions].
 */
class DataCache {
    private companion object {
        const val MAX_CACHE_ENTRIES = 20
        const val CACHE_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes
    }

    private val statsCache = LruCache<String, CacheEntry<MatchStatistics>>(MAX_CACHE_ENTRIES)
    private val graphsCache = LruCache<String, CacheEntry<MatchGraphs>>(MAX_CACHE_ENTRIES)
    private val positionsCache = LruCache<String, CacheEntry<MatchPositions>>(MAX_CACHE_ENTRIES)

    data class CacheEntry<T>(
        val data: T,
        val timestamp: Long = System.currentTimeMillis()
    ) {
        val isExpired: Boolean
            get() = System.currentTimeMillis() - timestamp > CACHE_TIMEOUT_MS
    }

    fun getStats(matchId: String): MatchStatistics? {
        return statsCache.snapshot()[matchId]
            ?.takeUnless { it.isExpired }
            ?.data
            .also { if (it == null) statsCache.remove(matchId) }
    }

    fun saveStats(matchId: String, data: MatchStatistics) {
        statsCache.put(matchId, CacheEntry(data))
    }

    fun getGraphs(graphId: String): MatchGraphs? {
        return graphsCache.snapshot()[graphId]
            ?.takeUnless { it.isExpired }
            ?.data
            .also { if (it == null) graphsCache.remove(graphId) }
    }

    fun saveGraphs(graphId: String, data: MatchGraphs) {
        graphsCache.put(graphId, CacheEntry(data))
    }

    fun getMatchPlayerPositions(matchId: String): MatchPositions? {
        return positionsCache.snapshot()[matchId]
            ?.takeUnless { it.isExpired }
            ?.data
            .also { if (it == null) positionsCache.remove(matchId) }
    }

    fun saveMatchPlayerPositions(matchId: String, data: MatchPositions) {
        positionsCache.put(matchId, CacheEntry(data))
    }
}