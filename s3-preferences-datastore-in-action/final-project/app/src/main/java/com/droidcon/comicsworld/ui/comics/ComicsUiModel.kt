package com.droidcon.comicsworld.ui.comics

import com.droidcon.comicsworld.data.Comic
import com.droidcon.comicsworld.data.UserPreferences

data class ComicsUiModel(val comics: List<Comic>, val userPreferences: UserPreferences) {
    companion object {
        val DefaultComicsUiModel = ComicsUiModel(emptyList(), UserPreferences.DefaultUserPreferences)
    }
}
