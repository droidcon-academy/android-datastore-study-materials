package com.droidcon.comicsworld.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.droidcon.comicsworld.UserComicPreference
import com.droidcon.comicsworld.data.proto.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    private const val USER_PREFERENCES_NAME = "comics_world_preferences"
    private const val USER_PROTO_PREFERENCES_NAME = "comics_world_proto_preferences.pb"

    @Provides
    @Singleton
    fun providePreferenceDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(produceFile = {
            appContext.preferencesDataStoreFile(USER_PREFERENCES_NAME)
        })

    @Provides
    @Singleton
    fun provideProtoDataStore(@ApplicationContext appContext: Context): DataStore<UserComicPreference> {
        return DataStoreFactory.create(serializer = UserPreferencesSerializer) {
            File(appContext.filesDir, USER_PROTO_PREFERENCES_NAME)
        }
    }

}