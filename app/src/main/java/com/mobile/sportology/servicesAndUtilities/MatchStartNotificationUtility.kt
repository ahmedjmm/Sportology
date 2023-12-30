package com.mobile.sportology.servicesAndUtilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mobile.sportology.R
import javax.inject.Inject
import javax.inject.Named

class MatchStartNotificationUtility @Inject constructor(
    val context: Context,
    private val alarmManager: AlarmManager,
    @Named("IntentForMatchStartNotification")
    val intent: Intent
) {
    fun scheduleNotification(time: Long, matchId: Int, seasonId: Int, body: String,
                             isChecked: Boolean, leagueId: Int, action: String
    ) {
        intent.apply {
            setAction(action)
            putExtra("body", body)
            putExtra("isChecked", isChecked)
            putExtra("matchId", matchId)
            putExtra("season", seasonId)
            putExtra("leagueId", leagueId)
            putExtra("matchTime", time)
        }
        //matchId is the broadcast requestCode
        val broadCastPendingIntent = PendingIntent.getBroadcast(context, matchId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        if(intent.action == context.getString(R.string.MATCH_START_NOTIFICATION_CANCEL))
            broadCastPendingIntent.send()
        else
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, broadCastPendingIntent)
    }
}