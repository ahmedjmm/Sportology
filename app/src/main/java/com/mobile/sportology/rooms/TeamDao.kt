package com.mobile.sportology.rooms

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.mobile.sportology.models.football.TeamRoom

@Dao
interface TeamDao {
    @Upsert
    suspend fun upsertTeam(team: TeamRoom)

    @Delete
    suspend fun deleteTeam(team: TeamRoom)

    @Query("SELECT * FROM teamroom ORDER By name")
    suspend fun getTeamsOrderedByName(): List<TeamRoom>
    
    @Query("SELECT * FROM teamroom where id=:id")
    suspend fun getTeam(id: Int): TeamRoom?
}