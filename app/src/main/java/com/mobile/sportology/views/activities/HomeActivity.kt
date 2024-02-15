package com.mobile.sportology.views.activities

import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.snackbar.Snackbar
import com.mobile.sportology.R
import com.mobile.sportology.repositories.DefaultLocalRepository
import com.mobile.sportology.repositories.RemoteRepository
import com.mobile.sportology.servicesAndUtilities.NetworkConnectivityReceiver
import com.mobile.sportology.viewModels.FootBallViewModel
import com.mobile.sportology.viewModels.MyViewModelProvider
import com.mobile.sportology.viewModels.NewsViewModel
import com.mobile.sportology.viewModels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity: AppCompatActivity(), NetworkConnectivityReceiver.NetworkStateListener {
    @Inject
    lateinit var intentFilter: IntentFilter
    @Inject
    lateinit var networkConnectivityReceiver: NetworkConnectivityReceiver
    @Inject
    lateinit var remoteRepository: RemoteRepository
    @Inject
    lateinit var localRepository: DefaultLocalRepository
    val footBallViewModel: FootBallViewModel by lazy {
        ViewModelProvider(this, MyViewModelProvider(
            this.application, remoteRepository = remoteRepository,
            defaultLocalRepository = localRepository))[FootBallViewModel::class.java]
    }
    val newsViewModel: NewsViewModel by lazy {
        ViewModelProvider(this, MyViewModelProvider(
            this.application, remoteRepository = remoteRepository,
            defaultLocalRepository = null))[NewsViewModel::class.java]
    }

    val settingsViewModel: SettingsViewModel by lazy {
        ViewModelProvider(this, MyViewModelProvider(
            this.application, remoteRepository = null,
            defaultLocalRepository = null))[SettingsViewModel::class.java]
    }

    lateinit var snackBar: Snackbar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navigationRail: NavigationRailView
    private lateinit var navHostFragment: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initializeViews()
    }

    override fun onResume() {
        super.onResume()
        networkConnectivityReceiver.setListener(this)
        registerReceiver(networkConnectivityReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        networkConnectivityReceiver.setListener(null)
        unregisterReceiver(networkConnectivityReceiver)
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {
        if(!isConnected) snackBar.show()
        else snackBar.dismiss()
    }

    private fun initializeViews() {
        navHostFragment = findViewById(R.id.navHostFragment)
        val navController = navHostFragment.getFragment<NavHostFragment>().navController
        snackBar = Snackbar.make(
            navHostFragment,
            this.resources.getString(R.string.unable_to_connect),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(this.resources.getString(R.string.dismiss)) {
            snackBar.dismiss()
        }

        // for tablet devices
        if(resources.configuration.smallestScreenWidthDp >= 600) {
            navigationRail = findViewById(R.id.navigation_rail)
            navigationRail.setupWithNavController(navController)
        }
        // for mobile devices
        else {
            bottomNavigation = findViewById(R.id.bottom_navigation)
            bottomNavigation.setupWithNavController(navController)
        }
    }
}