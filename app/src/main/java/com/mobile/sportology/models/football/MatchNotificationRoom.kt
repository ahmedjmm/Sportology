package com.mobile.sportology.models.football

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class MatchNotificationRoom(
    @PrimaryKey
    val matchId: Int,
    val home: String,
    val away: String,
    val matchTime: Long
    )
