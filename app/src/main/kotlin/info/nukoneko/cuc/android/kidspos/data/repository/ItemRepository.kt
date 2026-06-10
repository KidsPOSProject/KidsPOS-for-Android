package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ItemRepository(
    private val apiService: APIService,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getItemByBarcode(barcode: String): Item = withContext(dispatcher) {
        apiService.getItem(barcode)
    }
}
