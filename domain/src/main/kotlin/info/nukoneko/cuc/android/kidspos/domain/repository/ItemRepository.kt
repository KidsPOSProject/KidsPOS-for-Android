package info.nukoneko.cuc.android.kidspos.domain.repository

import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Item

interface ItemRepository {
    suspend fun fetchItem(barcode: Barcode): Item

    suspend fun fetchItemList(barcodeList: List<Barcode>): List<Item>
}
