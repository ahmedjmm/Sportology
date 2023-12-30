package com.mobile.sportology.views.activities

import android.app.SearchManager
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.Shared
import com.mobile.sportology.databinding.ActivitySearchableBinding
import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.models.football.LeagueSearchResult
import com.mobile.sportology.models.football.TeamRoom
import com.mobile.sportology.models.football.TeamSearchResult
import com.mobile.sportology.repositories.DefaultLocalRepository
import com.mobile.sportology.repositories.RemoteRepository
import com.mobile.sportology.servicesAndUtilities.NetworkConnectivityReceiver
import com.mobile.sportology.viewModels.MyViewModelProvider
import com.mobile.sportology.viewModels.SearchActivityViewModel
import com.mobile.sportology.views.viewsUtilities.imageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.error_layout.view.retry_button
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject


@AndroidEntryPoint
class SearchableActivity : AppCompatActivity(), NetworkConnectivityReceiver.NetworkStateListener {
    @Inject
    lateinit var intentFilter: IntentFilter
    @Inject
    lateinit var remoteRepository: RemoteRepository
    @Inject
    lateinit var networkConnectivityReceiver: NetworkConnectivityReceiver
    @Inject
    lateinit var defaultLocalRepository: DefaultLocalRepository
    private lateinit var leagueRoomList: List<LeagueRoom>
    private lateinit var teamRoomList: List<TeamRoom>
    var myQuery = ""
    private lateinit var snackBar: Snackbar
    lateinit var binding: ActivitySearchableBinding
    private val viewModel by lazy {
        ViewModelProvider(
            this, MyViewModelProvider(application, remoteRepository = remoteRepository,
                defaultLocalRepository = null))[SearchActivityViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_searchable)

        snackBar = Snackbar.make(binding.constraintLayout,
            this.resources.getString(R.string.unable_to_connect),
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(this.resources.getString(R.string.dismiss)) {
            snackBar.dismiss()
        }

        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                myQuery = query.trim()
                binding.toolbar.title = "${this.resources.getString(R.string.search)} $myQuery"
                lifecycleScope.launch(Dispatchers.IO) {
                    doMySearch(myQuery)
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            leagueRoomList = defaultLocalRepository.getLeagues()
            teamRoomList = defaultLocalRepository.getTeams()
        }

        viewModel.leagueSearchLiveData.observe(this) { responseState ->
            when(responseState) {
                is ResponseState.Loading -> {
                    binding.leaguesProgressCircular.visibility = View.VISIBLE
                    binding.leaguesError.root.visibility = View.GONE
                }
                is ResponseState.Success -> {
                    responseState.data?.response?.let {
                        buildLeaguesViews(it)
                    }
                    binding.leaguesLayout.visibility = View.VISIBLE
                    binding.leaguesProgressCircular.visibility = View.GONE
                    binding.leaguesError.root.visibility = View.GONE
                }
                is ResponseState.Error -> {
                    binding.leaguesProgressCircular.visibility = View.GONE
                    binding.leaguesLayout.visibility = View.VISIBLE
                    binding.leaguesError.root.visibility = View.VISIBLE
                    binding.leaguesError.root.retry_button.setOnClickListener {
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.searchLeague(myQuery)
                        }
                    }
                    binding.leaguesError.errorText.text = responseState.message
                }
            }
        }

        viewModel.teamSearchLiveData.observe(this) { responseState ->
            when(responseState) {
                is ResponseState.Loading -> {
                    binding.teamsProgressCircular.visibility = View.VISIBLE
                    binding.teamsError.root.visibility = View.GONE
                }
                is ResponseState.Success -> {
                    responseState.data?.response?.let {
                        buildTeamsViews(it)
                    }
                    binding.teamsLayout.visibility = View.VISIBLE
                    binding.teamsProgressCircular.visibility = View.GONE
                    binding.teamsError.root.visibility = View.GONE
                }
                is ResponseState.Error -> {
                    binding.teamsProgressCircular.visibility = View.GONE
                    binding.teamsLayout.visibility = View.VISIBLE
                    binding.teamsError.root.visibility = View.VISIBLE
                    binding.teamsError.root.retry_button.setOnClickListener {
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.searchTeam(myQuery)
                        }
                    }
                    binding.teamsError.errorText.text = responseState.message
                }
            }
        }
    }

    private fun buildLeaguesViews(leagues: List<LeagueSearchResult.Response?>) {
        if(leagues.size > 5) {
            val leagueSearchItems = LinearLayout(this).apply {
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                val params = layoutParams as? RelativeLayout.LayoutParams
                params?.addRule(RelativeLayout.BELOW, binding.leaguesTitle.id)
                orientation = LinearLayout.VERTICAL
            }

            for(index in 0 .. 4) {
                val itemLayout = LinearLayout(this) .apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        45.dpToPx()
                    )
                    val params = this.layoutParams as LinearLayout.LayoutParams
                    params.setMargins(0, 5.dpToPx(), 0, 0)
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    setBackgroundColor(ContextCompat.getColor(this.context, R.color.WhiteSmoke))
                }

                val logo = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(25.dpToPx(), 25.dpToPx())
                    imageBinding(leagues[index]?.league?.logo)
                }
                itemLayout.addView(logo)

                val nameAndCountryLayout = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1f
                    )
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    val params = this.layoutParams as LinearLayout.LayoutParams
                    params.setMargins(5.dpToPx(), 0, 0, 0)
                }

                val nameTextView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setTypeface(null, Typeface.BOLD)
                    text = leagues[index]?.league?.name
                }

                val countryTextView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = leagues[index]?.country?.name
                }
                nameAndCountryLayout.addView(nameTextView)
                nameAndCountryLayout.addView(countryTextView)
                itemLayout.addView(nameAndCountryLayout)

                val followCheckBox = CheckBox(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    val drawable: Drawable =
                        context.resources.getDrawable(R.drawable.follow_button_selector)
                    buttonDrawable = drawable
                    isChecked = isItemChecked(leagues[index]?.league!!)
                    setOnCheckedChangeListener { _, isChecked ->
                        if(isChecked){
                            val league = LeagueRoom(
                                leagues[index]?.league?.id!!,
                                leagues[index]?.league?.name!!,
                                leagues[index]?.country?.name!!,
                                leagues[index]?.league?.logo!!
                            )
                            lifecycleScope.launch(Dispatchers.IO) {
                                defaultLocalRepository.upsertLeague(league)
                            }
                        }
                        else {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val league = LeagueRoom(
                                    leagues[index]?.league?.id!!,
                                    leagues[index]?.league?.name!!,
                                    leagues[index]?.country?.name!!,
                                    leagues[index]?.league?.logo!!
                                )
                                defaultLocalRepository.deleteLeague(league)
                            }
                        }
                    }
                }
                itemLayout.addView(followCheckBox)
                leagueSearchItems.addView(itemLayout)
            }
            val seeAllTextView = TextView(this).apply {
                text = getString(R.string.see_all)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val params = this.layoutParams as LinearLayout.LayoutParams
                params.setMargins(0, 5.dpToPx(), 0, 0)
                setBackgroundColor(ContextCompat.getColor(this.context, R.color.WhiteSmoke))
                setOnClickListener {
                    if(Shared.isConnected){
                        val intent = Intent(this@SearchableActivity, AllSearchResultsActivity::class.java)
                        intent.putExtra("queryType", resources.getString(R.string.competitions))
                        intent.putExtra("queryValue", myQuery)
                        intent.putExtra("queryResult", leagues as Serializable)
                        startActivity(intent)
                    }
                    else Toast.makeText(
                        this@SearchableActivity,
                        R.string.unable_to_connect,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            leagueSearchItems.addView(seeAllTextView)
            binding.leaguesLayout.addView(leagueSearchItems)
        }
        else {
            val leagueSearchItems = LinearLayout(this).apply {
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                val params = layoutParams as? RelativeLayout.LayoutParams
                params?.addRule(RelativeLayout.BELOW, binding.leaguesTitle.id)
                orientation = LinearLayout.VERTICAL
            }

            for(index in leagues.indices) {
                val itemLayout = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60.dpToPx()
                    )
                    val params = this.layoutParams as LinearLayout.LayoutParams
                    params.setMargins(0, 5.dpToPx(), 0, 0)
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    setBackgroundColor(ContextCompat.getColor(this.context, R.color.WhiteSmoke))
                }

                val logo = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(25.dpToPx(), 25.dpToPx())
                    imageBinding(leagues[index]?.league?.logo)
                }
                itemLayout.addView(logo)

                val nameAndCountryLayout = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1f
                    )
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    val params = this.layoutParams as LinearLayout.LayoutParams
                    params.setMargins(5.dpToPx(), 0, 0, 0)
                }

                val nameTextView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setTypeface(null, Typeface.BOLD)
                    text = leagues[index]?.league?.name
                }

                val countryTextView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = leagues[index]?.country?.name
                }
                nameAndCountryLayout.addView(nameTextView)
                nameAndCountryLayout.addView(countryTextView)
                itemLayout.addView(nameAndCountryLayout)

                val followCheckBox = CheckBox(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    val button: Drawable =
                        context.resources.getDrawable(R.drawable.follow_button_selector)
                    buttonDrawable = button
                    isChecked = isItemChecked(leagues[index]?.league!!)
                    setOnCheckedChangeListener { _, isChecked ->
                        if(isChecked){
                            val league = LeagueRoom(
                                leagues[index]?.league?.id!!,
                                leagues[index]?.league?.name!!,
                                leagues[index]?.country?.name!!,
                                leagues[index]?.league?.logo!!
                            )
                            lifecycleScope.launch(Dispatchers.IO) {
                                defaultLocalRepository.upsertLeague(league)
                            }
                        }
                        else {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val league = LeagueRoom(
                                    leagues[index]?.league?.id!!,
                                    leagues[index]?.league?.name!!,
                                    leagues[index]?.country?.name!!,
                                    leagues[index]?.league?.logo!!
                                )
                                defaultLocalRepository.deleteLeague(league)
                            }
                        }
                    }
                }
                itemLayout.addView(followCheckBox)
                leagueSearchItems.addView(itemLayout)
            }
            binding.leaguesLayout.addView(leagueSearchItems)
        }
    }

    private fun isItemChecked(item: Any): Boolean {
        when(item) {
            is LeagueSearchResult.Response.League -> {
                leagueRoomList.forEach {
                    if(item.id == it.id)
                        return true
                }
            }
            is TeamSearchResult.Response.Team -> {
                teamRoomList.forEach {
                    if(item.id == it.id)
                        return true
                }
            }
        }
        return false
    }

    private fun buildTeamsViews(teams: List<TeamSearchResult.Response?>) {
        if(teams.size > 5) {
            val teamSearchItems = LinearLayout(this).apply {
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                val params = layoutParams as? RelativeLayout.LayoutParams
                params?.addRule(RelativeLayout.BELOW, binding.teamsTitle.id)
                orientation = LinearLayout.VERTICAL
            }

            for(index in 0 ..4) {
                val itemLayout = LinearLayout(this) .apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        45.dpToPx()
                    )
                    val params = this.layoutParams as LinearLayout.LayoutParams
                    params.setMargins(0, 5.dpToPx(), 0, 0)
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    setBackgroundColor(ContextCompat.getColor(this.context, R.color.WhiteSmoke))
                }

                val logo = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(25.dpToPx(), 25.dpToPx())
                    imageBinding(teams[index]?.team?.logo)
                }
                itemLayout.addView(logo)

                val nameTextView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1f
                    )
                    val params = layoutParams as LinearLayout.LayoutParams
                    params.setMargins(5.dpToPx(), 0, 0, 0)
                    gravity = Gravity.CENTER_VERTICAL
                    setTypeface(null, Typeface.BOLD)
                    text = teams[index]?.team?.name
                }
                itemLayout.addView(nameTextView)

                val followCheckBox = CheckBox(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    isChecked = isItemChecked(teams[index]?.team!!)
                    setOnCheckedChangeListener { _, isChecked ->
                        if(isChecked){
                            val team = TeamRoom(
                                teams[index]?.team?.id!!,
                                teams[index]?.team?.name!!,
                                teams[index]?.team?.logo!!
                            )
                            lifecycleScope.launch(Dispatchers.IO) {
                                defaultLocalRepository.upsertTeam(team)
                            }
                        }
                        else {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val team = TeamRoom(
                                    teams[index]?.team?.id!!,
                                    teams[index]?.team?.name!!,
                                    teams[index]?.team?.logo!!
                                )
                                defaultLocalRepository.deleteTeam(team)
                            }
                        }
                    }
                    val button: Drawable =
                        context.resources.getDrawable(R.drawable.follow_button_selector)
                    buttonDrawable = button
                }
                itemLayout.addView(followCheckBox)
                teamSearchItems.addView(itemLayout)
            }
            val seeAllTextView = TextView(this).apply {
                text = getString(R.string.see_all)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val params = this.layoutParams as LinearLayout.LayoutParams
                params.setMargins(0, 5.dpToPx(), 0, 0)
                setBackgroundColor(ContextCompat.getColor(this.context, R.color.WhiteSmoke))
                setOnClickListener {
                    if(Shared.isConnected){
                        val intent = Intent(this@SearchableActivity, AllSearchResultsActivity::class.java)
                        intent.putExtra("queryType", resources.getString(R.string.teams))
                        intent.putExtra("queryValue", myQuery)
                        intent.putExtra("queryResult", teams as Serializable)
                        startActivity(intent)
                    }
                    else Toast.makeText(
                        this@SearchableActivity,
                        R.string.unable_to_connect,
                        Toast.LENGTH_LONG).show()
                }
            }
            teamSearchItems.addView(seeAllTextView)
            binding.teamsLayout.addView(teamSearchItems)
        }
        else {
            val teamSearchItems = LinearLayout(this).apply {
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                val params = layoutParams as? RelativeLayout.LayoutParams
                params?.addRule(RelativeLayout.BELOW, binding.teamsTitle.id)
                orientation = LinearLayout.VERTICAL
            }

            for(index in teams.indices) {
                val itemLayout = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60.dpToPx()
                    )
                    val params = this.layoutParams as LinearLayout.LayoutParams
                    params.setMargins(0, 5.dpToPx(), 0, 0)
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    setBackgroundColor(ContextCompat.getColor(this.context, R.color.WhiteSmoke))
                }

                val logo = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(25.dpToPx(), 25.dpToPx())
                    imageBinding(teams[index]?.team?.logo)
                }
                itemLayout.addView(logo)

                val nameTextView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1f
                    )
                    val params = this.layoutParams as LinearLayout.LayoutParams
                    params.setMargins(5.dpToPx(), 0, 0, 0)
                    setTypeface(null, Typeface.BOLD)
                    text = teams[index]?.team?.name
                    gravity = Gravity.CENTER_VERTICAL
                }
                itemLayout.addView(nameTextView)

                val followCheck = CheckBox(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    isChecked = isItemChecked(teams[index]?.team!!)
                    setOnCheckedChangeListener { _, isChecked ->
                        if(isChecked) {
                            val team = TeamRoom(
                                teams[index]?.team?.id!!,
                                teams[index]?.team?.name!!,
                                teams[index]?.team?.logo!!
                            )
                            lifecycleScope.launch(Dispatchers.IO) {
                                defaultLocalRepository.upsertTeam(team)
                            }
                        }
                        else {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val team = TeamRoom(
                                    teams[index]?.team?.id!!,
                                    teams[index]?.team?.name!!,
                                    teams[index]?.team?.logo!!
                                )
                                defaultLocalRepository.deleteTeam(team)
                            }
                        }
                    }
                    val button: Drawable =
                        context.resources.getDrawable(R.drawable.follow_button_selector)
                    buttonDrawable = button
                }
                itemLayout.addView(followCheck)
                teamSearchItems.addView(itemLayout)
            }
            binding.teamsLayout.addView(teamSearchItems)
        }
    }

    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    private suspend fun doMySearch(query: String) {
        viewModel.searchLeague(query)
        viewModel.searchTeam(query)
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