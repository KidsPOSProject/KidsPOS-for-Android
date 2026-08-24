package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.DangerZoneVerification

class DemoDangerZoneService : DangerZoneService {
    override suspend fun isPasswordConfigured(): Boolean = false

    override suspend fun verifyPassword(password: String): DangerZoneVerification =
        DangerZoneVerification(
            valid = false,
            configured = false,
            message = "デモモードではパスワードは設定されていません"
        )
}
