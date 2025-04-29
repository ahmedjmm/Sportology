package com.dev.goalpulse.models.football

import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String
)
