package com.dev.goalpulse.views.adapters.footballAdapters

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dev.goalpulse.R
import com.dev.goalpulse.Shared
import com.dev.goalpulse.databinding.LeagueMatchDateItemBinding
import com.dev.goalpulse.databinding.LeagueMatchResultItemBinding
import com.dev.goalpulse.models.football.Matches

class LeagueMatchesRecyclerViewAdapter(
    private val onCheckedChangeListener: OnCheckedChangeListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE = R.layout.league_match_date_item
        private const val TYPE_MATCH = R.layout.league_match_result_item
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is String && newItem is String -> oldItem == newItem
                oldItem is Matches.MatchesItem && newItem is Matches.MatchesItem -> oldItem.id == newItem.id
                else -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is String && newItem is String -> oldItem == newItem
                oldItem is Matches.MatchesItem && newItem is Matches.MatchesItem -> oldItem == newItem
                else -> false
            }
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_DATE -> DateViewHolder(LeagueMatchDateItemBinding.inflate(inflater, parent, false))
            TYPE_MATCH -> MatchResultViewHolder(
                LeagueMatchResultItemBinding.inflate(inflater, parent, false),
                onCheckedChangeListener
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateViewHolder -> holder.bind(differ.currentList[position] as String)
            is MatchResultViewHolder -> holder.bind(differ.currentList[position] as Matches.MatchesItem)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is String -> TYPE_DATE
            is Matches.MatchesItem -> TYPE_MATCH
            else -> throw IllegalArgumentException("Unknown item type at position $position")
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ViewHolders
    ///////////////////////////////////////////////////////////////////////////

    class DateViewHolder(
        private val binding: LeagueMatchDateItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(date: String) {
            binding.date.text = date
        }
    }

    class MatchResultViewHolder(
        private val binding: LeagueMatchResultItemBinding,
        private val listener: OnCheckedChangeListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                binding.match?.let { matchItem ->
                    navigateToMatchDetails(matchItem)
                }
            }

            binding.notificationsCheck.setOnCheckedChangeListener { _, isChecked ->
                handleNotificationToggle(isChecked)
            }
        }

        fun bind(match: Matches.MatchesItem) {
            binding.match = match
            setupNotificationToggle(match)
        }

        private fun setupNotificationToggle(match: Matches.MatchesItem) {
            binding.notificationsCheck.apply {
                visibility = if (match.status?.type == "upcoming") View.VISIBLE else View.GONE
                isChecked = Shared.SAVED_MATCHES_NOTIFICATIONS_IDS.contains(match.id)
                jumpDrawablesToCurrentState() // Prevent animation on rebind
            }
        }

        private fun navigateToMatchDetails(matchItem: Matches.MatchesItem) {
            val bundle = Bundle().apply { putParcelable("match", matchItem) }
            itemView.findNavController().navigate(
                R.id.action_football_to_matchDetailsActivity,
                bundle
            )
        }

        private fun handleNotificationToggle(isChecked: Boolean) {
            val match = binding.match ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                checkExactAlarmPermission(match, isChecked)
            else
                proceedWithNotificationChange(match, isChecked)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        private fun checkExactAlarmPermission(match: Matches.MatchesItem, isChecked: Boolean) {
            val alarmManager = ContextCompat.getSystemService(
                itemView.context,
                AlarmManager::class.java
            )

            if (alarmManager?.canScheduleExactAlarms() == false) {
                itemView.context.startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                )
            } else {
                proceedWithNotificationChange(match, isChecked)
            }
        }

        private fun proceedWithNotificationChange(match: Matches.MatchesItem, isChecked: Boolean) {
            if (listener.checkNotificationPermission(match, isChecked)) {
                val action = if (isChecked) {
                    itemView.context.getString(R.string.MATCH_START_NOTIFICATION_SHOW)
                } else {
                    itemView.context.getString(R.string.MATCH_START_NOTIFICATION_CANCEL)
                }
                listener.onCheckChange(match, isChecked, action)
            }
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckChange(fixture: Matches.MatchesItem, isChecked: Boolean, action: String)
        fun checkNotificationPermission(fixture: Matches.MatchesItem, isChecked: Boolean): Boolean
    }
}