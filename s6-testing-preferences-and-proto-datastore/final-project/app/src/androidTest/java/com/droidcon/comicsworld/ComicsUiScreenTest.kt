package com.droidcon.comicsworld

import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.droidcon.comicsworld.data.ComicCategory
import com.droidcon.comicsworld.data.ComicsRepository
import com.droidcon.comicsworld.data.ComicsRepositoryDelegate
import com.droidcon.comicsworld.data.SortOrder
import com.droidcon.comicsworld.data.pref.UserPreferencesRepository
import com.droidcon.comicsworld.ui.comics.*
import com.droidcon.comicsworld.ui.comics.preference.ComicsPreferencesScreen
import com.droidcon.comicsworld.ui.comics.preference.PreferencesViewModel
import com.droidcon.comicsworld.utils.getTitleFromSortOrderOption
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ComicsUiScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    @get:Rule
    val temporaryFolder = TemporaryFolder()


    private lateinit var preferenceDataStore: DataStore<Preferences>
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var comicsRepository: ComicsRepository
    private lateinit var testFile: File
    private lateinit var dataStoreScope: TestScope
    private lateinit var preferenceViewModel:PreferencesViewModel
    @Before
    fun setUp(){
        testFile = temporaryFolder.newFile("test-preferences-file.preferences_pb")
        dataStoreScope = TestScope(UnconfinedTestDispatcher())
        preferenceDataStore = PreferenceDataStoreFactory.create(scope = dataStoreScope){
            testFile }
        userPreferencesRepository= UserPreferencesRepository(preferenceDataStore)
        comicsRepository= ComicsRepositoryDelegate()
        preferenceViewModel = PreferencesViewModel(comicRepository = comicsRepository,userPreferencesRepository)
    }
    @Test
    fun testEnableSortOrderByRating(){
        var uiState = mutableStateOf(ComicsUiModel.DefaultComicsUiModel)
        composeTestRule.activity.setContent {
            uiState= preferenceViewModel.comicsUiModel.collectAsState() as MutableState<ComicsUiModel>
            ComicsPreferencesScreen(preferenceViewModel)
        }
        composeTestRule.onNodeWithTag(OpenSortBottomSheetTestTag).performClick()

        composeTestRule.onNodeWithTag(SortOrder.NONE.getTitleFromSortOrderOption()).performClick()

        composeTestRule.onNodeWithTag(SortOrder.BY_RATING.getTitleFromSortOrderOption())
            .assertIsOff()
            .performClick()
            .assertIsOn()

        composeTestRule.onNodeWithTag(ApplyButtonTestTag).performClick()

        runBlocking {
           val updatedSortOrder= withTimeout(5000){ uiState.value }.userPreferences.sortOrder
            assertEquals(updatedSortOrder.name, SortOrder.BY_RATING.name)
        }
    }
    @Test
    fun testEnableSortOrderByDateAdded(){
        var uiState = mutableStateOf(ComicsUiModel.DefaultComicsUiModel)
        composeTestRule.activity.setContent {
            uiState= preferenceViewModel.comicsUiModel.collectAsState() as MutableState<ComicsUiModel>
            ComicsPreferencesScreen(preferenceViewModel)
        }
        composeTestRule.onNodeWithTag(OpenSortBottomSheetTestTag).performClick()

        composeTestRule.onNodeWithTag(SortOrder.NONE.getTitleFromSortOrderOption()).performClick()

        composeTestRule.onNodeWithTag(SortOrder.BY_DATE_ADDED.getTitleFromSortOrderOption())
            .assertIsOff()
            .performClick()
            .assertIsOn()

        composeTestRule.onNodeWithTag(ApplyButtonTestTag).performClick()

        runBlocking {
            val updatedSortOrder = withTimeout(5000){uiState.value}.userPreferences.sortOrder
            assertEquals(updatedSortOrder.name, SortOrder.BY_DATE_ADDED.name)
        }
    }
    @Test
    fun testFilteringComicsBasedOnCategory(){
        var uiState = mutableStateOf(ComicsUiModel.DefaultComicsUiModel)
        composeTestRule.activity.setContent {
            uiState= preferenceViewModel.comicsUiModel.collectAsState() as MutableState<ComicsUiModel>
            ComicsPreferencesScreen(preferenceViewModel)
        }
        composeTestRule.onNodeWithTag(OpenFilterBottomSheetTestTag).performClick()

        composeTestRule.onNodeWithTag(ComicCategory.HORROR.name).performClick()

        composeTestRule.onNodeWithTag(ApplyButtonTestTag).performClick()

        runBlocking {
            val updatedComicCategory = withTimeout(5000){uiState.value}.userPreferences.comicCategory
            assertEquals(updatedComicCategory.name, ComicCategory.HORROR.name)
        }
    }
    @Test
    fun testDisableSortingAndRemoveComicFilter(){
        var uiState = mutableStateOf(ComicsUiModel.DefaultComicsUiModel)
        composeTestRule.activity.setContent {
            uiState= preferenceViewModel.comicsUiModel.collectAsState() as MutableState<ComicsUiModel>
            ComicsPreferencesScreen(preferenceViewModel)
        }
        composeTestRule.onNodeWithTag(OpenSortBottomSheetTestTag).performClick()

        composeTestRule.onNodeWithTag(SortOrder.NONE.getTitleFromSortOrderOption()).performClick()

        composeTestRule.onNodeWithTag(ResetButtonTestTag).performClick()

        runBlocking {
            val updatedSortOrder = withTimeout(5000){uiState.value}.userPreferences.sortOrder
            assertEquals(updatedSortOrder.name, SortOrder.NONE.name)
        }
    }
}