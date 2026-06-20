package com.vacation.tripinmind

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.vacation.tripinmind.data.local.model.Activity
import com.vacation.tripinmind.data.local.model.Day
import com.vacation.tripinmind.data.local.model.UserProfileDto
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.data.repository.UserProfileRepository
import com.vacation.tripinmind.data.repository.VacationRepository
import com.vacation.tripinmind.details.intent.DetailsIntent
import com.vacation.tripinmind.details.model.DetailsError
import com.vacation.tripinmind.details.viewmodel.DetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var vacationRepository: VacationRepository
    private lateinit var userProfileRepository: UserProfileRepository
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var viewModel: DetailsViewModel

    private val fakeActivity = Activity(
        activityName = "Tour Eiffel",
        activityTime = "10h00",
        activityDuration = "2h",
        activityLocation = "Paris"
    )

    private val fakeUserProfileYourSelf = UserProfileDto(
        uuid = "12345",
        shareCode = "000000000"
    )

    private val fakeUserProfile = UserProfileDto(
        uuid = "54321",
        shareCode = "000000000"
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
        shareWith = listOf("111111"),
        shareWithUid = listOf("123456")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        whenever(mockFirebaseAuth.uid).thenReturn("12345")

        vacationRepository = mock(VacationRepository::class.java)
        userProfileRepository = mock(UserProfileRepository::class.java)

        whenever(vacationRepository.getVacationById(any(), any()))
            .thenReturn(MutableStateFlow(fakeVacation))

        val savedStateHandle = SavedStateHandle(mapOf("vacationId" to "1"))

        viewModel = DetailsViewModel(
            vacationRepository, userProfileRepository, mockFirebaseAuth, savedStateHandle
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load vacation by id and maps to UI model correctly`() = runTest {
        viewModel.vacation.test {
            assertThat(awaitItem()).isNull()
            val uiModel = awaitItem()

            assertThat(uiModel).isNotNull()
            assertThat(uiModel?.vacation?.id).isEqualTo("1")
            assertThat(uiModel?.vacation?.name).isEqualTo("Paris Trip")
            assertThat(uiModel?.vacation?.days).hasSize(1)
            assertThat(uiModel?.vacation?.days?.get(0)?.activity).hasSize(1)
            assertThat(uiModel?.vacation?.days?.get(0)?.activity?.get(0)?.activityName).isEqualTo("Tour Eiffel")
            assertThat(uiModel?.vacation?.ideas).contains("Manger des crêpes")
        }
    }

    @Test
    fun `when id is missing returns null state`() = runTest {
        val emptyViewModel = DetailsViewModel(
            vacationRepository,
            userProfileRepository,
            mockFirebaseAuth,
            SavedStateHandle()
        )

        advanceUntilIdle()

        assertThat(emptyViewModel.vacation.value).isNull()
    }

    @Test
    fun `when ClearError intent is sent then error in UI model should be null`() = runTest {
        whenever(userProfileRepository.getItemByCode(any())).thenReturn(null)

        viewModel.vacation.test {
            skipItems(2)

            viewModel.handleIntent(DetailsIntent.AddViewer(fakeVacation, "000000000"))

            val modelWithError = awaitItem()
            assertThat(modelWithError?.error).isEqualTo(DetailsError.NOT_FOUND)

            viewModel.handleIntent(DetailsIntent.ClearError)

            val modelAfterClear = awaitItem()
            assertThat(modelAfterClear?.error).isNull()
        }
    }

    @Test
    fun `when AddViewer intent is sent then error NOT_YOURSELF is returned`() = runTest {
        whenever(userProfileRepository.getItemByCode(any())).thenReturn(fakeUserProfileYourSelf)

        viewModel.vacation.test {
            skipItems(2)

            viewModel.handleIntent(DetailsIntent.AddViewer(fakeVacation, "000000000"))

            val modelWithSuccess = awaitItem()

            assertThat(modelWithSuccess?.error).isEqualTo(DetailsError.NOT_YOURSELF)
        }
    }

    @Test
    fun `when AddViewer intent is sent then error ALREADY_ADDED is returned`() = runTest {
        whenever(userProfileRepository.getItemByCode(any())).thenReturn(UserProfileDto(
            uuid = "123456",
            shareCode = "111111"
        ))

        viewModel.vacation.test {
            skipItems(2)

            viewModel.handleIntent(DetailsIntent.AddViewer(fakeVacation, "111111"))

            val modelWithSuccess = awaitItem()

            assertThat(modelWithSuccess?.error).isEqualTo(DetailsError.ALREADY_ADDED)
        }
    }

    @Test
    fun `when AddViewer intent is sent then error in UI model should be null`() = runTest {
        whenever(userProfileRepository.getItemByCode(any())).thenReturn(fakeUserProfile)
        whenever(vacationRepository.updateItem(any())).thenReturn(Unit)

        viewModel.vacation.test {
            skipItems(2)

            viewModel.handleIntent(DetailsIntent.AddViewer(fakeVacation, "000000000"))

            val modelWithSuccess = awaitItem()

            assertThat(modelWithSuccess?.error).isEqualTo(DetailsError.SUCCESS)

            verify(vacationRepository).updateItem(check { updatedVacation ->
                assertThat(updatedVacation.shareWith).contains("000000000")
                assertThat(updatedVacation.shareWithUid).contains("123456")
            })
        }
    }

    @Test
    fun `when RemoveViewer intent is sent then vacationRepository updateItem is called with removed viewer`() = runTest {
        whenever(vacationRepository.updateItem(any())).thenReturn(Unit)

        viewModel.vacation.test {
            skipItems(2)

            viewModel.handleIntent(DetailsIntent.RemoveViewer(0))
            advanceUntilIdle()

            verify(vacationRepository).updateItem(check { updatedVacation ->
                assertThat(updatedVacation.shareWith).isEmpty()
                assertThat(updatedVacation.shareWithUid).isEmpty()
            })
        }
    }
}
