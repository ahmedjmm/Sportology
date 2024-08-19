package com.dev.goalpulse.servicesAndUtilities

import android.content.SearchRecentSuggestionsProvider

class MySuggestionProvider: SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.mobile.sportology.servicesAndUtilities.MySuggestionProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }
}