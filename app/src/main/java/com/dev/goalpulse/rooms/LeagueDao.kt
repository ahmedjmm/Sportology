package com.dev.goalpulse.rooms

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.dev.goalpulse.models.football.LeagueRoom

@Dao
interface LeagueDao {
    @Upsert
    suspend fun upsertLeague(league: LeagueRoom)

    @Delete
    suspend fun deleteLeague(league: LeagueRoom)

    @Query("SELECT * FROM leagueroom ORDER By name")
    suspend fun getLeaguesOrderedByName(): List<LeagueRoom>

    @Query("SELECT * FROM leagueroom where id=:id")
    suspend fun getLeague(id: Int): LeagueRoom?
}