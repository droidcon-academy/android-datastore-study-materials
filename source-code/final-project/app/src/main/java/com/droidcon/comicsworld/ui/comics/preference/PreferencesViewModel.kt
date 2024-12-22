package com.droidcon.comicsworld.ui.comics.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.comicsworld.data.Comic
import com.droidcon.comicsworld.data.ComicCategory
import com.droidcon.comicsworld.data.ComicsRepository
import com.droidcon.comicsworld.data.SortOrder
import com.droidcon.comicsworld.data.pref.UserPreferencesRepository
import com.droidcon.comicsworld.ui.comics.ComicsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val comicRepository: ComicsRepository,
    private val preferenceRepository: UserPreferencesRepository
) : ViewModel() {

    private val userPreferences = preferenceRepository.getUserPreferences()

    val comicsUiModel: StateFlow<ComicsUiModel>
        get() = combine(userPreferences, comicRepository.getComics()) { preferences, comics ->
            val sortedAndFilteredComics = sortAndFilterComicsByGivenSortOrderAndCategory(
                comics,
                preferences.comicCategory,
                preferences.sortOrder
            )
            ComicsUiModel(sortedAndFilteredComics, userPreferences = preferences)
        }.stateIn(
            viewModelScope, started = SharingStarted.WhileSubscribed(500),
            initialValue = ComicsUiModel.DefaultComicsUiModel
        )

    fun sortAndFilterComicsByGivenSortOrderAndCategory(
        comics: List<Comic>,
        category: ComicCategory,
        sortOrder: SortOrder
    ): List<Comic> {
        val filteredComics = when (category) {
            ComicCategory.ALL -> comics
            ComicCategory.HORROR -> comics.filter { it.comicCategory == ComicCategory.HORROR }
            ComicCategory.FICTION -> comics.filter { it.comicCategory == ComicCategory.FICTION }
            ComicCategory.ACTION -> comics.filter { it.comicCategory == ComicCategory.ACTION }
        }
        // sort using the given sort order
        return when (sortOrder) {
            SortOrder.NONE -> filteredComics
            SortOrder.BY_RATING -> filteredComics.sortedByDescending { it.comicRating }
            SortOrder.BY_DATE_ADDED -> filteredComics.sortedBy { it.dateReleased }
            SortOrder.BY_NAME -> filteredComics.sortedBy { it.comicName }
        }
    }

    fun filterComicsByCategory(comicsCategory: ComicCategory) {
        viewModelScope.launch {
            preferenceRepository.filterComicsByCategory(comicsCategory)
        }
    }

    fun sortComicsByRating(enabled: Boolean) {
        viewModelScope.launch {
            preferenceRepository.enableSortByRating(enabled)
        }
    }

    fun sortComicsByDateAdded(enabled: Boolean) {
        viewModelScope.launch {
            preferenceRepository.enableSortByDateAdded(enabled)
        }
    }

    fun sortComicsByName(enabled: Boolean) {
        viewModelScope.launch {
            preferenceRepository.enableSortByName(enabled)
        }
    }

    fun disableSorting() {
        viewModelScope.launch {
            preferenceRepository.disableSorting()
        }
    }

    fun resetSortOrderAndFilterOption() {
        viewModelScope.launch {
            disableSorting()
            preferenceRepository.removeComicCategoryFilter()
        }
    }

}