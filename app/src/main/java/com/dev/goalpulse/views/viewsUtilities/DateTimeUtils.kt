package com.dev.goalpulse.views.viewsUtilities

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    var timeFormat = "12 HS"

    fun getTimeInMilliSeconds(dateTime: String): Long {
        val dateTimeObject = OffsetDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val timestamp = dateTimeObject.toInstant().epochSecond
        return timestamp * 1000
    }

    fun formatDateTime(dateTime: String): String {
        val date = dateTime.substring(0, 10)
        val time = if(timeFormat == "12 HS")
            convertTimeFormatTo12HS(dateTime)
        else
            convertTimeFormatTo24HS(dateTime)
        return "$date \n $time"
    }

    fun convertTimeFormatTo24HS(inputTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val twentyFourHourFormat = "HH:mm"
        val outputFormatter = SimpleDateFormat(twentyFourHourFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = inputFormat.parse(inputTime)
        return outputFormatter.format(calendar.time)
    }

    fun convertTimeFormatTo12HS(inputTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val twelveHourFormat = "hh:mm a"
        val outputFormatter = SimpleDateFormat(twelveHourFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = inputFormat.parse(inputTime)
        return outputFormatter.format(calendar.time)
    }
}