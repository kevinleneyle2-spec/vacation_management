package com.vacation.tripinmind.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.mapper.toDomain
import com.vacation.tripinmind.data.mapper.toDto
import com.vacation.tripinmind.data.mapper.toEntity
import com.vacation.tripinmind.data.remote.source.VacationRemoteDataSource
import com.vacation.tripinmind.model.Vacation
import com.vacation.tripinmind.repository.VacationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.UUID
import javax.inject.Inject

class VacationRepositoryImpl @Inject constructor(
    private val vacationDao: VacationDao,
    private val remoteDataSource: VacationRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) : VacationRepository {

    private fun getSharedVacationById(id: String): Flow<Vacation?> {
        val uid = firebaseAuth.currentUser?.uid ?: return flowOf(null)
        return remoteDataSource.observeSharedVacations(uid).map { dtos ->
            dtos.find { it.id == id }?.toDomain()
        }
    }

    private suspend fun syncRemoteVacations() {
        val currentUserId = firebaseAuth.uid ?: return
        val vacations = remoteDataSource.getVacationsByUserId(currentUserId)

        vacations.forEach { vacation ->
            val localVacation = vacation.toEntity()

            val result = vacationDao.insertItem(localVacation)
            if (result == -1L) {
                vacationDao.updateItem(localVacation)
            }
        }
    }

    override fun observeVacations(): Flow<List<Vacation>> {
        return vacationDao.getAllItems()
            .onStart { syncRemoteVacations() }
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getLocalItems(): Flow<List<Vacation>> = vacationDao.getAllItems().map { vacation ->
        vacation.map {
            it.toDomain()
        }
    }

    override fun getItemById(id: String): Flow<Vacation?> {
        return vacationDao.getItemById(id)
            .onStart { syncRemoteVacationById(id) }
            .map { it?.toDomain() }
    }

    private suspend fun syncRemoteVacationById(id: String) {
        if (firebaseAuth.currentUser == null) return
        val remoteVacation = remoteDataSource.getVacationById(id)

        if (remoteVacation != null) {
            val localVacation = remoteVacation.toEntity()
            val result = vacationDao.insertItem(localVacation)
            if (result == -1L) {
                vacationDao.updateItem(localVacation)
            }
        }
    }

    override suspend fun insertItem(item: Vacation) : Result<Unit> {
        val finalItem = if (item.id.isEmpty()) {
            item.copy(id = UUID.randomUUID().toString())
        } else {
            item
        }

        vacationDao.insertItem(finalItem.toEntity())
        return try {
            if (firebaseAuth.currentUser != null) {
                remoteDataSource.saveVacation(finalItem.toDto())
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteItem(item: Vacation) : Result<Unit> {
        val itemEntity = item.toEntity()
        vacationDao.deleteItem(itemEntity)
        return try {
            if (firebaseAuth.currentUser != null) {
                remoteDataSource.deleteVacation(itemEntity.id)
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateItem(item: Vacation) : Result<Unit> {
        val itemEntity = item.toEntity()
        vacationDao.upsertItem(itemEntity)
        return try {
            if (firebaseAuth.currentUser != null) {
                remoteDataSource.saveVacation(item.toDto())
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getVacationById(id: String, sharedVacation: Boolean): Flow<Vacation?> {
        return if (sharedVacation) {
            getSharedVacationById(id)
        } else {
            getItemById(id)
        }
    }

    override fun getSharedVacationsFlow(): Flow<List<Vacation>> {
        val uid = firebaseAuth.currentUser?.uid ?: return flowOf(emptyList())
        return remoteDataSource.observeSharedVacations(uid).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }
}
