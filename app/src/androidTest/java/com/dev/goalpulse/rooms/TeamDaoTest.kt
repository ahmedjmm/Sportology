package com.dev.goalpulse.rooms

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.dev.goalpulse.models.football.Team
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
    fun closeDb() = database.close()

    @Test
    fun insertTeam() = runBlocking {
        val team = Team(id = 1, name = "premier", logo = "url")
        teamDao.upsertTeam(team)
        val teams = teamDao.getTeamsOrderedByName()
        assertThat(teams).contains(team)
    }

    @Test
    fun getTeam() = runBlocking {
        val team = Team(id = 1, name = "premier", logo = "url")
        teamDao.upsertTeam(team)
        val teamQuery = teamDao.getTeam(1)
        assertThat(team).isEqualTo(teamQuery)
    }

    @Test
    fun deleteTeam() = runBlocking {
        val team = Team(id = 1, name = "premier", logo = "url")
        teamDao.upsertTeam(team)
        teamDao.deleteTeam(team)
        val teamQuery = teamDao.getTeamsOrderedByName()
        assertThat(teamQuery).doesNotContain(team)
    }
}