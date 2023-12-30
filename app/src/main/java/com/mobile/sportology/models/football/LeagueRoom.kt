package com.mobile.sportology.models.football

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class LeagueRoom(
    @PrimaryKey
    val id: Int,
    val name: String,
    val country: String,
    val logo: String
)
