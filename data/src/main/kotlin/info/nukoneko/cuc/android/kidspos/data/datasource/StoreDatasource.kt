package info.nukoneko.cuc.android.kidspos.data.datasource

import info.nukoneko.cuc.android.kidspos.domain.entity.Store

interface StoreDatasource {
    suspend fun fetchStoreList(): List<Store>
}
