package com.dev.goalpulse.dependencyInjection

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.preference.PreferenceManager
import com.dev.goalpulse.R
import com.dev.goalpulse.servicesAndUtilities.MatchStartNotificationReceiver
import com.dev.goalpulse.servicesAndUtilities.MatchStartNotificationUtility
import com.dev.goalpulse.views.activities.MatchDetailsActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidDI {

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService(AlarmManager::class.java)

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