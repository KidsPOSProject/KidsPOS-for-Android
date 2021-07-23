package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.data.datasource.SaleDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.ItemId
import info.nukoneko.cuc.android.kidspos.domain.entity.Sale
import info.nukoneko.cuc.android.kidspos.domain.entity.StoreId
import info.nukoneko.cuc.android.kidspos.domain.repository.SaleRepository

class SaleRepositoryImpl(private val saleDatasource: SaleDatasource) : SaleRepository {
    override suspend fun postSale(
        storeId: StoreId,
        staffBarcode: Barcode,
        deposit: Int,
        itemList: List<ItemId>
    ): Sale {
        return saleDatasource.postSale(storeId, staffBarcode, deposit, itemList)
    }

    override suspend fun fetchSale(barcode: Barcode): Sale {
        throw NotImplementedError()
    }
}
