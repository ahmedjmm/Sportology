package com.dev.goalpulse.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.goalpulse.repositories.DefaultLocalRepository
import com.dev.goalpulse.repositories.RemoteRepository
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MyViewModelProvider @Inject constructor(
    private val application: Application,
    private val remoteRepository: RemoteRepository?,
    private val defaultLocalRepository: DefaultLocalRepository?
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(viewModelClass: Class<T>): T {
        if (viewModelClass.isAssignableFrom(FootBallViewModel::class.java))
            return FootBallViewModel(application, remoteRepository!!, defaultLocalRepository!!) as T
        else if (viewModelClass.isAssignableFrom(MatchDetailsViewModel::class.java))
            return MatchDetailsViewModel(application, remoteRepository!!) as T
        else if (viewModelClass.isAssignableFrom(SearchActivityViewModel::class.java))
            return SearchActivityViewModel(application, remoteRepository!!) as T
        else if (viewModelClass.isAssignableFrom(NewsViewModel::class.java))
            return NewsViewModel(application, remoteRepository!!) as T
        else if (viewModelClass.isAssignableFrom(FavoritesActivityViewModel::class.java))
            return FavoritesActivityViewModel(defaultLocalRepository = defaultLocalRepository!!,
                remoteRepository = remoteRepository!!) as T
        else if (viewModelClass.isAssignableFrom(AllSearchActivityViewModel::class.java))
            return AllSearchActivityViewModel(localRepository = defaultLocalRepository!!) as T
        else if (viewModelClass.isAssignableFrom(SettingsViewModel::class.java))
            return SettingsViewModel() as T
        throw IllegalArgumentException("unknown view model")
    }
}