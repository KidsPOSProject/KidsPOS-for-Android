package info.nukoneko.cuc.android.kidspos.data.datasourceimpl

import info.nukoneko.cuc.android.kidspos.data.api.APIService
import info.nukoneko.cuc.android.kidspos.data.datasource.SaleDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.ItemId
import info.nukoneko.cuc.android.kidspos.domain.entity.Sale
import info.nukoneko.cuc.android.kidspos.domain.entity.StoreId

class SaleRestDatasource(private val api: APIService) : SaleDatasource {
    override suspend fun postSale(
        storeId: StoreId,
        staffBarcode: Barcode,
        deposit: Int,
        itemList: List<ItemId>
    ): Sale {
        return api.createSale(
            storeId = storeId.value,
            staffBarcode = staffBarcode.value,
            deposit = deposit,
            itemIds = itemList.map { it.value }.joinToString(",")
        )
    }
}
