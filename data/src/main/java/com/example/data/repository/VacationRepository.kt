package com.example.data.repository

import com.example.data.local.interfaces.VacationDao
import com.example.data.local.interfaces.VacationInterface
import com.example.data.local.model.VacationDto
import kotlinx.coroutines.flow.Flow

class VacationRepository(private val vacationDao: VacationDao) : VacationInterface {

    override fun getAllItems(): Flow<List<VacationDto>> =
        vacationDao.getAllItems()

    override fun getItemById(id: Int): Flow<VacationDto> =
        vacationDao.getItemById(id)

    override suspend fun insertItem(item: VacationDto) {
        vacationDao.insertItem(item)
    }

    override suspend fun deleteItem(item: VacationDto) {
        vacationDao.deleteItem(item)
    }

    override suspend fun updateItem(item: VacationDto) {
        vacationDao.updateItem(item)
    }
}