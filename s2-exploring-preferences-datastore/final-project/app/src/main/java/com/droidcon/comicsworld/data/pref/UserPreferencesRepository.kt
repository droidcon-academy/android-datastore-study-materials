package com.droidcon.comicsworld.data.pref

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.droidcon.comicsworld.data.ComicCategory
import com.droidcon.comicsworld.data.SortOrder
import com.droidcon.comicsworld.data.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(private val datastore: DataStore<Preferences>) {

    companion object {
        private val LogTag = Companion::class.java.simpleName
        private val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
        private val COMIC_CATEGORY_FILTER_KEY = stringPreferencesKey("comic_category")
    }

    fun getUserPreferences(): Flow<UserPreferences> {
        return datastore.data.catch { exception ->
            if (exception is IOException) {
                Log.e(
                    LogTag,
                    "The following error occurred while reading data from data preferences $exception"
                )
                emit(emptyPreferences())
            } else throw exception
        }.map { preferences ->
            preferences.toUserPreferences()
        }
    }

    suspend fun disableSorting() {
        datastore.edit { preferences ->
            preferences[SORT_ORDER_KEY] = SortOrder.NONE.name
        }
    }

    suspend fun removeComicCategoryFilter() {
        datastore.edit { preferences ->
            preferences[COMIC_CATEGORY_FILTER_KEY] = ComicCategory.ALL.name
        }
    }

    suspend fun filterComicsByCategory(comicCategory: ComicCategory) {
        datastore.edit { preferences ->
            val currentComicFilterCategory = preferences.getCurrentComicCategoryFilter()
            // perform a write only if the current comic category is not equal to the new comic category
            if (comicCategory == currentComicFilterCategory) {
                return@edit
            }
            preferences[COMIC_CATEGORY_FILTER_KEY] = comicCategory.name
        }
    }

    suspend fun enableSortByRating(enabled: Boolean) {
        datastore.edit { preferences ->
            val currentSortOrder =
                SortOrder.valueOf(preferences[SORT_ORDER_KEY] ?: SortOrder.NONE.name)
            val newSortOrder = if (enabled) {
                SortOrder.BY_RATING
            } else if (currentSortOrder == SortOrder.BY_RATING) SortOrder.NONE
            else currentSortOrder
            preferences[SORT_ORDER_KEY] = newSortOrder.name
        }
    }

    suspend fun enableSortByDateAdded(enabled: Boolean) {
        datastore.edit { preferences ->
            val currentSortOrder = preferences.getCurrentSortOrderFromPreferences()
            val newSortOrder =
                if (enabled) SortOrder.BY_DATE_ADDED else if (currentSortOrder == SortOrder.BY_DATE_ADDED) SortOrder.NONE else currentSortOrder
            preferences[SORT_ORDER_KEY] = newSortOrder.name
        }
    }


    private fun Preferences.getCurrentSortOrderFromPreferences(): SortOrder {
        return SortOrder.valueOf(this[SORT_ORDER_KEY] ?: SortOrder.NONE.name)
    }

    private fun Preferences.getCurrentComicCategoryFilter(): ComicCategory {
        return ComicCategory.valueOf(this[COMIC_CATEGORY_FILTER_KEY] ?: ComicCategory.ALL.name)
    }

    private fun Preferences.toUserPreferences(): UserPreferences {
        val sortOrder = getCurrentSortOrderFromPreferences()
        val comicCategoryFilter =
            ComicCategory.valueOf(this[COMIC_CATEGORY_FILTER_KEY] ?: ComicCategory.ALL.name)
        return UserPreferences(comicCategory = comicCategoryFilter, sortOrder = sortOrder)
    }
}
