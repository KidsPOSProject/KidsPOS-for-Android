package info.nukoneko.cuc.android.kidspos.datasource

import info.nukoneko.cuc.android.kidspos.data.datasource.ItemDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.*

class ItemDemoDatasource : ItemDatasource {
    private fun createDummyItem(id: Int, barcode: Barcode) = Item(
        id = ItemId(id),
        barcode = barcode,
        name = "Item$id",
        price = Price(100),
        storeId = StoreId(1)
    )

    override suspend fun fetchItem(barcode: Barcode) = createDummyItem(1, barcode)

    override suspend fun fetchItemList(barcodeList: List<Barcode>) =
        barcodeList.mapIndexed { index, barcode -> createDummyItem(index, barcode) }
}
