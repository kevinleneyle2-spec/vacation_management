package com.vacation.tripinmind.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vacation.tripinmind.data.local.database.VacationDatabase
import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.local.model.VacationDto
import com.google.common.truth.Truth.assertThat
import com.vacation.tripinmind.data.local.database.UserProfileDatabase
import com.vacation.tripinmind.data.local.interfaces.UserProfileDao
import com.vacation.tripinmind.data.local.model.UserProfileDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserProfileDatabaseTest {
    private lateinit var db: UserProfileDatabase
    private lateinit var dao: UserProfileDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, UserProfileDatabase::class.java).build()
        dao = db.userProfileDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadUserProfile() = runBlocking {
        val userProfile = UserProfileDto(
            uuid = "12345",
            shareCode = "123-456-123-456"
        )
        dao.insertItem(userProfile)
        val result = dao.getItemById("12345")
        assertThat(result?.shareCode).isEqualTo("123-456-123-456")
    }

    @Test
    fun readUserProfileByUid() = runBlocking {
        val userProfile = UserProfileDto(
            uuid = "12345",
            shareCode = "123-456-123-456"
        )
        dao.insertItem(userProfile)
        val result = dao.getItemById("12345")
        assertThat(result?.shareCode).isEqualTo("123-456-123-456")
    }

    @Test
    fun readUserProfileByShareCode() = runBlocking {
        val userProfile = UserProfileDto(
            uuid = "12345",
            shareCode = "123-456-123-456"
        )
        dao.insertItem(userProfile)
        val result = dao.getItemByCode("123-456-123-456")
        assertThat(result?.uuid).isEqualTo("12345")
    }
}