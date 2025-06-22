package com.dev.goalpulse.dependencyInjection

import android.content.Context
import androidx.room.Room
import com.dev.goalpulse.api.FootballApi
import com.dev.goalpulse.api.NewsApi
import com.dev.goalpulse.repositories.DataCache
import com.dev.goalpulse.repositories.DefaultLocalRepository
import com.dev.goalpulse.repositories.RemoteRepository
import com.dev.goalpulse.rooms.AppDatabase
import com.dev.goalpulse.rooms.LeagueDao
import com.dev.goalpulse.rooms.MatchNotificationDao
import com.dev.goalpulse.rooms.TeamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoriesDI {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "League&TeamsDatabase"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMatchDataCache(): DataCache = DataCache()

    @Singleton
    @Provides
    fun provideRemoteRepository(newsApi: NewsApi, footballApi: FootballApi, cache: DataCache) =
        RemoteRepository(
            footballApi = footballApi,
            newsApi = newsApi,
            cache = cache
        )

    @Singleton
    @Provides
    fun provideLeagueDao(appDatabase: AppDatabase) = appDatabase.getLeagueDao()

    @Singleton
    @Provides
    fun provideTeamDao(appDatabase: AppDatabase) = appDatabase.getTeamDao()

    @Singleton
    @Provides
    fun provideMachNotificationDao(appDatabase: AppDatabase) = appDatabase.getMatchNotificationDao()

    @Provides
    @Singleton
    fun provideLocalRepository(
        leagueDao: LeagueDao,
        teamDao: TeamDao,
        matchNotificationDao: MatchNotificationDao
    ) = DefaultLocalRepository(leagueDao, teamDao, matchNotificationDao)
}