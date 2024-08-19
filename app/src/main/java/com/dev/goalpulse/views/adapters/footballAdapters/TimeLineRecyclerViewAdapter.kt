package com.dev.goalpulse.views.adapters.footballAdapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dev.goalpulse.databinding.TimeLineItemBinding
import com.dev.goalpulse.models.football.FixtureById

class TimeLineRecyclerViewAdapter: RecyclerView.Adapter<TimeLineRecyclerViewAdapter.ViewHolder>() {
    private val _diffCallback = object : DiffUtil.ItemCallback<FixtureById.Response.Event>() {
        override fun areItemsTheSame(
            oldItem: FixtureById.Response.Event,
            newItem: FixtureById.Response.Event
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: FixtureById.Response.Event,
            newItem: FixtureById.Response.Event
        ): Boolean = oldItem == newItem
    }
    val differ = AsyncListDiffer(this, _diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            TimeLineItemBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(
        private val _timeLineItemBinding: TimeLineItemBinding
    ): RecyclerView.ViewHolder(
        _timeLineItemBinding.root
    ){
        fun bind(item: FixtureById.Response.Event){
            Log.i("timeLineBinder", item.toString())
            if(item.type.equals("subst"))
                _timeLineItemBinding.comment.text = item.player?.name + "\n" + item.assist?.name
            else _timeLineItemBinding.comment.text = item.player?.name
        }
    }
}