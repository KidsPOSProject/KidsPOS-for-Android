package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.api.generated.DangerZoneApi
import info.nukoneko.cuc.android.kidspos.api.generated.model.VerifyDangerZonePasswordRequest
import info.nukoneko.cuc.android.kidspos.entity.DangerZoneVerification

class OpenApiDangerZoneService(
    private val dangerZoneApi: DangerZoneApi
) : DangerZoneService {

    override suspend fun isPasswordConfigured(): Boolean {
        val response = dangerZoneApi.getDangerZoneStatus()
        if (!response.isSuccessful) {
            throw Exception("Failed to get danger zone status: ${response.code()}")
        }
        return response.body()!!.configured
    }

    override suspend fun verifyPassword(password: String): DangerZoneVerification {
        val response = dangerZoneApi.verifyDangerZonePassword(
            VerifyDangerZonePasswordRequest(password = password)
        )
        if (!response.isSuccessful) {
            throw Exception("Failed to verify danger zone password: ${response.code()}")
        }
        val body = response.body()!!
        return DangerZoneVerification(
            valid = body.valid,
            configured = body.configured,
            message = body.message
        )
    }
}
