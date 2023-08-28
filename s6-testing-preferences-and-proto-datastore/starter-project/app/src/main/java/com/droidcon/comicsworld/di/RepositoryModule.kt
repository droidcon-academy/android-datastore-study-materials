package com.droidcon.comicsworld.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.droidcon.comicsworld.UserComicPreference
import com.droidcon.comicsworld.data.ComicsRepository
import com.droidcon.comicsworld.data.ComicsRepositoryDelegate
import com.droidcon.comicsworld.data.pref.UserPreferencesRepository
import com.droidcon.comicsworld.data.proto.UserProtoPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideComicsRepository(): ComicsRepository = ComicsRepositoryDelegate()

    @Provides
    fun provideUserProtoPreferencesRepository(datastoreProtoPreferences: DataStore<UserComicPreference>): UserProtoPreferencesRepository =
        UserProtoPreferencesRepository(datastoreProtoPreferences)

    @Provides
    fun provideUserPreferenceRepository(datastorePreferences: DataStore<Preferences>): UserPreferencesRepository =
        UserPreferencesRepository(datastorePreferences)

}