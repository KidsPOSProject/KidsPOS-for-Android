package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.ServerStatus
import info.nukoneko.cuc.android.kidspos.entity.Store

interface APIService {
    suspend fun fetchStores(): List<Store>
    suspend fun createSale(storeId: Int, deposit: Int, itemIds: String): Sale
    suspend fun getItem(itemBarcode: String): Item
    suspend fun fetchItems(): List<Item>
    suspend fun getServerStatus(): ServerStatus

    companion object {
        const val SUPPORTED_API_VERSION = 1
    }
}
