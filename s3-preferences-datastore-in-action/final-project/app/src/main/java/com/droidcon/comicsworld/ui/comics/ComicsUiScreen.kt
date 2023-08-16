package com.droidcon.comicsworld.ui.comics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun ComicsUiScreen(comicsViewModel: ComicsViewModel = viewModel()) {
    val comicsUiModel by comicsViewModel.comicsUiModel.collectAsState()
    ComicsScreenView(
        comics = comicsUiModel.comics,
        userPreferences = comicsUiModel.userPreferences,
        filterComicsByCategory = comicsViewModel::filterComicsByCategory,
        disableSorting = comicsViewModel::disabledSorting,
        sortComicsByRating = comicsViewModel::sortComicsByRating,
        sortComicsByDateAdded = comicsViewModel::sortComicsByDateAdded,
        resetSortOrderAndFilterOption = comicsViewModel::resetSortOrderAndFilterOption,
        sortComicsByName = {}
        )
}

