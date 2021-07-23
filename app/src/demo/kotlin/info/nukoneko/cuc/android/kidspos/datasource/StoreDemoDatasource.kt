package info.nukoneko.cuc.android.kidspos.datasource

import info.nukoneko.cuc.android.kidspos.data.datasource.StoreDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Store
import info.nukoneko.cuc.android.kidspos.domain.entity.StoreId

class StoreDemoDatasource : StoreDatasource {
    private fun createDummyStore(id: Int) = Store(
        id = StoreId(id),
        name = "Store$id"
    )

    override suspend fun fetchStoreList() = (0..4).map { createDummyStore(it) }
}
