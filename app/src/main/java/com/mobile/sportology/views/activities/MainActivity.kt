package com.mobile.sportology.views.activities

import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.mobile.sportology.R
import com.mobile.sportology.databinding.ActivityMainBinding
import com.mobile.sportology.repositories.DefaultLocalRepository
import com.mobile.sportology.repositories.RemoteRepository
import com.mobile.sportology.servicesAndUtilities.NetworkConnectivityReceiver
import com.mobile.sportology.viewModels.FootBallViewModel
import com.mobile.sportology.viewModels.MyViewModelProvider
import com.mobile.sportology.viewModels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity(), NetworkConnectivityReceiver.NetworkStateListener {
    @Inject
    lateinit var intentFilter: IntentFilter
    @Inject
    lateinit var networkConnectivityReceiver: NetworkConnectivityReceiver
    @Inject
    lateinit var remoteRepository: RemoteRepository
    @Inject
    lateinit var localRepository: DefaultLocalRepository
    private lateinit var snackBar: Snackbar
    lateinit var binding: ActivityMainBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.bottomNavigation?.let {
            snackBar = Snackbar.make(
                it,
                this.resources.getString(R.string.unable_to_connect),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(this.resources.getString(R.string.dismiss)) {
                snackBar.dismiss()
            }
        }

        binding.navigationRail?.let {
            snackBar = Snackbar.make(
                binding.navHostFragment,
                this.resources.getString(R.string.unable_to_connect),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(this.resources.getString(R.string.dismiss)) {
                snackBar.dismiss()
            }
        }

        val navController = binding.navHostFragment.getFragment<NavHostFragment>().navController
        binding.bottomNavigation?.setupWithNavController(navController)
        binding.navigationRail?.setupWithNavController(navController)
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
}