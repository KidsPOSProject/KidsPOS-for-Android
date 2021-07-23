package info.nukoneko.cuc.android.kidspos.domain.repository

import info.nukoneko.cuc.android.kidspos.domain.entity.Store

interface StoreRepository {
    suspend fun fetchStoreList(): List<Store>
}
