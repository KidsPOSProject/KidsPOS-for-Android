package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.data.datasource.StatusDatasource
import info.nukoneko.cuc.android.kidspos.domain.repository.StatusRepository

class StatusRepositoryImpl(private val statusDatasource: StatusDatasource) : StatusRepository {
    override suspend fun check() {
        return statusDatasource.check()
    }
}
