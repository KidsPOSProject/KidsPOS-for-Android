package info.nukoneko.cuc.android.kidspos.manager

import info.nukoneko.cuc.android.kidspos.api.APIAdapter
import info.nukoneko.cuc.android.kidspos.entity.Sale
import io.reactivex.Single

class SaleManager(apiAdapter: APIAdapter) : BaseManager(apiAdapter) {
    fun createSale(receiveMoney: Int, saleItemCount: Int, sumPrice: Int, saleItemsList: String, storeId: Int, staffCode: String): Single<Sale> {
        return apiService.createSale(receiveMoney, saleItemCount, sumPrice, saleItemsList, storeId, staffCode)
    }
}
