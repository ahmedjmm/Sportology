package com.mobile.sportology.models.football

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class TeamRoom(
    @PrimaryKey
    val id: Int,
    val name: String,
    val logo: String
    )
