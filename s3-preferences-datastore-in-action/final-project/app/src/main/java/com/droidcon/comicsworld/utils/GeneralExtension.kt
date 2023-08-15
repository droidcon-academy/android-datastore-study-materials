package com.droidcon.comicsworld.utils

import com.droidcon.comicsworld.data.SortOrder

fun SortOrder.getTitleFromSortOrderOption(): String {
    return when (this) {
        SortOrder.NONE -> "Don't apply any sort order"
        SortOrder.BY_RATING -> "Sort by comics rating"
        SortOrder.BY_DATE_ADDED -> "Sort by date added"
    }
}