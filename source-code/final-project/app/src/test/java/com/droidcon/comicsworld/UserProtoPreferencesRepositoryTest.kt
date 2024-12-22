package com.droidcon.comicsworld

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.droidcon.comicsworld.UserComicPreference.ComicCategory
import com.droidcon.comicsworld.UserComicPreference.SortOrder
import com.droidcon.comicsworld.data.proto.UserPreferencesSerializer
import com.droidcon.comicsworld.data.proto.UserProtoPreferencesRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class UserProtoPreferencesRepositoryTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var protoDataStore:DataStore<UserComicPreference>
    private lateinit var userProtoPreferencesRepository: UserProtoPreferencesRepository
    private lateinit var testFile: File
    private lateinit var dataStoreScope: TestScope

    @Before
    fun setUp(){
        testFile = temporaryFolder.newFile("test-proto-preferences-file.pb")
        dataStoreScope = TestScope(UnconfinedTestDispatcher())
        protoDataStore=DataStoreFactory.create(serializer = UserPreferencesSerializer){ testFile }
        userProtoPreferencesRepository= UserProtoPreferencesRepository(protoDataStore)
    }
    @Test
    fun defaultFilterAndSortOptionsAreReturnedAfterProtoPreferenceFileCreation() = runTest {
        val userPreferences = userProtoPreferencesRepository.getUserPreferences().firstOrNull()
        assertEquals(userPreferences?.sortOrder?.name, SortOrder.UNSPECIFIED.name)
        assertEquals(userPreferences?.comicCategory?.name,ComicCategory.UNSPECIFIED_CATEGORY.name)
    }
    @Test
    fun testEnableAndDisableSortByRating() = runTest {
        userProtoPreferencesRepository.enableSortByRating(true)
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.let {
            assertEquals(it.name, SortOrder.BY_RATING.name)
        }
        userProtoPreferencesRepository.enableSortByRating(false)
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.let {
            assertEquals(it.name, SortOrder.NONE.name)
        }
    }
    @Test
    fun testEnableAndDisableSortByDateAdded() = runTest {
        userProtoPreferencesRepository.enableSortByDateAdded(true)
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.let {
            assertEquals(it.name, SortOrder.BY_DATE_ADDED.name)
        }
        userProtoPreferencesRepository.enableSortByDateAdded(false)
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.let {
            assertEquals(it.name, SortOrder.NONE.name)
        }
    }

    @Test
    fun testDisableSorting() = runTest {
        userProtoPreferencesRepository.enableSortByDateAdded(true)
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.let {
            assertEquals(it.name, SortOrder.BY_DATE_ADDED.name)
        }
        userProtoPreferencesRepository.disableSorting()
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.let {
            assertEquals(it.name, SortOrder.NONE.name)
        }
    }

    @Test
    fun testFilerByCategory() = runTest {
        userProtoPreferencesRepository.filterComicsByCategory(ComicCategory.ACTION)
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.let {
            assertEquals(it.name, ComicCategory.ACTION.name)
        }

        userProtoPreferencesRepository.filterComicsByCategory(ComicCategory.HORROR)
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.let {
            assertEquals(it.name, ComicCategory.HORROR.name)
        }

        userProtoPreferencesRepository.filterComicsByCategory(ComicCategory.FICTION)
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.let {
            assertEquals(it.name, ComicCategory.FICTION.name)
        }
        userProtoPreferencesRepository.removeComicCategoryFilter()
        userProtoPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.let {
            assertEquals(it.name, ComicCategory.ALL.name)
        }
    }

}