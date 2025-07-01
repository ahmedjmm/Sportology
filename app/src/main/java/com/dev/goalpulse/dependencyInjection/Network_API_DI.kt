package com.dev.goalpulse.dependencyInjection

import com.dev.goalpulse.BuildConfig
import com.dev.goalpulse.Shared
import com.dev.goalpulse.api.FootballApi
import com.dev.goalpulse.api.NewsApi
import com.dev.goalpulse.servicesAndUtilities.NetworkConnectivityReceiver
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Network_API_DI {

    @Singleton
    @Provides
    fun provideNetworkConnectivityReceiver() = NetworkConnectivityReceiver()

    @Singleton
    @Provides
    fun provideFootballAPI(): FootballApi {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        httpClient.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder().addHeader(
                    "Authorization", BuildConfig.FOOTBALL_API_KEY
                ).build()
                return chain.proceed(request)
            }
        })

        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(Shared.FOOTBALL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
            .create(FootballApi::class.java)
    }

    @Singleton
    @Provides
    fun provideNewsAPI(): NewsApi {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder().addHeader(
                    "X-Api-Key", BuildConfig.NEWS_API_KEY
                ).build()
                return chain.proceed(request)
            }
        })

        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(Shared.NEWS_BASE_URL + "/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
            .create(NewsApi::class.java)
    }
}