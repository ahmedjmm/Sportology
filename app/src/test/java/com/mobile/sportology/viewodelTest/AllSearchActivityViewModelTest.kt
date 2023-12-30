package com.mobile.sportology.viewodelTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mobile.sportology.models.football.TeamRoom
import com.mobile.sportology.repositories.local.FakeLocalRepository
import com.mobile.sportology.viewModels.AllSearchActivityViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AllSearchActivityViewModelTest {
    private lateinit var allSearchActivityViewModel: AllSearchActivityViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        allSearchActivityViewModel = AllSearchActivityViewModel(FakeLocalRepository())
    }

    @Test
    fun `insert team with empty field, return error`() {
        runBlocking {
            allSearchActivityViewModel.upsertTeam(TeamRoom(1, "real", "logo"))
        }
    }
}