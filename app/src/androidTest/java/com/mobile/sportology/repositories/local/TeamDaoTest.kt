package com.mobile.sportology.repositories.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.mobile.sportology.models.football.TeamRoom
import com.mobile.sportology.rooms.AppDatabase
import com.mobile.sportology.rooms.TeamDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TeamDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: AppDatabase
    private lateinit var teamDao: TeamDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        teamDao = database.getTeamDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertTeam() = runBlocking {
        val team = TeamRoom(id = 1, name = "premier", logo = "url")
        teamDao.upsertTeam(team)
        val teams = teamDao.getTeamsOrderedByName()
        assertThat(teams).contains(team)
    }

    @Test
    fun getTeam() = runBlocking {
        val team = TeamRoom(id = 1, name = "premier", logo = "url")
        teamDao.upsertTeam(team)
        val teamQuery = teamDao.getTeam(1)
        assertThat(team).isEqualTo(teamQuery)
    }

    @Test
    fun deleteTeam() = runBlocking {
        val team = TeamRoom(id = 1, name = "premier", logo = "url")
        teamDao.upsertTeam(team)
        teamDao.deleteTeam(team)
        val teamQuery = teamDao.getTeamsOrderedByName()
        assertThat(teamQuery).doesNotContain(team)
    }
}