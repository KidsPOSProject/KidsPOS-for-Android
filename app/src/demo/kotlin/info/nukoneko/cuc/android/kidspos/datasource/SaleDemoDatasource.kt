package info.nukoneko.cuc.android.kidspos.datasource

import info.nukoneko.cuc.android.kidspos.data.datasource.SaleDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.*
import java.util.*

class SaleDemoDatasource : SaleDatasource {
    override suspend fun postSale(
        storeId: StoreId,
        staffBarcode: Barcode,
        deposit: Int,
        itemList: List<ItemId>
    ) = Sale(
        id = SaleId(1),
        barcode = Barcode(""),
        createdAt = Date(),
        points = itemList.size,
        price = Price(deposit),
        items = itemList.map { it.value }.joinToString(","),
        storeId = storeId,
        staffId = StaffId(1)
    )
}
