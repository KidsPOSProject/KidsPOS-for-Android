package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.data.datasource.StoreDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Store
import info.nukoneko.cuc.android.kidspos.domain.repository.StoreRepository

class StoreRepositoryImpl(private val storeDatasource: StoreDatasource) : StoreRepository {
    override suspend fun fetchStoreList(): List<Store> {
        return storeDatasource.fetchStoreList()
    }
}
