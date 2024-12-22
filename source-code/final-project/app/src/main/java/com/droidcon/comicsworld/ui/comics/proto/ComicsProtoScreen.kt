package com.droidcon.comicsworld.ui.comics.proto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droidcon.comicsworld.ui.comics.ComicsScreenView


@Composable
fun ComicsProtoScreen(comicsProtoViewModel: ProtoViewModel = viewModel()) {
    val comicsUiModel by comicsProtoViewModel.comicsUiModel.collectAsState()
    ComicsScreenView(
        comics = comicsUiModel.comics,
        userPreferences = comicsUiModel.userPreferences,
        filterComicsByCategory = comicsProtoViewModel::filterComicsByCategory,
        disableSorting = comicsProtoViewModel::disableSorting,
        sortComicsByRating = comicsProtoViewModel::sortComicsByRating,
        sortComicsByDateAdded = comicsProtoViewModel::sortComicsByDateAdded,
        sortComicsByName = comicsProtoViewModel::sortComicsByName,
        resetSortOrderAndFilterOption = comicsProtoViewModel::resetSortOrderAndFilterOption
    )
}