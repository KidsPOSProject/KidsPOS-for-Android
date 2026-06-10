package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Store
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class StoreRepository(
    private val apiService: APIService,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun fetchStores(): List<Store> = withContext(dispatcher) {
        apiService.fetchStores()
    }
}
