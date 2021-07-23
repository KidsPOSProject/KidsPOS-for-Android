package info.nukoneko.cuc.android.kidspos.data.datasource

import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.ItemId
import info.nukoneko.cuc.android.kidspos.domain.entity.Sale
import info.nukoneko.cuc.android.kidspos.domain.entity.StoreId

interface SaleDatasource {
    suspend fun postSale(
        storeId: StoreId,
        staffBarcode: Barcode,
        deposit: Int,
        itemList: List<ItemId>
    ): Sale
}
