package info.nukoneko.cuc.android.kidspos.data.datasourceimpl

import info.nukoneko.cuc.android.kidspos.data.api.APIService
import info.nukoneko.cuc.android.kidspos.data.datasource.ItemDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Item

class ItemRestDatasource(private val api: APIService) : ItemDatasource {
    override suspend fun fetchItem(barcode: Barcode): Item {
        return api.getItem(
            itemBarcode = barcode.value
        )
    }

    override suspend fun fetchItemList(barcodeList: List<Barcode>): List<Item> {
        TODO("Not yet implemented")
    }
}
