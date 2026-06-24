package com.vacation.tripinmind

import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.data.repository.VacationRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.vacation.tripinmind.data.local.interfaces.UserProfileDao
import com.vacation.tripinmind.data.local.model.UserProfileDto
import com.vacation.tripinmind.data.repository.UserProfileRepository
import com.vacation.tripinmind.home.model.VacationFilter
import com.vacation.tripinmind.home.intent.VacationIntent
import com.vacation.tripinmind.home.viewmodel.HomeViewModel
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
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val vacationsFlow = MutableStateFlow<List<VacationDto>>(emptyList())

    private lateinit var fakeVacationDao: VacationDao
    private lateinit var fakeUserProfileDao: UserProfileDao
    private lateinit var mockFirestore: FirebaseFirestore

    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var vacationRepository: VacationRepository
    private lateinit var userProfileRepository: UserProfileRepository

    private lateinit var viewModel: HomeViewModel

    private val fakeVacation = VacationDto(
        id = "1",
        name = "Paris",
        startDate = "2023-06-01",
        nbrDay = 5,
        days = emptyList(),
        ideas = emptyList(),
        image = "",
        isArchived = false,
        createdBy = "",
        shareWith = listOf()
    )

    private val fakeUserProfile = UserProfileDto(
        uuid = "12345",
        shareCode = "123-456-123-456"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        val mockFirebaseUser = mock(com.google.firebase.auth.FirebaseUser::class.java)

        whenever(mockFirebaseAuth.uid).thenReturn("12345")
        whenever(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.uid).thenReturn("12345")

        vacationsFlow.value = listOf(fakeVacation)

        fakeVacationDao = object : VacationDao {
            override fun getItemById(id: String): Flow<VacationDto> =
                MutableStateFlow(fakeVacation)

            override fun getAllItems(): Flow<List<VacationDto>> = vacationsFlow

            override suspend fun insertItem(item: VacationDto) : Long { return 0 }

            override suspend fun deleteItem(item: VacationDto) {
                vacationsFlow.value = vacationsFlow.value.filter { it.id != item.id }
            }

            override suspend fun updateItem(item: VacationDto) {
                vacationsFlow.value = vacationsFlow.value.map {
                    if (it.id == item.id) item else it
                }
            }
        }

        fakeUserProfileDao = object : UserProfileDao {
            override suspend fun getItemById(uuid: String): UserProfileDto =
                fakeUserProfile

            override suspend fun insertItem(item: UserProfileDto) { }

            override suspend fun getItemByCode(shareCode: String): UserProfileDto =
                fakeUserProfile
        }

        mockFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection = mock(CollectionReference::class.java)
        whenever(mockFirestore.collection(any())).thenReturn(mockCollection)
        val mockQuery = mock(Query::class.java)
        val mockRegistration = mock(ListenerRegistration::class.java)

        whenever(mockFirestore.collection(any())).thenReturn(mockCollection)
        whenever(mockCollection.where(any<Filter>())).thenReturn(mockQuery)
        whenever(mockQuery.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
        whenever(mockQuery.addSnapshotListener(any())).thenReturn(mockRegistration)

        vacationRepository = VacationRepository(fakeVacationDao, mockFirestore, mockFirebaseAuth)
        userProfileRepository = UserProfileRepository(fakeUserProfileDao, mockFirestore, mockFirebaseAuth)

        viewModel = HomeViewModel(vacationRepository, userProfileRepository)
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

        assertThat(state.selectedFilter).isEqualTo(VacationFilter.PROJECTS)

        viewModel.handleIntent(VacationIntent.ToggleShowVacationFilter(VacationFilter.SHARED))
        advanceUntilIdle()

        state = viewModel.vacationState.value

        assertThat(state.selectedFilter).isEqualTo(VacationFilter.SHARED)

        viewModel.handleIntent(VacationIntent.ToggleShowVacationFilter(VacationFilter.ARCHIVED))
        advanceUntilIdle()

        state = viewModel.vacationState.value

        assertThat(state.selectedFilter).isEqualTo(VacationFilter.ARCHIVED)
    }

    @Test
    fun `create share code and emits share code`() = runTest(testDispatcher) {
        var state = viewModel.vacationState.value
        assertThat(state.shareCode).isEmpty()

        viewModel.handleIntent(VacationIntent.CreateShareCode)
        advanceUntilIdle()

        state = viewModel.vacationState.value

        assertThat(state.shareCode).isNotEmpty()
        assertThat(state.shareCode).isEqualTo("123-456-123-456")
    }
}