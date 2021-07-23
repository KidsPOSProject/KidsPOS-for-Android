package info.nukoneko.cuc.android.kidspos.data.datasource

import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Item

interface ItemDatasource {
    suspend fun fetchItem(barcode: Barcode): Item

    suspend fun fetchItemList(barcodeList: List<Barcode>): List<Item>
}
