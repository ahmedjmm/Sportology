package com.dev.goalpulse.rooms

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.dev.goalpulse.models.football.Team

@Dao
interface TeamDao {
    @Upsert
    suspend fun upsertTeam(team: Team)

    @Delete
    suspend fun deleteTeam(team: Team)

    @Query("SELECT * FROM team ORDER By name")
    suspend fun getTeamsOrderedByName(): List<Team>
    
    @Query("SELECT * FROM team where id=:id")
    suspend fun getTeam(id: Int): Team?
}