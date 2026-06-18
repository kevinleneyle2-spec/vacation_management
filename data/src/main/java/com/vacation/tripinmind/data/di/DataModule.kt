package com.vacation.tripinmind.data.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.vacation.tripinmind.data.local.database.UserProfileDatabase
import com.vacation.tripinmind.data.local.database.VacationDatabase
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
    fun provideVacationRepository(
        @ApplicationContext context: Context,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): VacationRepository {
        return VacationRepository(
            VacationDatabase.getDatabase(context).vacationDao(),
            firestore,
            firebaseAuth
        )
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        @ApplicationContext context: Context,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): UserProfileRepository {
        return UserProfileRepository(
            UserProfileDatabase.getDatabase(context).userProfileDao(),
            firestore,
            firebaseAuth
        )
    }
}