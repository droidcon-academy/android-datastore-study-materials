package com.droidcon.comicsworld.di

import com.droidcon.comicsworld.data.ComicsRepository
import com.droidcon.comicsworld.data.ComicsRepositoryDelegate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideComicsRepository(): ComicsRepository = ComicsRepositoryDelegate()
}