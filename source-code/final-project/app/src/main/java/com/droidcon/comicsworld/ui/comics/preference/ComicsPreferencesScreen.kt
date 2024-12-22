package com.droidcon.comicsworld.ui.comics.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droidcon.comicsworld.ui.comics.ComicsScreenView

@Composable
fun ComicsPreferencesScreen(comicsPreferencesViewModel: PreferencesViewModel = viewModel()) {
    val comicsUiModel by comicsPreferencesViewModel.comicsUiModel.collectAsState()

    ComicsScreenView(
        comics = comicsUiModel.comics,
        userPreferences = comicsUiModel.userPreferences,
        filterComicsByCategory = comicsPreferencesViewModel::filterComicsByCategory,
        disableSorting = comicsPreferencesViewModel::disableSorting,
        sortComicsByRating = comicsPreferencesViewModel::sortComicsByRating,
        sortComicsByDateAdded = comicsPreferencesViewModel::sortComicsByDateAdded,
        sortComicsByName = comicsPreferencesViewModel::sortComicsByName,
        resetSortOrderAndFilterOption = comicsPreferencesViewModel::resetSortOrderAndFilterOption
    )
}