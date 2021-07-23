package info.nukoneko.cuc.android.kidspos.domain.repository

import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.ItemId
import info.nukoneko.cuc.android.kidspos.domain.entity.Sale
import info.nukoneko.cuc.android.kidspos.domain.entity.StoreId

interface SaleRepository {
    suspend fun postSale(
        storeId: StoreId,
        staffBarcode: Barcode,
        deposit: Int,
        itemList: List<ItemId>
    ): Sale

    suspend fun fetchSale(barcode: Barcode): Sale
}
