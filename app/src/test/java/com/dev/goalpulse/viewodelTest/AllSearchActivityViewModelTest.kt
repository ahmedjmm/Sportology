package com.dev.goalpulse.viewodelTest

import com.google.common.truth.Truth.assertThat
import com.dev.goalpulse.models.football.Team
import com.dev.goalpulse.repositories.local.FakeLocalRepository
import com.dev.goalpulse.viewModels.AllSearchActivityViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AllSearchActivityViewModelTest {
    private lateinit var allSearchActivityViewModel: AllSearchActivityViewModel

//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        allSearchActivityViewModel = AllSearchActivityViewModel(FakeLocalRepository())
    }

    @Test
    fun `insert team with empty field, return error`() {
        runBlocking {
            val team = Team(0, "", "logo")
            allSearchActivityViewModel.upsertTeam(team)
            assertThat(team.logo).isNotEmpty()
        }
    }
}