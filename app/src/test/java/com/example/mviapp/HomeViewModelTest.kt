package com.example.mviapp

import com.google.common.truth.Truth.assertThat
import com.example.data.local.interfaces.VacationDao
import com.example.data.local.model.VacationDto
import com.example.data.repository.VacationRepository
import com.example.mviapp.home.intent.VacationIntent
import com.example.mviapp.home.viewmodel.HomeViewModel
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
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val vacationsFlow = MutableStateFlow<List<VacationDto>>(emptyList())

    private lateinit var fakeVacationDao: VacationDao
    private lateinit var vacationRepository: VacationRepository

    private lateinit var viewModel: HomeViewModel

    private val fakeVacation = VacationDto(
        id = 1,
        name = "Paris",
        startDate = "2023-06-01",
        nbrDay = 5,
        days = emptyList(),
        ideas = emptyList(),
        image = "",
        isArchived = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vacationsFlow.value = listOf(fakeVacation)

        fakeVacationDao = object : VacationDao {
            override fun getItemById(id: Int): Flow<VacationDto> =
                MutableStateFlow(fakeVacation)

            override fun getAllItems(): Flow<List<VacationDto>> = vacationsFlow

            override suspend fun insertItem(item: VacationDto) {}

            override suspend fun deleteItem(item: VacationDto) {
                vacationsFlow.value = vacationsFlow.value.filter { it.id != item.id }
            }

            override suspend fun updateItem(item: VacationDto) {
                vacationsFlow.value = vacationsFlow.value.map {
                    if (it.id == item.id) item else it
                }
            }
        }

        vacationRepository = VacationRepository(fakeVacationDao)
        viewModel = HomeViewModel(vacationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads vacation and emits non-empty list`() = runTest(testDispatcher) {
        viewModel.handleIntent(VacationIntent.LoadData)

        advanceUntilIdle()

        val state = viewModel.vacationState.value

        assertThat(state.isLoading).isFalse()
        assertThat(state.vacations).isNotEmpty()
        assertThat(state.vacations[0].name).isEqualTo("Paris")
    }

    @Test
    fun `delete vacation and emits empty list`() = runTest(testDispatcher) {
        viewModel.handleIntent(VacationIntent.LoadData)
        advanceUntilIdle()

        assertThat(viewModel.vacationState.value.vacations).hasSize(1)

        viewModel.handleIntent(VacationIntent.DeleteVacation(fakeVacation))
        advanceUntilIdle()

        val state = viewModel.vacationState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.vacations).isEmpty()
    }

    @Test
    fun `archive vacation and change isArchived value`() = runTest(testDispatcher) {
        viewModel.handleIntent(VacationIntent.LoadData)
        advanceUntilIdle()

        var state = viewModel.vacationState.value

        assertThat(state.vacations).hasSize(1)
        assertThat(state.vacations[0].isArchived).isFalse()

        viewModel.handleIntent(VacationIntent.ArchiveVacation(fakeVacation))
        advanceUntilIdle()

        state = viewModel.vacationState.value

        assertThat(state.isLoading).isFalse()
        assertThat(state.vacations).isNotEmpty()
        assertThat(state.vacations[0].isArchived).isTrue()

        viewModel.handleIntent(VacationIntent.ArchiveVacation(fakeVacation.copy(isArchived = true)))
        advanceUntilIdle()

        state = viewModel.vacationState.value

        assertThat(state.isLoading).isFalse()
        assertThat(state.vacations).isNotEmpty()
        assertThat(state.vacations[0].isArchived).isFalse()
    }

    @Test
    fun `toggle show archived changes view to show archived vacations`() = runTest(testDispatcher) {
        viewModel.handleIntent(VacationIntent.LoadData)
        advanceUntilIdle()

        var state = viewModel.vacationState.value

        assertThat(state.showArchived).isFalse()

        viewModel.handleIntent(VacationIntent.ToggleShowArchived)
        advanceUntilIdle()

        state = viewModel.vacationState.value

        assertThat(state.showArchived).isTrue()

        viewModel.handleIntent(VacationIntent.ToggleShowArchived)
        advanceUntilIdle()

        state = viewModel.vacationState.value

        assertThat(state.showArchived).isFalse()
    }
}