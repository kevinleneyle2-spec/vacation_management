package com.vacation.tripinmind

import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.vacation.tripinmind.data.local.interfaces.UserProfileDao
import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.local.model.UserProfileEntity
import com.vacation.tripinmind.data.local.model.VacationEntity
import com.vacation.tripinmind.data.mapper.toDomain
import com.vacation.tripinmind.data.mapper.toDto
import com.vacation.tripinmind.data.repository.UserProfileRepositoryImpl
import com.vacation.tripinmind.home.intent.VacationIntent
import com.vacation.tripinmind.home.model.VacationFilter
import com.vacation.tripinmind.home.viewmodel.HomeViewModel
import com.vacation.tripinmind.repository.VacationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val vacationsFlow = MutableStateFlow<List<VacationEntity>>(emptyList())
    private val sharedVacationsFlow = MutableStateFlow<List<VacationEntity>>(emptyList())

    private lateinit var fakeUserProfileDao: UserProfileDao
    private lateinit var mockFirestore: FirebaseFirestore

    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockFirebaseCrashlytics: FirebaseCrashlytics
    private lateinit var vacationRepository: VacationRepository
    private lateinit var userProfileRepository: UserProfileRepositoryImpl

    private lateinit var viewModel: HomeViewModel

    private val fakeVacation = VacationEntity(
        id = "1",
        name = "Paris",
        startDate = 1685577600000L,
        endDate = 1685923200000L,
        days = emptyList(),
        ideas = emptyList(),
        image = "",
        isArchived = false,
        createdBy = "",
        shareWith = listOf()
    )

    private val fakeUserProfile = UserProfileEntity(
        uuid = "12345",
        shareCode = "123-456-123-456"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        mockFirebaseCrashlytics = mock(FirebaseCrashlytics::class.java)
        val mockFirebaseUser = mock(com.google.firebase.auth.FirebaseUser::class.java)

        whenever(mockFirebaseAuth.uid).thenReturn("12345")
        whenever(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.uid).thenReturn("12345")

        vacationsFlow.value = listOf(fakeVacation)
        sharedVacationsFlow.value = emptyList()

        fakeUserProfileDao = object : UserProfileDao {
            override suspend fun getItemById(uuid: String): UserProfileEntity =
                fakeUserProfile

            override suspend fun insertItem(item: UserProfileEntity) {}

            override suspend fun getItemByCode(shareCode: String): UserProfileEntity =
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

        vacationRepository = mock(VacationRepository::class.java)
        whenever(vacationRepository.observeVacations()).thenReturn(vacationsFlow.map { vacations ->
            vacations.map { it.toDomain() }
        })
        whenever(vacationRepository.getSharedVacationsFlow()).thenReturn(sharedVacationsFlow.map { vacations ->
            vacations.map {
                it.toDomain()
            }
        })

        userProfileRepository = mock(UserProfileRepositoryImpl::class.java)

        viewModel = HomeViewModel(
            vacationRepository,
            userProfileRepository,
            mockFirebaseAuth,
            mockFirebaseCrashlytics
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create share code should set crashlytics user id`() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.vacationState.collect {} }
        whenever(userProfileRepository.insertItem()).thenReturn(Result.success("123-456-123-456"))

        viewModel.handleIntent(VacationIntent.CreateShareCode)
        advanceUntilIdle()

        verify(mockFirebaseCrashlytics).setUserId("12345")
    }

    @Test
    fun `loads vacation and emits non-empty list`() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.vacationState.collect {} }
        advanceUntilIdle()

        val state = viewModel.vacationState.value

        assertThat(state.isLoading).isFalse()
        assertThat(state.vacations).isNotEmpty()
        assertThat(state.vacations[0].name).isEqualTo("Paris")
    }

    @Test
    fun `delete vacation and emits empty list`() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.vacationState.collect {} }
        advanceUntilIdle()

        assertThat(viewModel.vacationState.value.vacations).hasSize(1)

        viewModel.handleIntent(VacationIntent.DeleteVacation(fakeVacation.toDomain()))
        advanceUntilIdle()

        verify(vacationRepository).deleteItem(fakeVacation.toDomain())
    }

    @Test
    fun `archive vacation and change isArchived value`() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.vacationState.collect {} }
        advanceUntilIdle()

        val state = viewModel.vacationState.value
        assertThat(state.vacations).hasSize(1)

        viewModel.handleIntent(VacationIntent.ArchiveVacation(fakeVacation.toDomain()))
        advanceUntilIdle()

        verify(vacationRepository).updateItem(fakeVacation.copy(isArchived = true).toDomain())
    }

    @Test
    fun `toggle show archived changes view to show archived vacations`() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.vacationState.collect {} }
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
        backgroundScope.launch { viewModel.vacationState.collect {} }
        whenever(userProfileRepository.insertItem()).thenReturn(Result.success("123-456-123-456"))

        var state = viewModel.vacationState.value
        assertThat(state.shareCode).isEmpty()

        viewModel.handleIntent(VacationIntent.CreateShareCode)
        advanceUntilIdle()

        state = viewModel.vacationState.value

        assertThat(state.shareCode).isNotEmpty()
        assertThat(state.shareCode).isEqualTo("123-456-123-456")
    }
}