package com.dev.goalpulse.servicesAndUtilities

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dev.goalpulse.R
import com.dev.goalpulse.models.football.MatchNotificationRoom
import com.dev.goalpulse.repositories.DefaultLocalRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@AndroidEntryPoint
class MatchStartNotificationReceiver: BroadcastReceiver() {
    @Inject
    @Named("IntentForMatchDetailsActivity")
    lateinit var matchDetailsActivityIntent: Intent
    private lateinit var notification: Notification
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var localRepository: DefaultLocalRepository
    @Inject
    lateinit var alarmManager: AlarmManager

    override fun onReceive(context: Context, intent: Intent) = goAsync {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_REBOOT -> {
                Log.i("notificationState", intent.action!!)
                val matchNotifications = localRepository.getAllMatchNotifications()
                if(matchNotifications?.isNotEmpty() == true) {
                    matchNotifications.forEach {
                        if(System.currentTimeMillis() >= it.matchTime) {
                            showNotification(context, it)
                            // remove match start notification if showed
                            localRepository.deleteMatchNotification(it)
                        }
                        else {
                            intent.action = context.getString(R.string.MATCH_START_NOTIFICATION_SHOW)
                            intent.putExtra("body", "${it.home} VS ${it.away}")
                            scheduleAlarm(context, intent, it)
                        }
                    }
                }
            }
            context.getString(R.string.MATCH_START_NOTIFICATION_SHOW) -> {
                Log.i("notificationState", intent.action!!)
                if(System.currentTimeMillis() >= intent.getLongExtra("matchTime", 0)) {
                    showNotification(context, intent)
                    // remove match start notification if showed
                    localRepository.deleteMatchStartNotificationById(intent.getIntExtra("matchId", 0))
                }
                else {
                    scheduleAlarm(context, intent)
                }
            }
            context.getString(R.string.MATCH_START_NOTIFICATION_CANCEL) -> {
                Log.i("notificationState", intent.action!!)
                cancelMachNotification(context, intent)
            }
        }
    }

    private fun cancelMachNotification(context: Context, intent: Intent) {
        Log.i("notificationState",  intent.getIntExtra("matchId", 0).toString())
        alarmManager.cancel(PendingIntent.getBroadcast(context, intent.getIntExtra("matchId", 0), intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        )
        Log.i("notificationState", "notification canceled")
    }

    private fun scheduleAlarm(
        context: Context,
        intent: Intent,
        matchNotification: MatchNotificationRoom
    ) {
        val broadCastPendingIntent = PendingIntent.getBroadcast(
            context, matchNotification.matchId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            matchNotification.matchTime,
            broadCastPendingIntent
        )
        Log.i("notificationState", "alarm scheduled")
    }

    private fun scheduleAlarm(context: Context, intent: Intent) {
        val broadCastPendingIntent = PendingIntent.getBroadcast(
            context,
            intent.getIntExtra("matchId", 0),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            intent.getLongExtra("matchTime", 0),
            broadCastPendingIntent
        )
        Log.i("notificationState", "alarm scheduled")
    }

    private fun showNotification(context: Context, intent: Intent) {
        matchDetailsActivityIntent.putExtra("matchId", intent.getIntExtra("matchId", 0))
        val matchDetailsActivityPendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(matchDetailsActivityIntent)
            getPendingIntent(
                intent.getIntExtra("matchId", 0),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
        val notificationChannel = NotificationChannel(
            context.getString(R.string.MATCH_START_NOTIFICATION_CHANNEL_ID),
            context.getString(R.string.MATCH_START_NOTIFICATION_TITLE_AND_CHANNEL),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.MATCH_START_NOTIFICATION_DESCRIPTION)
        }
        notificationManager.createNotificationChannel(notificationChannel)
        notification = NotificationCompat.Builder(context, context.getString(R.string.MATCH_START_NOTIFICATION_CHANNEL_ID))
            .setSmallIcon(R.drawable.ic_football)
            .setContentTitle(context.getString(R.string.MATCH_START_NOTIFICATION_TITLE_AND_CHANNEL))
            .setContentText(intent.getStringExtra("body"))
            .setContentIntent(matchDetailsActivityPendingIntent)
            .setStyle(NotificationCompat.InboxStyle())
            .setAutoCancel(true)
            .build()
        notificationManager.notify(intent.getIntExtra("matchId", 0), notification)
        Log.i("notificationState", "notification showed")
    }

    private fun showNotification(context: Context, matchNotification: MatchNotificationRoom) {
        matchDetailsActivityIntent.putExtra("matchId", matchNotification.matchId)
        val matchDetailsActivityPendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(matchDetailsActivityIntent)
            getPendingIntent(
                matchNotification.matchId,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
        val notificationChannel = NotificationChannel(
            context.getString(R.string.MATCH_START_NOTIFICATION_CHANNEL_ID),
            context.getString(R.string.MATCH_START_NOTIFICATION_TITLE_AND_CHANNEL),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.MATCH_START_NOTIFICATION_DESCRIPTION)
        }
        notificationManager.createNotificationChannel(notificationChannel)
        notification = NotificationCompat.Builder(context, context.getString(R.string.MATCH_START_NOTIFICATION_CHANNEL_ID))
            .setSmallIcon(R.drawable.ic_football)
            .setContentTitle(context.getString(R.string.MATCH_START_NOTIFICATION_TITLE_AND_CHANNEL))
            .setContentText("${matchNotification.home} VS ${matchNotification.away}")
            .setContentIntent(matchDetailsActivityPendingIntent)
            .setDeleteIntent(matchDetailsActivityPendingIntent)
            .setStyle(NotificationCompat.InboxStyle())
            .setAutoCancel(true)
            .build()
        notificationManager.notify(matchNotification.matchId, notification)
        Log.i("notificationState", "notification showed")
    }
}

/**
 * async block to do suspended or io calls
 * @param context coroutine context.
 * @param block suspended block to be executed.
 *
 * Best practice is to use ork manager especially for longer and guaranteed tasks like upload photos or network calls with slow internet connection
 */
internal fun BroadcastReceiver.goAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    @OptIn(DelicateCoroutinesApi::class) // Must run globally; there's no teardown callback.
    GlobalScope.launch(context) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}