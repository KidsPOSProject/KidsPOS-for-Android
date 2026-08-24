package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.api.DangerZoneService
import info.nukoneko.cuc.android.kidspos.entity.DangerZoneVerification

class FakeDangerZoneService : DangerZoneService {
    val verifiedPasswords = mutableListOf<String>()

    var isPasswordConfiguredHandler: suspend () -> Boolean = { false }

    var verifyPasswordHandler: suspend (String) -> DangerZoneVerification = {
        DangerZoneVerification(valid = false, configured = true, message = "パスワードが違います")
    }

    override suspend fun isPasswordConfigured(): Boolean = isPasswordConfiguredHandler()

    override suspend fun verifyPassword(password: String): DangerZoneVerification {
        verifiedPasswords += password
        return verifyPasswordHandler(password)
    }
}
