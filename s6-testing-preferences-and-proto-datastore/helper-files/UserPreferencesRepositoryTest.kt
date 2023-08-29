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
            testFile }

       userPreferencesRepository= UserPreferencesRepository(preferenceDataStore)
    }

    @Test
    fun defaultFilterAndSortOptionsAreReturnedAfterPreferenceFileCreation() = runTest{
        //TODO:
    }
    @Test
    fun testEnableAndDisableSortByRating() = runTest {
        //TODO:
    }
    @Test
    fun testEnableAndDisableSortByDateAdded() = runTest {
        //TODO:
    }
    @Test
    fun testDisableSorting() = runTest {
    	//TODO:
    }
    @Test
    fun testFilterByCategory() = runTest {
       //TODO:
    }
}
