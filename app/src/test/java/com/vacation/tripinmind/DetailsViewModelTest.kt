package com.vacation.tripinmind

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.local.model.Activity
import com.vacation.tripinmind.data.local.model.Day
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.data.repository.VacationRepository
import com.vacation.tripinmind.details.viewmodel.DetailsViewModel
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var vacationRepository: VacationRepository

    private lateinit var mockFirestore: FirebaseFirestore

    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var viewModel: DetailsViewModel

    private val fakeActivity = Activity(
        activityName = "Tour Eiffel",
        activityTime = "10h00",
        activityDuration = "2h",
        activityLocation = "Paris"
    )

    private val fakeVacation = VacationDto(
        id = "1",
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
        isArchived = false,
        createdBy = "",
        shareWith = listOf()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        whenever(mockFirebaseAuth.uid).thenReturn("12345")

        val fakeDao = object : VacationDao {
            override fun getItemById(id: String): Flow<VacationDto> = MutableStateFlow(fakeVacation)
            override fun getAllItems(): Flow<List<VacationDto>> =
                MutableStateFlow(listOf(fakeVacation))

            override suspend fun insertItem(item: VacationDto) : Long { return 0 }
            override suspend fun deleteItem(item: VacationDto) {}
            override suspend fun updateItem(item: VacationDto) {}
        }

        mockFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection = mock(CollectionReference::class.java)
        whenever(mockFirestore.collection(any())).thenReturn(mockCollection)

        vacationRepository = VacationRepository(fakeDao, mockFirestore, mockFirebaseAuth)

        val savedStateHandle = SavedStateHandle(mapOf("vacationId" to "1"))

        viewModel = DetailsViewModel(vacationRepository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load vacation by id and maps to UI model correctly`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("vacationId" to "1"))

        viewModel = DetailsViewModel(vacationRepository, savedStateHandle)

        viewModel.vacation.test {
            assertThat(awaitItem()).isNull()
            val uiModel = awaitItem()

            assertThat(uiModel).isNotNull()
            assertThat(uiModel?.id).isEqualTo("1")
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