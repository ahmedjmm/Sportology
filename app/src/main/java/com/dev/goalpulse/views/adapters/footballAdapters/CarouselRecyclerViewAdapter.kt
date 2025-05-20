package com.dev.goalpulse.views.adapters.footballAdapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dev.goalpulse.R
import com.dev.goalpulse.databinding.FavouriteItemBinding
import com.dev.goalpulse.models.football.LeagueRoom
import com.dev.goalpulse.models.football.Team

class CarouselRecyclerViewAdapter(
    private val favourites: List<Any>,
    private val coachesNames: ArrayList<String>?
    ): RecyclerView.Adapter<CarouselRecyclerViewAdapter.CarouselViewHolder>() {
    private lateinit var favouriteItemBinding: FavouriteItemBinding

    inner class CarouselViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {
        fun bindLeague(league: LeagueRoom) {
            Glide.with(itemView.context).asBitmap().load(league.logo)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Palette.from(resource).generate { palette ->
                            palette?.vibrantSwatch?.rgb?.let { rgb ->
                                favouriteItemBinding.constraintLayout.setBackgroundColor(rgb)
                            }
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
            Glide.with(itemView.context).load(league.logo).into(itemView.findViewById(R.id.logo))
            favouriteItemBinding.itemName.text = league.name
//            itemView.item_winner.text = league.country
        }

        fun bindTeam(team: Team, position: Int) {
            Glide.with(itemView.context).asBitmap().load(team.logo)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Palette.from(resource).generate { palette ->
                            palette?.vibrantSwatch?.rgb?.let { rgb ->
                                favouriteItemBinding.constraintLayout.setBackgroundColor(rgb)
                            }
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
            Glide.with(itemView.context).load(team.logo).into(itemView.findViewById(R.id.logo))
            favouriteItemBinding.itemName.text = team.name
            favouriteItemBinding.coachName.text = coachesNames?.get(position) ?: "not provided"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselRecyclerViewAdapter.CarouselViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        favouriteItemBinding = FavouriteItemBinding.inflate(inflater, parent, false)
        return CarouselViewHolder(favouriteItemBinding.root)
    }

    override fun getItemCount() = favourites.size

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        if (favourites[position] is LeagueRoom)
            holder.bindLeague(favourites[position] as LeagueRoom)
        else
            holder.bindTeam(favourites[position] as Team, position)
    }
}