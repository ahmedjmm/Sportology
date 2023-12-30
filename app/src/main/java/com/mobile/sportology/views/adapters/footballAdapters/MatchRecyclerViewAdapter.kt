package com.mobile.sportology.views.adapters.footballAdapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sportology.R
import com.mobile.sportology.Shared
import com.mobile.sportology.databinding.LeagueMatchResultItemBinding
import com.mobile.sportology.models.football.Fixtures

class MatchRecyclerViewAdapter(val context: Context) :
    RecyclerView.Adapter<MatchRecyclerViewAdapter.MatchViewHolder>() {
    lateinit var onCheckedChangeListener: OnCheckedChangeListener

    private val _diffCallback = object : DiffUtil.ItemCallback<Fixtures.Response>() {
        override fun areItemsTheSame(
            oldItem: Fixtures.Response,
            newItem: Fixtures.Response
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Fixtures.Response,
            newItem: Fixtures.Response
        ): Boolean = oldItem == newItem
    }
    val differ = AsyncListDiffer(this, _diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        return MatchViewHolder(LeagueMatchResultItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class MatchViewHolder(
        private val _itemViewBinding: LeagueMatchResultItemBinding,
    ): RecyclerView.ViewHolder(_itemViewBinding.root) {

        init {
            _itemViewBinding.root.apply {
                setOnClickListener {
                    val bundle = Bundle().apply {
                        putInt("matchId", differ.currentList[layoutPosition].fixture?.id!!)
                        putInt("season", differ.currentList[layoutPosition].league?.season!!)
                        putInt("leagueId", differ.currentList[layoutPosition].league?.id!!)
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
                    val isPermissionGranted = onCheckedChangeListener.checkNotificationPermission(
                        fixture = _itemViewBinding.match!!,
                        isChecked = isChecked
                    )
                    if (isPermissionGranted) {
                        onCheckedChangeListener.onCheckChange(
                            fixture = it,
                            isChecked = isChecked,
                            action = if(isChecked)
                                context.getString(R.string.MATCH_START_NOTIFICATION_SHOW)
                            else
                                context.getString(R.string.MATCH_START_NOTIFICATION_CANCEL)
                        )
                    }
                }
            }
        }

        fun bind(item: Fixtures.Response) {
            _itemViewBinding.match = item
            _itemViewBinding.notificationsCheck.setOnCheckedChangeListener { _, isChecked ->
                val isPermissionGranted = onCheckedChangeListener.checkNotificationPermission(
                    fixture = _itemViewBinding.match!!,
                    isChecked = isChecked
                )
                if (isPermissionGranted) {
                    onCheckedChangeListener.onCheckChange(
                        fixture = item,
                        isChecked = isChecked,
                        action = if (isChecked)
                            context.getString(R.string.MATCH_START_NOTIFICATION_SHOW)
                        else
                            context.getString(R.string.MATCH_START_NOTIFICATION_CANCEL)
                    )
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