package info.nukoneko.cuc.android.kidspos.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.di.hilt.IoDispatcher
import info.nukoneko.cuc.android.kidspos.entity.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val apiService: APIService,
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    val cachedItems: Flow<List<Item>> = dataStore.data.map { prefs ->
        prefs[KEY_CACHED_ITEMS]?.let { decodeItems(it) } ?: emptyList()
    }

    suspend fun getItemByBarcode(barcode: String): Item = withContext(dispatcher) {
        getCachedItems().firstOrNull { it.barcode == barcode } ?: apiService.getItem(barcode)
    }

    suspend fun refreshItems(): List<Item> = withContext(dispatcher) {
        val items = apiService.fetchItems()
        dataStore.edit { prefs -> prefs[KEY_CACHED_ITEMS] = json.encodeToString(items) }
        items
    }

    suspend fun getCachedItems(): List<Item> = cachedItems.first()

    private fun decodeItems(raw: String): List<Item> = try {
        json.decodeFromString<List<Item>>(raw)
    } catch (e: Throwable) {
        Timber.e(e, "failed to decode cached items")
        emptyList()
    }

    private companion object {
        val KEY_CACHED_ITEMS = stringPreferencesKey("cached_items")
    }
}
