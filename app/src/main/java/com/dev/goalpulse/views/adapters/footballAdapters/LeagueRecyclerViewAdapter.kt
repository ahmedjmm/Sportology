package com.dev.goalpulse.views.adapters.footballAdapters

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dev.goalpulse.R
import com.dev.goalpulse.databinding.LeagueMatchDateItemBinding
import com.dev.goalpulse.databinding.LeagueMatchResultItemBinding
import com.dev.goalpulse.Shared
import com.dev.goalpulse.models.football.Fixtures

class LeagueRecyclerViewAdapter(
    private val onCheckedChangeListener: OnCheckedChangeListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val _diffCallback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(
            oldItem: Any,
            newItem: Any
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Any,
            newItem: Any
        ): Boolean = oldItem == newItem
    }
    val differ = AsyncListDiffer(this, _diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            R.layout.league_match_date_item -> {
                DateViewHolder(LeagueMatchDateItemBinding.inflate(inflater, parent, false))
            }
            else -> {
                MatchResultViewHolder(LeagueMatchResultItemBinding.inflate(inflater, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is DateViewHolder -> holder.bind(differ.currentList[position] as String)
            is MatchResultViewHolder -> holder.bind(differ.currentList[position] as Fixtures.Response)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return when(differ.currentList[position]) {
            is String -> R.layout.league_match_date_item
            else -> R.layout.league_match_result_item
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    inner class DateViewHolder(
        private val _itemViewBinding: LeagueMatchDateItemBinding,
    ): RecyclerView.ViewHolder(_itemViewBinding.root) {
        fun bind(item: String) {
            _itemViewBinding.date.text = item
        }
    }

    inner class MatchResultViewHolder(
        private val _itemViewBinding: LeagueMatchResultItemBinding,
    ): RecyclerView.ViewHolder(_itemViewBinding.root) {

        init {
            _itemViewBinding.root.apply {
                setOnClickListener {
                    val matchId = (differ.currentList[layoutPosition] as Fixtures.Response).fixture?.id!!
                    val season = (differ.currentList[layoutPosition] as Fixtures.Response).league?.season!!
                    val leagueId = (differ.currentList[layoutPosition] as Fixtures.Response).league?.id!!
                    val bundle = Bundle().apply {
                        putInt("matchId", matchId)
                        putInt("season", season)
                        putInt("leagueId", leagueId)
                    }
                    findNavController().navigate(
                        R.id.action_football_to_matchDetailsActivity,
                        bundle
                    )
                }
            }

            _itemViewBinding.notificationsCheck.setOnCheckedChangeListener { _, isChecked ->
                val fixture = _itemViewBinding.match
                fixture?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = ContextCompat.getSystemService(_itemViewBinding.root.context,
                            AlarmManager::class.java)
                        if (alarmManager?.canScheduleExactAlarms() == false) {
                            Intent().also { intent ->
                                intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                _itemViewBinding.root.context.startActivity(intent)
                            }
                        }
                    }
                    else {
                        val isPermissionGranted = onCheckedChangeListener.checkNotificationPermission(
                            fixture = _itemViewBinding.match!!,
                            isChecked = isChecked
                        )
                        if (isPermissionGranted) {
                            onCheckedChangeListener.onCheckChange(
                                fixture = it,
                                isChecked = isChecked,
                                action = if(isChecked)
                                    _itemViewBinding.root.context.getString(R.string.MATCH_START_NOTIFICATION_SHOW)
                                else
                                    _itemViewBinding.root.context.getString(R.string.MATCH_START_NOTIFICATION_CANCEL)
                            )
                        }
                    }
                }
            }
        }

        fun bind(item: Fixtures.Response) {
            _itemViewBinding.match = item
            _itemViewBinding.notificationsCheck.setOnCheckedChangeListener { _, isChecked ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = ContextCompat.getSystemService(_itemViewBinding.root.context,
                        AlarmManager::class.java)
                    if (alarmManager?.canScheduleExactAlarms() == false) {
                        Intent().also { intent ->
                            intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                            _itemViewBinding.root.context.startActivity(intent)
                        }
                    }
                }
                else {
                    val isPermissionGranted = onCheckedChangeListener.checkNotificationPermission(
                        fixture = _itemViewBinding.match!!,
                        isChecked = isChecked
                    )
                    if (isPermissionGranted) {
                        onCheckedChangeListener.onCheckChange(
                            fixture = item,
                            isChecked = isChecked,
                            action = if (isChecked)
                                _itemViewBinding.root.context.getString(R.string.MATCH_START_NOTIFICATION_SHOW)
                            else
                                _itemViewBinding.root.context.getString(R.string.MATCH_START_NOTIFICATION_CANCEL)
                        )
                    }
                }
            }
            _itemViewBinding.notificationsCheck.visibility =
                if (item.fixture?.status?.short?.equals("NS")!!) View.VISIBLE
                else View.GONE
            _itemViewBinding.notificationsCheck.isChecked =
                Shared.SAVED_MATCHES_NOTIFICATIONS_IDS.contains(item.fixture.id)
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckChange(
            fixture: Fixtures.Response,
            isChecked: Boolean,
            action: String
        )

        fun checkNotificationPermission(
            fixture: Fixtures.Response,
            isChecked: Boolean,
        ): Boolean
    }
}