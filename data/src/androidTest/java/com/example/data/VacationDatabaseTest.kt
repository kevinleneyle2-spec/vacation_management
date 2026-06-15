package com.example.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.local.database.VacationDatabase
import com.example.data.local.interfaces.VacationDao
import com.example.data.local.model.VacationDto
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VacationDatabaseTest {
    private lateinit var db: VacationDatabase
    private lateinit var dao: VacationDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VacationDatabase::class.java).build()
        dao = db.vacationDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadVacation() = runBlocking {
        val vacation = VacationDto(
            id = 1,
            name = "Test",
            startDate = "2023-06-01",
            nbrDay = 5,
            days = emptyList(),
            ideas = emptyList(),
            image = "",
            isArchived = false
        )
        dao.insertItem(vacation)
        val result = dao.getItemById(1).first()
        assertThat(result.name).isEqualTo("Test")
    }

    @Test
    fun deleteAndReadVacation() = runBlocking {
        val vacation = VacationDto(
            id = 1,
            name = "Test",
            startDate = "2023-06-01",
            nbrDay = 5,
            days = emptyList(),
            ideas = emptyList(),
            image = "",
            isArchived = false
        )
        dao.insertItem(vacation)
        val result = dao.getItemById(1).first()
        assertThat(result.name).isEqualTo("Test")
        dao.deleteItem(vacation)
        assertThat(dao.getAllItems().first()).isEmpty()
    }

    @Test
    fun updateAndReadVacation() = runBlocking {
        val vacation = VacationDto(
            id = 1,
            name = "Test",
            startDate = "2023-06-01",
            nbrDay = 5,
            days = emptyList(),
            ideas = emptyList(),
            image = "",
            isArchived = false
        )
        dao.insertItem(vacation)
        assertThat(dao.getItemById(1).first().name).isEqualTo("Test")
        dao.updateItem(vacation.copy(name = "Updated"))
        assertThat(dao.getItemById(1).first().name).isEqualTo("Updated")
    }

    @Test
    fun readVacation() = runBlocking {
        val vacation = VacationDto(
            id = 1,
            name = "Test",
            startDate = "2023-06-01",
            nbrDay = 5,
            days = emptyList(),
            ideas = emptyList(),
            image = "",
            isArchived = false
        )
        dao.insertItem(vacation)

        val result = dao.getItemById(1)
        assertThat(result.first().name).isEqualTo("Test")
    }

    @Test
    fun readAllVacations() = runBlocking {
        val vacation = VacationDto(
            id = 1,
            name = "Test",
            startDate = "2023-06-01",
            nbrDay = 5,
            days = emptyList(),
            ideas = emptyList(),
            image = "",
            isArchived = false
        )
        dao.insertItem(vacation)
        dao.insertItem(vacation.copy(id = 2, name = "Test2"))

        val result = dao.getAllItems()
        assertThat(result.first()[0].name).isEqualTo("Test")
        assertThat(result.first()[1].name).isEqualTo("Test2")
    }
}