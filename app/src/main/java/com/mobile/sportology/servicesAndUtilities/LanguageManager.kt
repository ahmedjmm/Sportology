package com.mobile.sportology.servicesAndUtilities

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaFormat.KEY_LANGUAGE
import java.util.Locale


class LanguageManager(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    fun setCurrentLanguage(language: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, language).apply()
        updateAppLocale(language)
    }

    private fun updateAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        reloadResources(context)
    }

    private fun reloadResources(context: Context) {
        val configuration = context.resources.configuration
        configuration.setLocale(Locale.getDefault())
        context.createConfigurationContext(configuration)
    }
}
