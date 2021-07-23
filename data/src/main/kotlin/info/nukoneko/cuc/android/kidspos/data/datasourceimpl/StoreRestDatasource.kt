package info.nukoneko.cuc.android.kidspos.data.datasourceimpl

import info.nukoneko.cuc.android.kidspos.data.api.APIService
import info.nukoneko.cuc.android.kidspos.data.datasource.StoreDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Store

class StoreRestDatasource(private val api: APIService) : StoreDatasource {
    override suspend fun fetchStoreList(): List<Store> {
        return api.fetchStores()
    }
}
