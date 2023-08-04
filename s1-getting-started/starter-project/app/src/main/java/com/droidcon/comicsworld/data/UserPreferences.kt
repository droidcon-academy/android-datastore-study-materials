package com.droidcon.comicsworld.data

data class UserPreferences(val comicCategory: ComicCategory, val sortOrder: SortOrder) {
    companion object {
        val DefaultUserPreferences = UserPreferences(ComicCategory.ALL, SortOrder.NONE)
    }
}
