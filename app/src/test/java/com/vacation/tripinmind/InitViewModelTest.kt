package com.vacation.tripinmind

import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.local.model.Activity
import com.vacation.tripinmind.data.local.model.Day
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.data.repository.VacationRepository
import com.vacation.tripinmind.vacation.intent.InitIntent
import com.vacation.tripinmind.vacation.viewmodel.InitViewModel
import com.google.common.truth.Truth.assertThat
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
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
class InitViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val vacationsFlow = MutableStateFlow<List<VacationDto>>(emptyList())

    private lateinit var fakeVacationDao: VacationDao

    private lateinit var mockFirestore: FirebaseFirestore

    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var vacationRepository: VacationRepository

    private lateinit var viewModel: InitViewModel

    private val fakeVacation = VacationDto(
        id = "1",
        name = "Paris",
        startDate = "2023-06-01",
        nbrDay = 5,
        days = listOf(
            Day(
                nameDay = "Day 1",
                additionalInfo = "Info",
                activity = listOf(
                    Activity(
                        activityName = "Louvre",
                        activityTime = "11h00",
                        activityDuration = "1h",
                        activityLocation = "Paris Museum"
                    )
                )
            )
        ),
        ideas = listOf(
            "piscine"
        ),
        image = "",
        isArchived = false,
        createdBy = "12345",
        shareWith = listOf()
    )

    @Before
    fun setUp() {
        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        whenever(mockFirebaseAuth.uid).thenReturn("12345")

        Dispatchers.setMain(testDispatcher)
        vacationsFlow.value = listOf(fakeVacation)

        fakeVacationDao = object : VacationDao {
            override fun getItemById(id: String): Flow<VacationDto?> =
                MutableStateFlow(fakeVacation)

            override fun getAllItems(): Flow<List<VacationDto>> = vacationsFlow

            override suspend fun insertItem(item: VacationDto) : Long {
                vacationsFlow.value += item
                return 0
            }

            override suspend fun deleteItem(item: VacationDto) {
                vacationsFlow.value = vacationsFlow.value.filter { it.id != item.id }
            }

            override suspend fun updateItem(item: VacationDto) {
                vacationsFlow.value = vacationsFlow.value.map {
                    if (it.id == item.id) item else it
                }
            }
        }

        mockFirestore = mock(FirebaseFirestore::class.java)
        val mockCollection = mock(CollectionReference::class.java)
        val mockDocumentReference = mock(DocumentReference::class.java)

        whenever(mockFirestore.collection(any())).thenReturn(mockCollection)
        whenever(mockFirestore.document(any())).thenReturn(mockDocumentReference)

        whenever(mockCollection.document(any())).thenReturn(mockDocumentReference)
        whenever(mockCollection.document()).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.id).thenReturn("mock_id")
        whenever(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

        vacationRepository = VacationRepository(fakeVacationDao, mockFirestore, mockFirebaseAuth)
        viewModel = InitViewModel(vacationRepository, mockFirebaseAuth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `update vacation name`() = runTest(testDispatcher) {
        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value

        assertThat(state.vacationName).isEqualTo("Paris")

        viewModel.handleIntent(InitIntent.UpdateName("Nantes"))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.vacationName).isEqualTo("Nantes")
    }

    @Test
    fun `update vacation start date`() = runTest(testDispatcher) {
        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value

        assertThat(state.startDate).isEqualTo("2023-06-01")

        viewModel.handleIntent(InitIntent.UpdateStartDate("2023-07-02"))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.startDate).isEqualTo("2023-07-02")
    }

    @Test
    fun `update vacation number of days`() = runTest(testDispatcher) {
        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value

        assertThat(state.numDays).isEqualTo(5)

        viewModel.handleIntent(InitIntent.UpdateDays("10"))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.numDays).isEqualTo(10)
    }

    @Test
    fun `update additional info for a day`() = runTest(testDispatcher) {
        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value
        assertThat(state.days[0].additionalInfo).isEqualTo("Info")

        viewModel.handleIntent(InitIntent.UpdateAddInfo(0, "with friend"))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.days[0].additionalInfo).isEqualTo("with friend")
    }

    @Test
    fun `add day activity`() = runTest(testDispatcher) {
        val activity = Activity(
            activityName = "Tour Eiffel",
            activityTime = "10h00",
            activityDuration = "2h",
            activityLocation = "Paris"
        )

        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value
        assertThat(state.days[0].activity.size).isEqualTo(1)

        viewModel.handleIntent(InitIntent.AddDayActivities(0, activity))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.days[0].activity.size).isEqualTo(2)
        assertThat(state.days[0].activity[1].activityName).isEqualTo("Tour Eiffel")
        assertThat(state.days[0].activity[1].activityTime).isEqualTo("10h00")
        assertThat(state.days[0].activity[1].activityDuration).isEqualTo("2h")
        assertThat(state.days[0].activity[1].activityLocation).isEqualTo("Paris")
    }

    @Test
    fun `update day activity`() = runTest(testDispatcher) {
        val activity = Activity(
            activityName = "Tour Eiffel",
            activityTime = "10h00",
            activityDuration = "2h",
            activityLocation = "Paris"
        )

        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value
        assertThat(state.days[0].activity.size).isEqualTo(1)

        viewModel.handleIntent(InitIntent.UpdateDayActivities(0, 0, activity))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.days[0].activity.size).isEqualTo(1)
        assertThat(state.days[0].activity[0].activityName).isEqualTo("Tour Eiffel")
        assertThat(state.days[0].activity[0].activityTime).isEqualTo("10h00")
        assertThat(state.days[0].activity[0].activityDuration).isEqualTo("2h")
        assertThat(state.days[0].activity[0].activityLocation).isEqualTo("Paris")
    }

    @Test
    fun `remove day activity`() = runTest(testDispatcher) {
        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value
        assertThat(state.days[0].activity.size).isEqualTo(1)

        viewModel.handleIntent(InitIntent.RemoveDayActivities(0, 0))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.days[0].activity.size).isEqualTo(0)
    }

    @Test
    fun `add idea`() = runTest(testDispatcher) {
        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value
        assertThat(state.ideas.size).isEqualTo(1)

        viewModel.handleIntent(InitIntent.AddIdea("new idea"))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.ideas.size).isEqualTo(2)
    }

    @Test
    fun `remove idea`() = runTest(testDispatcher) {
        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value
        assertThat(state.ideas.size).isEqualTo(1)

        viewModel.handleIntent(InitIntent.RemoveIdea(0))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.ideas.size).isEqualTo(0)
    }

    @Test
    fun `update image`() = runTest(testDispatcher) {
        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        var state = viewModel.initState.value
        assertThat(state.image).isEmpty()

        viewModel.handleIntent(InitIntent.UpdateImage("vacation_ico"))
        advanceUntilIdle()

        state = viewModel.initState.value
        assertThat(state.image).isEqualTo("vacation_ico")
    }

    @Test
    fun `create vacation persists to repository`() = runTest(testDispatcher) {
        val newVacation = fakeVacation.copy(id = "2", name = "London Trip")

        viewModel.handleIntent(InitIntent.CreateVacation(newVacation))
        advanceUntilIdle()

        assertThat(vacationsFlow.value).contains(newVacation)
    }

    @Test
    fun `create vacation with wrong createdBy fails`() = runTest(testDispatcher) {
        val newVacation = fakeVacation.copy(id = "2", name = "London Trip", createdBy = "wrongUser")

        viewModel.handleIntent(InitIntent.CreateVacation(newVacation))
        advanceUntilIdle()

        assertThat(vacationsFlow.value).contains(fakeVacation)
    }

    @Test
    fun `update vacation persists changes to repository`() = runTest(testDispatcher) {
        val updatedVacation = fakeVacation.copy(name = "Paris Modified")

        viewModel.handleIntent(InitIntent.UpdateVacation(updatedVacation))
        advanceUntilIdle()

        val persisted = vacationsFlow.value.find { it.id == "1" }
        assertThat(persisted?.name).isEqualTo("Paris Modified")
    }

    @Test
    fun `load vacation from repository`() = runTest(testDispatcher) {
        viewModel.handleIntent(InitIntent.LoadVacation("1"))
        advanceUntilIdle()

        val state = viewModel.initState.value
        assertThat(state.vacationName).isEqualTo("Paris")
    }
}
