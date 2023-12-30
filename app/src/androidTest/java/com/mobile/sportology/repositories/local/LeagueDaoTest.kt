package com.mobile.sportology.repositories.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.mobile.sportology.models.football.LeagueRoom
import com.mobile.sportology.rooms.AppDatabase
import com.mobile.sportology.rooms.LeagueDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class LeagueDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: AppDatabase
    private lateinit var leagueDao: LeagueDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
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