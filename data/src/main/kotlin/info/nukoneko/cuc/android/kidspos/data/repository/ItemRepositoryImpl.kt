package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.data.datasource.ItemDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Item
import info.nukoneko.cuc.android.kidspos.domain.repository.ItemRepository

class ItemRepositoryImpl(private val itemDatasource: ItemDatasource) : ItemRepository {
    override suspend fun fetchItem(barcode: Barcode): Item {
        return itemDatasource.fetchItem(barcode)
    }

    override suspend fun fetchItemList(barcodeList: List<Barcode>): List<Item> {
        return itemDatasource.fetchItemList(barcodeList)
    }
}
