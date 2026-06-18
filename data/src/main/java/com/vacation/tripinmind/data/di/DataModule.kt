package com.vacation.tripinmind.data.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.vacation.tripinmind.data.local.database.UserProfileDatabase
import com.vacation.tripinmind.data.local.database.VacationDatabase
import com.vacation.tripinmind.data.local.interfaces.UserProfileDao
import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.repository.UserProfileRepository
import com.vacation.tripinmind.data.repository.VacationRepository
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
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideVacationDatabase(@ApplicationContext context: Context): VacationDatabase {
        return VacationDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideVacationDao(database: VacationDatabase): VacationDao {
        return database.vacationDao()
    }

    @Provides
    @Singleton
    fun provideVacationRepository(
        vacationDao: VacationDao,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): VacationRepository {
        return VacationRepository(vacationDao, firestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideUserProfileDatabase(@ApplicationContext context: Context): UserProfileDatabase {
        return UserProfileDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(database: UserProfileDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        userProfileDao: UserProfileDao,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): UserProfileRepository {
        return UserProfileRepository(userProfileDao, firestore, firebaseAuth)
    }
}
