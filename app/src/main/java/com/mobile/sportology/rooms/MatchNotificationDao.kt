package com.mobile.sportology.rooms

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.mobile.sportology.models.football.MatchNotificationRoom

@Dao
interface MatchNotificationDao {
    @Upsert
    suspend fun upsertMatchNotification(matchNotification: MatchNotificationRoom)

    @Delete
    suspend fun deleteMatchNotification(matchNotification: MatchNotificationRoom)

    @Query("SELECT * FROM matchnotificationroom")
    suspend fun getAllMatchNotifications(): List<MatchNotificationRoom>?

    @Query("SELECT * FROM matchnotificationroom where matchId=:id")
    suspend fun getMatchNotificationById(id: Int): MatchNotificationRoom

    @Query("DELETE FROM matchnotificationroom where matchId=:id")
    suspend fun deleteMatchNotificationById(id: Int)
}