package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.di.hilt.IoDispatcher
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaleRepository @Inject constructor(
    private val apiService: APIService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun createSale(
        storeId: Int,
        staffBarcode: String,
        deposit: Int,
        items: List<Item>
    ): Sale = withContext(dispatcher) {
        val itemIds = items.map { it.id.toString() }.joinToString(",")
        apiService.createSale(storeId, staffBarcode, deposit, itemIds)
    }
}
