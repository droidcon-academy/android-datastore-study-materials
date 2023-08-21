package com.droidcon.comicsworld.data

import java.util.*

data class Comic(
    val comicName: String,
    val comicThumbnail: Int,
    val comicRating: Double,
    val dateReleased: Date,
    val comicCategory: ComicCategory
)
