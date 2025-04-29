package com.dev.goalpulse.dependencyInjection

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.dev.goalpulse.BuildConfig
import com.dev.goalpulse.R
import com.google.gson.GsonBuilder
import com.dev.goalpulse.Shared
import com.dev.goalpulse.api.FootballApi
import com.dev.goalpulse.api.NewsApi
import com.dev.goalpulse.repositories.DefaultLocalRepository
import com.dev.goalpulse.repositories.RemoteRepository
import com.dev.goalpulse.rooms.AppDatabase
import com.dev.goalpulse.rooms.LeagueDao
import com.dev.goalpulse.rooms.MatchNotificationDao
import com.dev.goalpulse.rooms.TeamDao
import com.dev.goalpulse.servicesAndUtilities.MatchStartNotificationReceiver
import com.dev.goalpulse.servicesAndUtilities.MatchStartNotificationUtility
import com.dev.goalpulse.servicesAndUtilities.NetworkConnectivityReceiver
import com.dev.goalpulse.views.activities.MatchDetailsActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService(AlarmManager::class.java)

    @Singleton
    @Provides
    fun provideNetworkConnectivityReceiver() = NetworkConnectivityReceiver()

    @Singleton
    @Provides
    fun provideIntentForNetworkConnectivityReceiver() =
        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideLocalRepository(
        leagueDao: LeagueDao,
        teamDao: TeamDao,
        matchNotificationDao: MatchNotificationDao
    ) = DefaultLocalRepository(leagueDao, teamDao, matchNotificationDao)

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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "League&TeamsDatabase"
        ).build()
    }

    @Singleton
    @Provides
    fun provideRemoteRepository(newsApi: NewsApi, footballApi: FootballApi) =
        RemoteRepository(footballApi = footballApi, newsApi = newsApi)

    @Singleton
    @Provides
    fun provideFootballAPI(): FootballApi {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder().addHeader(
                    "Authorization", BuildConfig.FOOTBALL_API_KEY
                ).build()
                return chain.proceed(request)
            }
        })

        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(Shared.FOOTBALL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
            .create(FootballApi::class.java)
    }

    @Singleton
    @Provides
    fun provideNewsAPI(): NewsApi {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder().addHeader(
                    "X-Api-Key", BuildConfig.NEWS_API_KEY
                ).build()
                return chain.proceed(request)
            }
        })

        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(Shared.NEWS_BASE_URL + "/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context) = context

    @Provides
    @Named("IntentForMatchDetailsActivity")
    fun provideIntentForMatchDetailsActivity(
        @ApplicationContext
        context: Context,
    ) = Intent(context, MatchDetailsActivity::class.java)

    @Provides
    @Named("IntentForMatchStartNotification")
    fun provideIntentForMatchStartNotification(@ApplicationContext context: Context) =
        Intent(context, MatchStartNotificationReceiver::class.java)
            .putExtra("title", context.getString(R.string.MATCH_START_NOTIFICATION_TITLE_AND_CHANNEL))
            .putExtra("channelId", context.getString(R.string.MATCH_START_NOTIFICATION_CHANNEL_ID))
            .putExtra("channelName", context.getString(R.string.MATCH_START_NOTIFICATION_TITLE_AND_CHANNEL))
            .putExtra("description", context.getString(R.string.MATCH_START_NOTIFICATION_DESCRIPTION))

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext
        context: Context
    ) = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun matchStartNotificationUtility(
        @ApplicationContext
        context: Context,
        alarmManager: AlarmManager,
        @Named("IntentForMatchStartNotification")
        intent: Intent,
    ) = MatchStartNotificationUtility(context, alarmManager, intent)
}