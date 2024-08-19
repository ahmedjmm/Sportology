package com.dev.goalpulse.models.football

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Team(
    @PrimaryKey
    val id: Int,
    val name: String,
    val logo: String
    )
