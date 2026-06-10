package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store

interface APIService {
    suspend fun fetchStores(): List<Store>
    suspend fun createSale(storeId: Int, staffBarcode: String, deposit: Int, itemIds: String): Sale
    suspend fun getItem(itemBarcode: String): Item
    suspend fun getStaff(staffBarcode: String): Staff
    suspend fun getStatus(): Any
}
