package com.mobile.sportology.views.viewsUtilities

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.mobile.sportology.R
import com.mobile.sportology.models.football.Fixtures
import com.mobile.sportology.servicesAndUtilities.DateTimeUtils

@BindingAdapter("match")
fun scoreBinding(textView: TextView, match: Fixtures.Response) {
    if((match.fixture?.status?.short == "1H") || (match.fixture?.status?.short == "2H") ||
        (match.fixture?.status?.short == "HT") || (match.fixture?.status?.short == "ET") ||
        (match.fixture?.status?.short == "BT") || (match.fixture?.status?.short == "P") ||
        (match.fixture?.status?.short == "INT") || (match.fixture?.status?.short == "FT") ||
        (match.fixture?.status?.short == "AET") || (match.fixture?.status?.short == "PEN") ||
        (match.fixture?.status?.short == "LIVE")) {
        val homeScore = match.goals?.home
        val awayScore = match.goals?.away
        val stringBuilder = StringBuilder(match.fixture.status.long!!).apply {
            append("\n")
            append(homeScore)
            append("-")
            append(awayScore)
        }.toString()
        textView.text = stringBuilder
//        textView.text = "${match.fixture.status.long}\n${match.goals?.home}-" +
//                "${match.goals?.away}"
    }
    else if(match.fixture?.status?.short == "SUSP") textView.text = match.fixture.status.long
    else {
        textView.text = if(DateTimeUtils.timeFormat == textView.resources.getString(R.string.time_format_12))
            match.fixture?.date?.let { DateTimeUtils.convertTimeFormatTo12HS(it) }
        else match.fixture?.date?.let { DateTimeUtils.convertTimeFormatTo24HS(it) }
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