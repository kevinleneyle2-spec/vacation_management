package com.example.data.di

import android.content.Context
import com.example.data.local.database.VacationDatabase
import com.example.data.repository.VacationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideVacationRepository(@ApplicationContext context: Context): VacationRepository {
        return VacationRepository(VacationDatabase.getDatabase(context).vacationDao())
    }
}