package com.mobile.sportology

import android.app.Application
import com.mobile.sportology.repositories.DefaultLocalRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class MyApplication: Application() {
    @Inject
    lateinit var localRepository: DefaultLocalRepository
    override fun onCreate() {
        super.onCreate()
        MainScope().launch {
            getAllSavedNotifications()
        }
//        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    private suspend fun getAllSavedNotifications() {
        val savedMatchesNotifications = localRepository.getAllMatchNotifications()
        savedMatchesNotifications?.forEach {
            Shared.SAVED_MATCHES_NOTIFICATIONS_IDS.add(it.matchId)
        }
    }
}