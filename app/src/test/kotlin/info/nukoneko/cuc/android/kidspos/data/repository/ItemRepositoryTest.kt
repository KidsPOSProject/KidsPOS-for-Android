package info.nukoneko.cuc.android.kidspos.data.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.testutil.FakeAPIService
import info.nukoneko.cuc.android.kidspos.testutil.FakePreferencesDataStore
import info.nukoneko.cuc.android.kidspos.testutil.createItemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemRepositoryTest {

    private val cachedItemsKey = stringPreferencesKey("cached_items")

    private fun item(id: Int) = Item(
        id = id,
        barcode = "100100000$id",
        name = "item$id",
        price = id * 100,
        storeId = 1,
        genreId = 1
    )

    @Test
    fun refreshItemsReturnsFetchedItems() = runTest {
        val apiService = FakeAPIService()
        val items = listOf(item(1), item(2))
        apiService.fetchItemsHandler = { items }
        val repository = createItemRepository(apiService, StandardTestDispatcher(testScheduler))

        assertEquals(items, repository.refreshItems())
    }

    @Test
    fun refreshItemsStoresItemsIntoCache() = runTest {
        val apiService = FakeAPIService()
        val items = listOf(item(1), item(2), item(3))
        apiService.fetchItemsHandler = { items }
        val repository = createItemRepository(apiService, StandardTestDispatcher(testScheduler))

        repository.refreshItems()

        assertEquals(items, repository.getCachedItems())
        assertEquals(items, repository.cachedItems.first())
    }

    @Test
    fun cachedItemsSurviveLaterFetchFailure() = runTest {
        val apiService = FakeAPIService()
        val dataStore = FakePreferencesDataStore()
        val items = listOf(item(1))
        apiService.fetchItemsHandler = { items }
        val repository =
            createItemRepository(apiService, StandardTestDispatcher(testScheduler), dataStore)
        repository.refreshItems()

        apiService.fetchItemsHandler = { throw RuntimeException("network") }
        runCatching { repository.refreshItems() }

        assertEquals(items, repository.getCachedItems())
    }

    @Test
    fun cachedItemsAreEmptyBeforeFirstRefresh() = runTest {
        val apiService = FakeAPIService()
        val repository = createItemRepository(apiService, StandardTestDispatcher(testScheduler))

        assertTrue(repository.getCachedItems().isEmpty())
    }

    @Test
    fun brokenCacheFallsBackToEmptyList() = runTest {
        val apiService = FakeAPIService()
        val dataStore = FakePreferencesDataStore()
        dataStore.edit { it[cachedItemsKey] = "this is not json" }
        val repository =
            createItemRepository(apiService, StandardTestDispatcher(testScheduler), dataStore)

        assertTrue(repository.getCachedItems().isEmpty())
    }

    @Test
    fun refreshItemsOverwritesPreviousCache() = runTest {
        val apiService = FakeAPIService()
        val dataStore = FakePreferencesDataStore()
        apiService.fetchItemsHandler = { listOf(item(1), item(2)) }
        val repository =
            createItemRepository(apiService, StandardTestDispatcher(testScheduler), dataStore)
        repository.refreshItems()

        apiService.fetchItemsHandler = { listOf(item(3)) }
        repository.refreshItems()

        assertEquals(listOf(item(3)), repository.getCachedItems())
    }

    @Test
    fun getItemByBarcodeDelegatesToApi() = runTest {
        val apiService = FakeAPIService()
        apiService.getItemHandler = { barcode -> item(9).copy(barcode = barcode) }
        val repository = createItemRepository(apiService, StandardTestDispatcher(testScheduler))

        assertEquals("1001000001", repository.getItemByBarcode("1001000001").barcode)
    }

    @Test
    fun getItemByBarcodeReturnsCachedItemWithoutCallingApi() = runTest {
        val apiService = FakeAPIService()
        val dataStore = FakePreferencesDataStore()
        var apiCalls = 0
        apiService.getItemHandler = { barcode ->
            apiCalls++
            item(9).copy(barcode = barcode)
        }
        apiService.fetchItemsHandler = { listOf(item(1), item(2)) }
        val repository =
            createItemRepository(apiService, StandardTestDispatcher(testScheduler), dataStore)
        repository.refreshItems()

        assertEquals(item(2), repository.getItemByBarcode("1001000002"))
        assertEquals(0, apiCalls)
    }

    @Test
    fun getItemByBarcodeFallsBackToApiWhenBarcodeIsNotCached() = runTest {
        val apiService = FakeAPIService()
        val dataStore = FakePreferencesDataStore()
        var apiCalls = 0
        apiService.getItemHandler = { barcode ->
            apiCalls++
            item(9).copy(barcode = barcode)
        }
        apiService.fetchItemsHandler = { listOf(item(1)) }
        val repository =
            createItemRepository(apiService, StandardTestDispatcher(testScheduler), dataStore)
        repository.refreshItems()

        assertEquals("A01000008A", repository.getItemByBarcode("A01000008A").barcode)
        assertEquals(1, apiCalls)
    }
}
