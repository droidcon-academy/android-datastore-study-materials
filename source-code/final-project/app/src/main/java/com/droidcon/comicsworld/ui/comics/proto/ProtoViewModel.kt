package com.droidcon.comicsworld.ui.comics.proto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.comicsworld.UserComicPreference
import com.droidcon.comicsworld.data.*
import com.droidcon.comicsworld.data.proto.UserProtoPreferencesRepository
import com.droidcon.comicsworld.ui.comics.ComicsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProtoViewModel @Inject constructor(
    private val comicsRepository: ComicsRepository,
    private val protoPreferenceRepository: UserProtoPreferencesRepository
) : ViewModel() {
    private val userPreferences = protoPreferenceRepository.getUserPreferences()

    val comicsUiModel: StateFlow<ComicsUiModel>
        get() = combine(
            userPreferences,
            comicsRepository.getComics()
        ) { preferences, comics ->
            val sortedAndFilteredComics = sortAndFilterComicsByGivenSortOrderAndCategory(
                comics = comics,
                comicCategory = preferences.comicCategory,
                sortOrder = preferences.sortOrder
            )
            ComicsUiModel(
                sortedAndFilteredComics,
                userPreferences = UserPreferences(
                    comicCategory = preferences.toUiComicCategory(),
                    sortOrder = preferences.toUiSortOrder()
                )
            )
        }.stateIn(
            viewModelScope, started = SharingStarted.WhileSubscribed(500),
            initialValue = ComicsUiModel.DefaultComicsUiModel
        )

    fun filterComicsByCategory(comicsCategory: ComicCategory) {
        viewModelScope.launch {
            protoPreferenceRepository.filterComicsByCategory(comicsCategory.toUserComicPreferenceCategory())
        }
    }

    fun sortComicsByRating(enabled: Boolean) {
        viewModelScope.launch {
            protoPreferenceRepository.enableSortByRating(enabled)
        }
    }

    fun sortComicsByDateAdded(enabled: Boolean) {
        viewModelScope.launch {
            protoPreferenceRepository.enableSortByDateAdded(enabled)
        }
    }

    fun disableSorting() {
        viewModelScope.launch {
            protoPreferenceRepository.disableSorting()
        }
    }

    fun resetSortOrderAndFilterOption() {
        viewModelScope.launch {
            disableSorting()
            protoPreferenceRepository.removeComicCategoryFilter()
        }
    }

    fun sortAndFilterComicsByGivenSortOrderAndCategory(
        comics: List<Comic>,
        comicCategory: UserComicPreference.ComicCategory,
        sortOrder: UserComicPreference.SortOrder
    ): List<Comic> {
        val filteredComics = when (comicCategory) {
            UserComicPreference.ComicCategory.UNSPECIFIED_CATEGORY, UserComicPreference.ComicCategory.UNRECOGNIZED -> comics
            UserComicPreference.ComicCategory.ALL -> comics
            UserComicPreference.ComicCategory.HORROR -> comics.filter { it.comicCategory == ComicCategory.HORROR }
            UserComicPreference.ComicCategory.FICTION -> comics.filter { it.comicCategory == ComicCategory.FICTION }
            UserComicPreference.ComicCategory.ACTION -> comics.filter { it.comicCategory == ComicCategory.ACTION }
        }
        return when (sortOrder) {
            UserComicPreference.SortOrder.UNSPECIFIED, UserComicPreference.SortOrder.UNRECOGNIZED -> filteredComics
            UserComicPreference.SortOrder.NONE -> filteredComics
            UserComicPreference.SortOrder.BY_RATING -> filteredComics.sortedByDescending { it.comicRating }
            UserComicPreference.SortOrder.BY_DATE_ADDED -> filteredComics.sortedBy { it.dateReleased }
            UserComicPreference.SortOrder.BY_NAME -> filteredComics.sortedBy { it.comicName }
        }
    }

    private fun ComicCategory.toUserComicPreferenceCategory(): UserComicPreference.ComicCategory {
        return when (this) {
            ComicCategory.ALL -> UserComicPreference.ComicCategory.ALL
            ComicCategory.HORROR -> UserComicPreference.ComicCategory.HORROR
            ComicCategory.FICTION -> UserComicPreference.ComicCategory.FICTION
            ComicCategory.ACTION -> UserComicPreference.ComicCategory.ACTION
        }
    }

    private fun UserComicPreference.toUiComicCategory(): ComicCategory {
        return when (comicCategory) {
            UserComicPreference.ComicCategory.ACTION -> ComicCategory.ACTION
            UserComicPreference.ComicCategory.FICTION -> ComicCategory.FICTION
            UserComicPreference.ComicCategory.UNSPECIFIED_CATEGORY, UserComicPreference.ComicCategory.UNRECOGNIZED -> ComicCategory.ALL
            UserComicPreference.ComicCategory.ALL -> ComicCategory.ALL
            UserComicPreference.ComicCategory.HORROR -> ComicCategory.HORROR
            else -> ComicCategory.ALL
        }
    }

    private fun UserComicPreference.toUiSortOrder(): SortOrder {
        return when (sortOrder) {
            UserComicPreference.SortOrder.UNSPECIFIED, UserComicPreference.SortOrder.UNRECOGNIZED -> SortOrder.NONE
            UserComicPreference.SortOrder.NONE -> SortOrder.NONE
            UserComicPreference.SortOrder.BY_RATING -> SortOrder.BY_RATING
            UserComicPreference.SortOrder.BY_DATE_ADDED -> SortOrder.BY_DATE_ADDED
            UserComicPreference.SortOrder.BY_NAME -> SortOrder.BY_NAME
            else -> SortOrder.NONE
        }
    }

    fun sortComicsByName(enabled: Boolean){
        viewModelScope.launch{
            protoPreferenceRepository.enableSortByName(enabled)
        }
    }

}

