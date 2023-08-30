package com.droidcon.comicsworld

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.droidcon.comicsworld.data.ComicCategory
import com.droidcon.comicsworld.data.SortOrder
import com.droidcon.comicsworld.data.pref.UserPreferencesRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File


@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesRepositoryTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()


    private lateinit var preferenceDataStore:DataStore<Preferences>
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var testFile:File
    private lateinit var dataStoreScope: TestScope


    @Before
    fun setUp(){
        testFile = temporaryFolder.newFile("test-preferences-file.preferences_pb")
        dataStoreScope = TestScope(UnconfinedTestDispatcher())
        preferenceDataStore = PreferenceDataStoreFactory.create(scope = dataStoreScope){
            testFile
        }
        userPreferencesRepository = UserPreferencesRepository(preferenceDataStore)
    }

    @Test
    fun defaultFilterAndSortOptionsAreReturnedAfterPreferenceFileCreation() = runTest{
        val userPreferences = userPreferencesRepository.getUserPreferences().firstOrNull()
        assertEquals(userPreferences?.sortOrder?.name, SortOrder.NONE.name)
        assertEquals(userPreferences?.comicCategory?.name, ComicCategory.ALL.name)
    }
    @Test
    fun testEnableAndDisableSortByRating() = runTest {
        val expectedSortOrder = SortOrder.BY_RATING.name
        userPreferencesRepository.enableSortByRating(true)
        val userPreferences = userPreferencesRepository.getUserPreferences().firstOrNull()
        assertEquals(userPreferences?.sortOrder?.name, expectedSortOrder)
        userPreferencesRepository.enableSortByRating(false)
        val updatedUserPreferences = userPreferencesRepository.getUserPreferences().firstOrNull()
        assertEquals(updatedUserPreferences?.sortOrder?.name, SortOrder.NONE.name)
    }
    @Test
    fun testEnableAndDisableSortByDateAdded() = runTest {
        userPreferencesRepository.enableSortByDateAdded(true)
        val userPreferences = userPreferencesRepository.getUserPreferences().firstOrNull()
        assertEquals(userPreferences?.sortOrder?.name, SortOrder.BY_DATE_ADDED.name)
        userPreferencesRepository.enableSortByDateAdded(false)
        val updatedUserPreferences = userPreferencesRepository.getUserPreferences().firstOrNull()
        assertEquals(updatedUserPreferences?.sortOrder?.name, SortOrder.NONE.name)
    }
    @Test
    fun testDisableSorting() = runTest {
    	userPreferencesRepository.enableSortByDateAdded(true)
        val currentSortOrder = userPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.name
        assertEquals(currentSortOrder, SortOrder.BY_DATE_ADDED.name)
        userPreferencesRepository.disableSorting()
        userPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.name?.let{
            assertEquals(it, SortOrder.NONE.name)
        }
    }
    @Test
    fun testFilterByCategory() = runTest {
       userPreferencesRepository.filterComicsByCategory(ComicCategory.ACTION)
        userPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.name?.let{
            assertEquals(it, ComicCategory.ACTION.name)
        }
        userPreferencesRepository.filterComicsByCategory(ComicCategory.HORROR)
        userPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.name?.let{
            assertEquals(it, ComicCategory.HORROR.name)
        }
        userPreferencesRepository.filterComicsByCategory(ComicCategory.FICTION)
        userPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.name?.let{
            assertEquals(it, ComicCategory.FICTION.name)
        }
        userPreferencesRepository.removeComicCategoryFilter()
        userPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.name?.let{
            assertEquals(it, ComicCategory.ALL.name)
        }
    }
}
