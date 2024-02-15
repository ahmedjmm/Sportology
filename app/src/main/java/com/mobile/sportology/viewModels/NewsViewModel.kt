package com.mobile.sportology.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.models.news.News
import com.mobile.sportology.repositories.RemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


class NewsViewModel @Inject constructor(
    private val application: Application,
    private val remoteRepository: RemoteRepository
): ViewModel() {
    private val _everyThingMutableLiveData = MutableLiveData<ResponseState<News>>()
    private val _topHeadlinesMutableLiveData = MutableLiveData<ResponseState<News>>()
    val everyThingLiveData: LiveData<ResponseState<News>> = _everyThingMutableLiveData
    val topHeadlinesLiveData: LiveData<ResponseState<News>> = _topHeadlinesMutableLiveData
    var language: String = ""
    private val _newsLanguageMutableLiveData = MutableLiveData(language)
    val newsLanguageLiveData: LiveData<String> = _newsLanguageMutableLiveData

    val content = "content"
    val title = "title"
    val description = "description"
    var searchIn = "title,content,description"
    val search_in = "title,content,description"
    var sources = application.resources.getString(R.string.lequipe) + "," +
            application.resources.getString(R.string.bleacher_report) + "," +
            application.resources.getString(R.string.espn) + "," +
            application.resources.getString(R.string.espn_cric_info) + "," +
            application.resources.getString(R.string.fox_sports) + "," +
            application.resources.getString(R.string.nfl_news) + "," +
            application.resources.getString(R.string.nhl_news) + "," +
            application.resources.getString(R.string.bbc_sport) + "," +
            application.resources.getString(R.string.four_four_two) + "," +
            application.resources.getString(R.string.talksport) + "," +
            application.resources.getString(R.string.the_sport_bible) + "," +
            application.resources.getString(R.string.bleacher_report)
    val _sources = application.resources.getString(R.string.lequipe) + "," +
            application.resources.getString(R.string.bleacher_report) + "," +
            application.resources.getString(R.string.espn) + "," +
            application.resources.getString(R.string.espn_cric_info) + "," +
            application.resources.getString(R.string.fox_sports) + "," +
            application.resources.getString(R.string.nfl_news) + "," +
            application.resources.getString(R.string.nhl_news) + "," +
            application.resources.getString(R.string.bbc_sport) + "," +
            application.resources.getString(R.string.four_four_two) + "," +
            application.resources.getString(R.string.talksport) + "," +
            application.resources.getString(R.string.the_sport_bible) + "," +
            application.resources.getString(R.string.bleacher_report)
    var sortBy = "publishedAt"
    val sort_by: String = "publishedAt"
    val _q: String = "football"
    var q: String = "football"


    suspend fun getEveryThingNews() = viewModelScope.launch(Dispatchers.IO) {
        _everyThingMutableLiveData.postValue(ResponseState.Loading())
        try {
            val response = remoteRepository.getEveryThingNews(
                q = q,
                language = language,
                sortBy = sortBy,
                sources = sources,
                searchIn = searchIn,
            )
            if(response?.body()?.articles?.isEmpty()!!)
                _everyThingMutableLiveData.postValue(ResponseState.Error(application.resources.getString(R.string.no_results)))
            else
                _everyThingMutableLiveData.postValue(ResponseState.Success(response.body()!!))
        }
        catch (exception: Exception) {
            handleNewsException(_everyThingMutableLiveData, exception)
        }
    }

    suspend fun getTopHeadlinesNews() = viewModelScope.launch(Dispatchers.IO) {
        _topHeadlinesMutableLiveData.postValue(ResponseState.Loading())
        try {
            val response = remoteRepository.getTopHeadLinesNews(language = language)
            if(response?.body()?.articles?.isEmpty()!!)
                _topHeadlinesMutableLiveData.postValue(ResponseState.Error(application.resources.getString(R.string.no_results)))
            else
                _topHeadlinesMutableLiveData.postValue(ResponseState.Success(response.body()!!))
        }
        catch (exception: Exception) {
            handleNewsException(_topHeadlinesMutableLiveData, exception)
        }
    }

    private fun handleNewsException(
        liveData: MutableLiveData<ResponseState<News>>,
        exception: Exception
    ) {
        val errorMessage = when (exception) {
            is JsonSyntaxException -> application.getString(R.string.limit_reached)
            is SocketTimeoutException -> exception.message!!
            is UnknownHostException -> exception.message!!
            is MalformedURLException -> application.getString(R.string.unknown_error)
            else -> application.getString(R.string.unknown_error)
        }
        Log.e("handleNewsException()", exception.toString())
        liveData.postValue(ResponseState.Error(errorMessage))
    }

    fun removeSearchInExtraStrings() {
        if(searchIn == "") searchIn = "title,content,description"
        else {
            searchIn = searchIn.replace(Regex(",+"), ",")
            searchIn = searchIn.removePrefix(",")
            searchIn = searchIn.removeSuffix(",")
        }
    }

    fun removeSourcesExtraStrings() {
        if(sources == "") sources = application.resources.getStringArray(R.array.news_sources).forEach {
            "$it,"
        }.toString()
        sources = sources.replace(Regex(",+"), ",")
        sources = sources.removePrefix(",")
        sources = sources.removeSuffix(",")
        Log.i("sources", sources)
    }
}