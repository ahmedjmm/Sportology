package com.mobile.sportology.roomsTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.rooms.AppDatabase
import com.mobile.sportology.rooms.LeagueDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@SmallTest
class LeagueDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @Inject
    @Named("db_test")
    lateinit var database: AppDatabase
    private lateinit var leagueDao: LeagueDao

    @Before
    fun setup() {
        hiltRule.inject()
        leagueDao = database.getLeagueDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertLeague() = runBlocking {
        val league = LeagueRoom(id = 1, name = "premier", country = "england", logo = "url")
        leagueDao.upsertLeague(league)
        val leagues = leagueDao.getLeaguesOrderedByName()
        assertThat(leagues).contains(league)
    }

    @Test
    fun getLeague() = runBlocking {
        val league = LeagueRoom(id = 1, name = "premier", country = "england", logo = "url")
        leagueDao.upsertLeague(league)
        val leagueQuery = leagueDao.getLeague(1)
        assertThat(league).isEqualTo(leagueQuery)
    }

    @Test
    fun deleteLeague() = runBlocking {
        val league = LeagueRoom(id = 1, name = "premier", country = "england", logo = "url")
        leagueDao.upsertLeague(league)
        leagueDao.deleteLeague(league)
        val leagueQuery = leagueDao.getLeaguesOrderedByName()
        assertThat(leagueQuery).doesNotContain(league)
    }
}