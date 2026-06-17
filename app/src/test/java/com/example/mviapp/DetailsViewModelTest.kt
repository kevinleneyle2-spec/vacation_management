package com.vacation.tripinmind

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.data.local.interfaces.VacationDao
import com.example.data.local.model.Activity
import com.example.data.local.model.Day
import com.example.data.local.model.VacationDto
import com.example.data.repository.VacationRepository
import com.vacation.tripinmind.details.viewmodel.DetailsViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var vacationRepository: VacationRepository
    private lateinit var viewModel: DetailsViewModel

    // Données de test
    private val fakeActivity = Activity(
        activityName = "Tour Eiffel",
        activityTime = "10h00",
        activityDuration = "2h",
        activityLocation = "Paris"
    )

    private val fakeVacation = VacationDto(
        id = 1,
        name = "Paris Trip",
        startDate = "2024-05-01",
        nbrDay = 1,
        days = listOf(
            Day(
                nameDay = "Jour 1",
                additionalInfo = "Info",
                activity = listOf(fakeActivity)
            )
        ),
        ideas = listOf("Manger des crêpes"),
        image = "beach_ico",
        isArchived = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock simple du DAO
        val fakeDao = object : VacationDao {
            override fun getItemById(id: Int): Flow<VacationDto> = MutableStateFlow(fakeVacation)
            override fun getAllItems(): Flow<List<VacationDto>> =
                MutableStateFlow(listOf(fakeVacation))

            override suspend fun insertItem(item: VacationDto) {}
            override suspend fun deleteItem(item: VacationDto) {}
            override suspend fun updateItem(item: VacationDto) {}
        }

        vacationRepository = VacationRepository(fakeDao)

        val savedStateHandle = SavedStateHandle(mapOf("vacationId" to 1))

        viewModel = DetailsViewModel(vacationRepository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load vacation by id and maps to UI model correctly`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("vacationId" to 1))

        viewModel = DetailsViewModel(vacationRepository, savedStateHandle)

        viewModel.vacation.test {
            assertThat(awaitItem()).isNull()
            val uiModel = awaitItem()

            assertThat(uiModel).isNotNull()
            assertThat(uiModel?.id).isEqualTo(1)
            assertThat(uiModel?.name).isEqualTo("Paris Trip")
            assertThat(uiModel?.days).hasSize(1)
            assertThat(uiModel?.days?.get(0)?.activities).hasSize(1)
            assertThat(uiModel?.days?.get(0)?.activities?.get(0)?.name).isEqualTo("Tour Eiffel")
            assertThat(uiModel?.ideas).contains("Manger des crêpes")
        }
    }

    @Test
    fun `when id is missing returns null state`() = runTest {
        val emptyViewModel = DetailsViewModel(
            vacationRepository,
            SavedStateHandle()
        )

        advanceUntilIdle()

        assertThat(emptyViewModel.vacation.value).isNull()
    }
}