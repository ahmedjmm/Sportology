package com.mobile.sportology.roomsTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.mobile.sportology.models.football.TeamRoom
import com.mobile.sportology.rooms.AppDatabase
import com.mobile.sportology.rooms.TeamDao
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
class TeamDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @Inject
    @Named("db_test")
    lateinit var database: AppDatabase
    private lateinit var teamDao: TeamDao

    @Before
    fun setup() {
        hiltRule.inject()
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