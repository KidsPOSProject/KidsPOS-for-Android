package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.di.hilt.IoDispatcher
import info.nukoneko.cuc.android.kidspos.entity.ServerStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerStatusRepository @Inject constructor(
    private val apiService: APIService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun getServerStatus(): ServerStatus = withContext(dispatcher) {
        apiService.getServerStatus()
    }
}
