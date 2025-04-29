package com.dev.goalpulse.views.viewsUtilities

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.dev.goalpulse.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.dev.goalpulse.models.football.Matches

@BindingAdapter("match")
fun scoreBinding(textView: TextView, match: Matches.MatchesItem) {
    val homeScore = match.homeTeamScore?.display
    val awayScore = match.awayTeamScore?.display
    val stringBuilder = StringBuilder().apply {
        append(homeScore)
        append(" - ")
        append(awayScore)
    }.toString()
    when (match.statusType) {
        "upcoming" ->
            textView.text = if(DateTimeUtils.timeFormat == "12 HS")
                match.startTime?.let { DateTimeUtils.convertTimeFormatTo12HS(it) }
            else match.startTime?.let { DateTimeUtils.convertTimeFormatTo24HS(it) }

        "finished" -> textView.apply {
            text = stringBuilder
        }

        "live" -> textView.apply {
            text = stringBuilder
            background = ResourcesCompat.getDrawable(resources, R.drawable.live_score_background, null)
        }

        "postponed" -> textView.apply {
            text = match.status?.reason
        }

        "canceled" -> textView.apply {
            text = match.status?.reason
        }

        "delayed" -> textView.apply {
            text = match.status?.reason
        }

        "endure" -> textView.apply {
            text = match.status?.reason
        }

        "interrupted" -> textView.apply {
            text = match.status?.reason
        }

        "suspended" -> textView.apply {
            text = match.status?.reason
        }
    }
}

@BindingAdapter("card")
fun TextView.card(card: String?) {
    card?.let {
        this.text = it
    }?: run {
        this.text = "0"
    }
}

// league_match_result_item.xml
@BindingAdapter("url")
fun ImageView.imageBinding(hashImage: String?) {
    val imageUrl = "https://images.sportdevs.com/${hashImage}.png"

    Glide.with(this.context).load(imageUrl).circleCrop().placeholder(R.drawable.ic_football)
        .into(this)
}

// article_item.xml
@BindingAdapter("url2")
fun ImageView.squareImageBinding(imageUrl: String?) =
    Glide.with(this.context).load(imageUrl).placeholder(R.drawable.ic_football)
        .into(this)

@BindingAdapter("possession")
fun CircularProgressIndicator.possessionBinding(progress: String) {
    var myProgress: String? = null
    if(progress.contains("%")) myProgress = progress.replace("%", "")
    this.progress = myProgress?.let { Integer.valueOf(it) }?: 0
}