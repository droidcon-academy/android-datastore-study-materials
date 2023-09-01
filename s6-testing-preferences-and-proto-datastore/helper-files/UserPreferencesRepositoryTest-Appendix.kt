@Test
    fun testDisableSorting() = runTest {
        userPreferencesRepository.enableSortByDateAdded(true)
        val currentSortOrder=userPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.name

        assertEquals(currentSortOrder, SortOrder.BY_DATE_ADDED.name)
        userPreferencesRepository.disableSorting()

        userPreferencesRepository.getUserPreferences().firstOrNull()?.sortOrder?.name?.let {
            assertEquals(it, SortOrder.NONE.name) }
    }
    
    @Test
    fun testFilterByCategory() = runTest {
        userPreferencesRepository.filterComicsByCategory(ComicCategory.ACTION)
        userPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.name?.let {
            assertEquals(it, ComicCategory.ACTION.name)
        }
        userPreferencesRepository.filterComicsByCategory(ComicCategory.HORROR)
        userPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.name?.let {
            assertEquals(it, ComicCategory.HORROR.name)
        }

        userPreferencesRepository.filterComicsByCategory(ComicCategory.FICTION)
        userPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.name?.let {
            assertEquals(it, ComicCategory.FICTION.name)
        }

        userPreferencesRepository.removeComicCategoryFilter()
        userPreferencesRepository.getUserPreferences().firstOrNull()?.comicCategory?.name?.let {
            assertEquals(it, ComicCategory.ALL.name)
        }
    }
