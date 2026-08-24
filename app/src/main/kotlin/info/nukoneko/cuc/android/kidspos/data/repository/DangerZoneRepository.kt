package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.api.DangerZoneService
import info.nukoneko.cuc.android.kidspos.di.hilt.IoDispatcher
import info.nukoneko.cuc.android.kidspos.entity.DangerZoneVerification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DangerZoneRepository @Inject constructor(
    private val dangerZoneService: DangerZoneService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun isPasswordConfigured(): Boolean = withContext(dispatcher) {
        dangerZoneService.isPasswordConfigured()
    }

    suspend fun verifyPassword(password: String): DangerZoneVerification = withContext(dispatcher) {
        dangerZoneService.verifyPassword(password)
    }
}
