package com.mobile.sportology.rooms

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.models.football.MatchNotificationRoom
import com.mobile.sportology.models.football.TeamRoom

@Database(entities = [LeagueRoom:: class, TeamRoom:: class, MatchNotificationRoom::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getLeagueDao(): LeagueDao

    abstract fun getTeamDao(): TeamDao

    abstract fun getMatchNotificationDao(): MatchNotificationDao
}