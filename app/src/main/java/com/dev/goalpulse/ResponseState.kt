package com.dev.goalpulse

import android.util.Log
import com.dev.goalpulse.models.football.ApiError
import com.google.gson.Gson
import retrofit2.Response

sealed class ResponseState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T, message: String? = null): ResponseState<T>(data, message)
    class Error<T>(message: String, data: T? = null): ResponseState<T>(data, message)
    class Loading<T>: ResponseState<T>()
}

object ApiResponseHandler {
    suspend fun <T> handleResponse(
        call: suspend () -> Response<T>
    ): ResponseState<T> {
        return try {
            val response = call()

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    ResponseState.Success(body)
                } ?: ResponseState.Error("Empty response body")
            } else {
                // Get the raw error response as string first
                val errorBodyString = response.errorBody()?.string() ?: ""
                Log.d("API_ERROR", "Raw error response: $errorBodyString")

                // Try to parse as ApiError
                val apiError = try {
                    Gson().fromJson(errorBodyString, ApiError::class.java)
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Failed to parse error response", e)
                    null
                }

                // Use the parsed error message or fallback
                val errorMessage = apiError?.message ?: response.message().ifEmpty {
                    "Request failed with status code ${response.code()}"
                }

                ResponseState.Error(errorMessage)
            }
        } catch (e: Exception) {
            ResponseState.Error(e.message ?: "Unknown error")
        }
    }
}