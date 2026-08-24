package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.DangerZoneVerification

interface DangerZoneService {
    suspend fun isPasswordConfigured(): Boolean

    suspend fun verifyPassword(password: String): DangerZoneVerification
}
