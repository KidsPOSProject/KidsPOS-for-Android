package info.nukoneko.cuc.android.kidspos.manager

import info.nukoneko.cuc.android.kidspos.api.APIAdapter
import info.nukoneko.cuc.android.kidspos.entity.Item
import io.reactivex.Single

class ItemManager(apiAdapter: APIAdapter) : BaseManager(apiAdapter) {
    fun getItem(barcode: String): Single<Item> {
        return apiService.getItem(barcode)
    }
}
