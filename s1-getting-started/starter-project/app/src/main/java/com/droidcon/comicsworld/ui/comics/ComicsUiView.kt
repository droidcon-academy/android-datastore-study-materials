package com.droidcon.comicsworld.ui.comics

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.droidcon.comicsworld.data.Comic
import com.droidcon.comicsworld.data.ComicCategory
import com.droidcon.comicsworld.data.SortOrder
import com.droidcon.comicsworld.data.UserPreferences
import com.droidcon.comicsworld.utils.formatToString
import com.droidcon.comicsworld.utils.getTitleFromSortOrderOption
import com.droidcon.comicsworld.utils.parseStringToDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicsScreenView(
    modifier: Modifier = Modifier,
    comics: List<Comic>,
    userPreferences: UserPreferences,
    filterComicsByCategory: (ComicCategory) -> Unit,
    disableSorting: () -> Unit,
    sortComicsByRating: (Boolean) -> Unit,
    sortComicsByDateAdded: (Boolean) -> Unit,
    resetSortOrderAndFilterOption: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showFilterMenu by rememberSaveable { mutableStateOf(false) }
    var showSortMenu by rememberSaveable { mutableStateOf(false) }

    val closeBottomSheet: () -> Unit = {
        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            if (!bottomSheetState.isVisible) {
                isBottomSheetOpen = false
            }
        }
    }

    Scaffold(modifier = modifier.fillMaxSize(),
        topBar = {
            ComicsScreenAppBar(
                onFilterButtonClicked = {
                    showFilterMenu = true
                    showSortMenu = false
                    isBottomSheetOpen = true
                },
                onSortButtonClicked = {
                    showSortMenu = true
                    showFilterMenu = false
                    isBottomSheetOpen = true
                })
        })
    {
        if (isBottomSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isBottomSheetOpen = false },
                sheetState = bottomSheetState,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            ) {
                BottomSheetContent(showFilterMenu = showFilterMenu,
                    sortOrder = userPreferences.sortOrder,
                    comicCategory = userPreferences.comicCategory,
                    showSortMenu = showSortMenu,
                    applyFilterClicked = {
                        closeBottomSheet()
                    },
                    resetFilterAndSortOptionClicked = {
                        resetSortOrderAndFilterOption()
                        closeBottomSheet()
                    },
                    onComicCategoryFilterChanged = { category ->
                        filterComicsByCategory(category)
                    },
                    onSortOrderChanged = { sortOrder ->
                        when (sortOrder) {
                            SortOrder.BY_RATING -> sortComicsByRating(true)
                            SortOrder.BY_DATE_ADDED -> sortComicsByDateAdded(true)
                            else -> disableSorting()
                        }
                    },
                    modifier = Modifier.padding(bottom = 32.dp))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            contentPadding = it,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(comics) { comic ->
                ComicItemComponent(comic = comic)
            }
        }
    }
}

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    showFilterMenu: Boolean,
    showSortMenu: Boolean,
    sortOrder: SortOrder,
    comicCategory: ComicCategory,
    applyFilterClicked: () -> Unit,
    resetFilterAndSortOptionClicked: () -> Unit,
    onComicCategoryFilterChanged: (ComicCategory) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit
) {

    val comicCategories by remember { mutableStateOf(ComicCategory.values()) }
    val comicSortOrders by remember { mutableStateOf(SortOrder.values()) }
    var categorySelected by remember(comicCategory) { mutableStateOf(comicCategory) }
    var selectedSortOrder by remember(sortOrder) { mutableStateOf(sortOrder) }


    LazyColumn(
        modifier = modifier.fillMaxWidth().padding(bottom=32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    modifier = Modifier.testTag(ResetButtonTestTag),
                    onClick = resetFilterAndSortOptionClicked
                ) {
                    Text(
                        text = stringResource(id = com.droidcon.comicsworld.R.string.reset_filter_or_sort),
                        textAlign = TextAlign.Center,
                        color = Color.Red
                    )
                }

                TextButton(
                    modifier = Modifier.testTag(ApplyButtonTestTag),
                    onClick = applyFilterClicked
                ) {
                    Text(
                        text = stringResource(id = com.droidcon.comicsworld.R.string.apply_filter),
                        textAlign = TextAlign.Center
                    )
                }

            }

        }

        if (showFilterMenu) {
            item { Spacer(modifier = Modifier.height(5.dp)) }
            item {
                Text(
                    text = stringResource(id = com.droidcon.comicsworld.R.string.filter_comics),
                    fontWeight = FontWeight.Medium
                )
            }
            item { Spacer(modifier = Modifier.height(5.dp)) }

            items(comicCategories) { item: ComicCategory ->
                ComicSortAndFilterRowItem(
                    category = item.name,
                    isChecked = categorySelected == item,
                    onCheckedChanged = { checked ->
                        if (checked) {
                            categorySelected = item
                            onComicCategoryFilterChanged(categorySelected)
                        }
                    })
            }
            item { Spacer(modifier = Modifier.height(5.dp)) }

        } else if (showSortMenu) {
            item { Spacer(modifier = Modifier.height(5.dp)) }
            item {
                Text(
                    text = stringResource(id = com.droidcon.comicsworld.R.string.sort_comics),
                    fontWeight = FontWeight.Medium
                )
            }
            item { Spacer(modifier = Modifier.height(5.dp)) }
            items(comicSortOrders) { item: SortOrder ->
                ComicSortAndFilterRowItem(modifier = Modifier,
                    category = item.getTitleFromSortOrderOption(),
                    isChecked = selectedSortOrder == item,
                    onCheckedChanged = { checked ->
                        if (checked) {
                            selectedSortOrder = item
                            onSortOrderChanged(selectedSortOrder)
                        }
                    })
            }
            item { Spacer(modifier = Modifier.height(5.dp)) }
        }
    }
}

@Composable
fun ComicSortAndFilterRowItem(
    modifier: Modifier = Modifier,
    category: String, isChecked: Boolean, onCheckedChanged: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = category)
        Spacer(modifier = Modifier.width(20.dp))
        Switch(
            checked = isChecked,
            modifier = Modifier.testTag(category),
            onCheckedChange = onCheckedChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicsScreenAppBar(
    modifier: Modifier = Modifier, onFilterButtonClicked: () -> Unit,
    onSortButtonClicked: () -> Unit
) {
    val view = LocalView.current
    TopAppBar(modifier = modifier,
        title = { Text(text = stringResource(id = com.droidcon.comicsworld.R.string.app_name)) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        actions = {
            IconButton(modifier = Modifier.testTag(OpenFilterBottomSheetTestTag), onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onFilterButtonClicked()
            }) {
                Icon(
                    painter = painterResource(id = com.droidcon.comicsworld.R.drawable.filter_icon),
                    contentDescription = stringResource(id = com.droidcon.comicsworld.R.string.filter_comics)
                )
            }
            IconButton(modifier = Modifier.testTag(OpenSortBottomSheetTestTag),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onSortButtonClicked()
                }) {
                Icon(
                    painter = painterResource(id = com.droidcon.comicsworld.R.drawable.sort_icon),
                    contentDescription = stringResource(id = com.droidcon.comicsworld.R.string.sort_comics)
                )
            }
        })
}

@Composable
fun ComicItemComponent(modifier: Modifier = Modifier, comic: Comic) {
    Column(
        modifier = modifier.width(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = comic.comicThumbnail),
            modifier = Modifier
                .size(150.dp, 250.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(text = comic.comicName, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(5.dp))

        Text(text = "${comic.comicRating}/10", textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(5.dp))

        Text(text = comic.comicCategory.name, textAlign = TextAlign.Center)


        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = comic.dateReleased.formatToString(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light
        )
    }
}

const val ResetButtonTestTag = "resetButton"
const val OpenSortBottomSheetTestTag = "showSortBottomSheet"
const val ApplyButtonTestTag = "applyButton"
const val OpenFilterBottomSheetTestTag = "showFilterBottomSheet"

@Composable
@Preview
fun ComicItemPreview() {
    val comic = Comic(
        comicName = "Spawn(1992-)", comicThumbnail = com.droidcon.comicsworld.R.drawable.spawn,
        7.0, "1992-07-03".parseStringToDate(), ComicCategory.ACTION
    )
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ComicItemComponent(comic = comic)
    }

}