package com.droidcon.comicsworld.ui.comics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.comicsworld.data.Comic
import com.droidcon.comicsworld.data.ComicCategory
import com.droidcon.comicsworld.data.ComicsRepository
import com.droidcon.comicsworld.data.SortOrder
import com.droidcon.comicsworld.data.UserPreferences.Companion.DefaultUserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class ComicsViewModel @Inject constructor(comicRepository: ComicsRepository) : ViewModel() {

    private val _userPreference = MutableStateFlow(DefaultUserPreferences)
    private val currentSortOrder: SortOrder
        get() = _userPreference.value.sortOrder

    private val _comicsUiModel = combine(
        comicRepository.getComics(),
        _userPreference
    ) { comics, userPreference ->
        val sortedAndFilteredComics = sortAndFilterComicsByGivenSortOrderAndCategory(
            comics,
            category = userPreference.comicCategory,
            sortOrder = userPreference.sortOrder
        )
        return@combine ComicsUiModel(sortedAndFilteredComics, userPreference)
    }

    val comicsUiModel: StateFlow<ComicsUiModel>
        get() = _comicsUiModel.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
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
        _userPreference.update { pref ->
            pref.copy(comicCategory = comicsCategory)
        }
    }

    fun disabledSorting() {
        _userPreference.update { pref ->
            pref.copy(sortOrder = SortOrder.NONE)
        }
    }


    fun sortComicsByRating(enabled: Boolean) {
        val newSortOrder = if (enabled) {
            SortOrder.BY_RATING
        } else currentSortOrder
        _userPreference.update { pref ->
            pref.copy(sortOrder = newSortOrder)
        }
    }

    fun sortComicsByDateAdded(enabled: Boolean) {
        val newSortOrder = if (enabled) {
            SortOrder.BY_DATE_ADDED
        } else currentSortOrder
        _userPreference.update { pref ->
            pref.copy(sortOrder = newSortOrder)
        }
    }

    fun resetSortOrderAndFilterOption() {
        val newSortOrder = SortOrder.NONE
        val newCategoryFilterOption = ComicCategory.ALL
        _userPreference.update { pref ->
            pref.copy(comicCategory = newCategoryFilterOption, sortOrder = newSortOrder)
        }
    }

}