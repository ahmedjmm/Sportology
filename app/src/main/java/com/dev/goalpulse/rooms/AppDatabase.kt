package com.dev.goalpulse.rooms

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.goalpulse.models.football.LeagueRoom
import com.dev.goalpulse.models.football.MatchNotificationRoom
import com.dev.goalpulse.models.football.Team

@Database(entities = [LeagueRoom:: class, Team:: class, MatchNotificationRoom::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getLeagueDao(): LeagueDao

    abstract fun getTeamDao(): TeamDao

    abstract fun getMatchNotificationDao(): MatchNotificationDao
}