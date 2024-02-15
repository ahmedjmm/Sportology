package com.mobile.sportology.views.viewsUtilities

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.mobile.sportology.R
import com.mobile.sportology.models.football.Fixtures
import com.mobile.sportology.servicesAndUtilities.DateTimeUtils

@BindingAdapter("match")
fun scoreBinding(textView: TextView, match: Fixtures.Response) {
    val homeScore = match.goals?.home
    val awayScore = match.goals?.away
    val stringBuilder = StringBuilder().apply {
        append(homeScore)
        append(" - ")
        append(awayScore)
    }.toString()
    // if match is live
    when (match.fixture?.status?.short) {
        "1H", "2H", "HT", "ET", "BT", "P", "INT" -> {
            textView.apply {
                text = stringBuilder
                background = ResourcesCompat.getDrawable(resources, R.drawable.live_score_background, null)
            }
//        textView.text = "${match.fixture.status.long}\n${match.goals?.home}-" +
//                "${match.goals?.away}"
        }
        // if match is finished
        "FT", "AET", "PEN" -> {
            textView.apply {
                text = stringBuilder
            }
        }
        // if match is canceled for any reason
        "CANC", "PST", "TBD" -> {
            textView.text = match.fixture.status.long
        }
        // if match is scheduled
        else -> {
            textView.text = if(DateTimeUtils.timeFormat == "12 HS")
                match.fixture?.date?.let { DateTimeUtils.convertTimeFormatTo12HS(it) }
            else match.fixture?.date?.let { DateTimeUtils.convertTimeFormatTo24HS(it) }
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
fun ImageView.imageBinding(imageUrl: String?) =
    Glide.with(this.context).load(imageUrl).circleCrop().placeholder(R.drawable.ic_football)
        .into(this)

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