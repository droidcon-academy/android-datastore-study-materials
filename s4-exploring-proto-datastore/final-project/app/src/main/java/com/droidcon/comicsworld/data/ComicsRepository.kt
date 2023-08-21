package com.droidcon.comicsworld.data

import com.droidcon.comicsworld.R
import com.droidcon.comicsworld.utils.parseStringToDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface ComicsRepository {
    fun getComics(): Flow<List<Comic>>
}

class ComicsRepositoryDelegate @Inject constructor() : ComicsRepository {
    private val comics = listOf(
        Comic(
            "Predator(2023-)", R.drawable.predator,
            4.0, "2023-8-03".parseStringToDate(), ComicCategory.ACTION
        ),
        Comic(
            "Amazing Spider-Man (2022-)", R.drawable.spiderman,
            5.0, "2022-07-03".parseStringToDate(), ComicCategory.FICTION
        ),
        Comic(
            "X-men(2021-)", R.drawable.x_men,
            6.0, "2021-04-01".parseStringToDate(), ComicCategory.HORROR
        ),
        Comic(
            "X-23:Deadly Regenesis(2023-)", R.drawable.x_23,
            7.0, "2023-06-07".parseStringToDate(), ComicCategory.FICTION
        ),
        Comic(
            "New Mutants Lethal Legion(2022-)", R.drawable.new_mutants,
            5.0, "2022-10-05".parseStringToDate(), ComicCategory.ACTION
        ),
        Comic(
            "Two Graves (2022-)", R.drawable.two_graves,
            6.0, "2022-12-01".parseStringToDate(), ComicCategory.HORROR
        ),
        Comic(
            "Spawn(1992-)", R.drawable.spawn,
            7.0, "1992-07-03".parseStringToDate(), ComicCategory.ACTION,
        )
    )

    override
    fun getComics(): Flow<List<Comic>> {
        return flowOf(comics)
    }
}