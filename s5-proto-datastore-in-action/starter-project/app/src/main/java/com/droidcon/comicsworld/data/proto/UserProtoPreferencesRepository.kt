package com.droidcon.comicsworld.data.proto

import android.util.Log
import androidx.datastore.core.DataStore
import com.droidcon.comicsworld.UserComicPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject

class UserProtoPreferencesRepository @Inject constructor(private val userPreferenceProtoStore: DataStore<UserComicPreference>) {
    companion object {
        private val LoggingTag = UserProtoPreferencesRepository::class.java.simpleName

    }

    fun getUserPreferences(): Flow<UserComicPreference> {
        return userPreferenceProtoStore.data.catch { exception ->
            if (exception is IOException) {
                Log.e(
                    LoggingTag,
                    "An error occurred while reading from the proto data store $exception"
                )
                emit(UserComicPreference.getDefaultInstance())
            } else throw exception
        }
    }

    suspend fun filterComicsByCategory(comicCategory: UserComicPreference.ComicCategory) {
        userPreferenceProtoStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setComicCategory(comicCategory).build()
        }
    }

    suspend fun enableSortByRating(enabled: Boolean) {
        userPreferenceProtoStore.updateData { currentPreferences ->
            val currentSortOrder = currentPreferences.sortOrder
            val newSortOrder =
                if (enabled) UserComicPreference.SortOrder.BY_RATING else if (currentSortOrder == UserComicPreference.SortOrder.BY_RATING) UserComicPreference.SortOrder.NONE else currentSortOrder
            currentPreferences.toBuilder().setSortOrder(newSortOrder).build()
        }
    }

    suspend fun enableSortByDateAdded(enabled: Boolean) {
        userPreferenceProtoStore.updateData { currentPreferences ->
            val currentSortOrder = currentPreferences.sortOrder
            val newSortOrder =
                if (enabled) UserComicPreference.SortOrder.BY_DATE_ADDED else if (currentSortOrder == UserComicPreference.SortOrder.BY_DATE_ADDED) UserComicPreference.SortOrder.NONE else currentSortOrder
            currentPreferences.toBuilder().setSortOrder(newSortOrder).build()
        }
    }

    suspend fun disableSorting() {
        userPreferenceProtoStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setSortOrder(UserComicPreference.SortOrder.NONE).build()
        }
    }

    suspend fun removeComicCategoryFilter() {
        userPreferenceProtoStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setComicCategory(UserComicPreference.ComicCategory.ALL)
                .build()
        }
    }

}