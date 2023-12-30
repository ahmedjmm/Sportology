package com.mobile.sportology.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.carousel.CarouselLayoutManager
import com.mobile.sportology.R
import com.mobile.sportology.databinding.ActivityFavoritesBinding
import com.mobile.sportology.repositories.DefaultLocalRepository
import com.mobile.sportology.repositories.RemoteRepository
import com.mobile.sportology.viewModels.FavoritesActivityViewModel
import com.mobile.sportology.viewModels.MyViewModelProvider
import com.mobile.sportology.views.adapters.footballAdapters.CarouselRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesActivity : AppCompatActivity() {
    @Inject
    lateinit var defaultLocalRepository: DefaultLocalRepository
    @Inject
    lateinit var remoteRepository: RemoteRepository
    private val viewModel: FavoritesActivityViewModel by lazy {
        ViewModelProvider(this, MyViewModelProvider(
            application = application, defaultLocalRepository = defaultLocalRepository,
            remoteRepository = remoteRepository))[FavoritesActivityViewModel::class.java]
    }
    private lateinit var favoritesActivityBinding: ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoritesActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorites)

        lifecycleScope.launch(Dispatchers.IO) {
            val teams = viewModel.getTeams()
            val leagues = viewModel.getLeagues()

            withContext(Dispatchers.Main) {
                if(leagues.isNotEmpty()) {
                    favoritesActivityBinding.leaguesError.root.visibility = View.GONE
                    val carouselRecyclerViewAdapter = CarouselRecyclerViewAdapter(leagues, null)
                    favoritesActivityBinding.leaguesRecyclerView.visibility = View.VISIBLE
                    favoritesActivityBinding.leaguesRecyclerView.apply {
                        adapter = carouselRecyclerViewAdapter
                        layoutManager = CarouselLayoutManager()
                    }
                }
                else {
                    favoritesActivityBinding.leaguesError.root.visibility = View.VISIBLE
                    favoritesActivityBinding.leaguesError.retryButton.visibility = View.GONE
                    favoritesActivityBinding.leaguesError.errorText.text = this@FavoritesActivity.resources.getString(R.string.no_results)
                    favoritesActivityBinding.leaguesRecyclerView.visibility = View.GONE
                }

                if(teams.isNotEmpty()) {
                    favoritesActivityBinding.teamsError.root.visibility = View.GONE
                    val carouselRecyclerViewAdapter = CarouselRecyclerViewAdapter(teams, null)
                    favoritesActivityBinding.teamsRecyclerView.visibility = View.VISIBLE
                    favoritesActivityBinding.teamsRecyclerView.apply {
                        adapter = carouselRecyclerViewAdapter
                        layoutManager = CarouselLayoutManager()
                    }
                }
                else {
                    favoritesActivityBinding.teamsError.root.visibility = View.VISIBLE
                    favoritesActivityBinding.teamsError.retryButton.visibility = View.GONE
                    favoritesActivityBinding.teamsError.errorText.text =
                        this@FavoritesActivity.resources.getString(R.string.no_results)
                    favoritesActivityBinding.teamsRecyclerView.visibility = View.GONE
                }
            }
        }
    }
}