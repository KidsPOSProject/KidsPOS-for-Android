package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.api.generated.DangerZoneApi
import info.nukoneko.cuc.android.kidspos.api.generated.model.ChangeDangerZonePasswordRequest
import info.nukoneko.cuc.android.kidspos.api.generated.model.ClearDangerZonePasswordRequest
import info.nukoneko.cuc.android.kidspos.api.generated.model.DangerZonePasswordResponse
import info.nukoneko.cuc.android.kidspos.api.generated.model.DangerZoneStatusResponse
import info.nukoneko.cuc.android.kidspos.api.generated.model.DangerZoneVerifyResponse
import info.nukoneko.cuc.android.kidspos.api.generated.model.VerifyDangerZonePasswordRequest
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class OpenApiDangerZoneServiceTest {

    private class FakeDangerZoneApi(
        private val verifyResponse: Response<DangerZoneVerifyResponse>
    ) : DangerZoneApi {
        override suspend fun getDangerZoneStatus(): Response<DangerZoneStatusResponse> =
            Response.success(DangerZoneStatusResponse(configured = true))

        override suspend fun verifyDangerZonePassword(
            verifyDangerZonePasswordRequest: VerifyDangerZonePasswordRequest
        ): Response<DangerZoneVerifyResponse> = verifyResponse

        override suspend fun changeDangerZonePassword(
            changeDangerZonePasswordRequest: ChangeDangerZonePasswordRequest
        ): Response<DangerZonePasswordResponse> =
            Response.success(
                DangerZonePasswordResponse(
                    success = true,
                    message = "パスワードを保存しました",
                    configured = true
                )
            )

        override suspend fun clearDangerZonePassword(
            clearDangerZonePasswordRequest: ClearDangerZonePasswordRequest
        ): Response<DangerZonePasswordResponse> =
            Response.success(
                DangerZonePasswordResponse(
                    success = true,
                    message = "パスワードを解除しました",
                    configured = false
                )
            )
    }

    private fun tooManyRequests(retryAfter: String?): Response<DangerZoneVerifyResponse> {
        val raw = okhttp3.Response.Builder()
            .request(Request.Builder().url("http://localhost/api/setting/danger-zone/verify").build())
            .protocol(Protocol.HTTP_1_1)
            .code(429)
            .message("Too Many Requests")
            .apply { retryAfter?.let { header("Retry-After", it) } }
            .build()
        return Response.error("{}".toResponseBody("application/json".toMediaType()), raw)
    }

    private fun serviceWith(response: Response<DangerZoneVerifyResponse>) =
        OpenApiDangerZoneService(FakeDangerZoneApi(response))

    @Test
    fun successfulVerifyIsMappedToVerification() = runTest {
        val service = serviceWith(
            Response.success(
                DangerZoneVerifyResponse(valid = true, configured = true, message = "認証しました")
            )
        )

        val result = service.verifyPassword("secret")

        assertEquals(true, result.valid)
        assertEquals(true, result.configured)
        assertEquals("認証しました", result.message)
    }

    @Test
    fun tooManyRequestsRaisesRateLimitedWithRetryAfter() = runTest {
        val service = serviceWith(tooManyRequests("45"))

        val error = runCatching { service.verifyPassword("wrong") }.exceptionOrNull()

        assertTrue(error is DangerZoneRateLimitedException)
        assertEquals(45L, (error as DangerZoneRateLimitedException).retryAfterSeconds)
    }

    @Test
    fun tooManyRequestsWithoutRetryAfterRaisesRateLimitedWithoutSeconds() = runTest {
        val service = serviceWith(tooManyRequests(null))

        val error = runCatching { service.verifyPassword("wrong") }.exceptionOrNull()

        assertTrue(error is DangerZoneRateLimitedException)
        assertNull((error as DangerZoneRateLimitedException).retryAfterSeconds)
    }

    @Test
    fun tooManyRequestsWithHttpDateRetryAfterRaisesRateLimitedWithoutSeconds() = runTest {
        val service = serviceWith(tooManyRequests("Wed, 21 Oct 2015 07:28:00 GMT"))

        val error = runCatching { service.verifyPassword("wrong") }.exceptionOrNull()

        assertTrue(error is DangerZoneRateLimitedException)
        assertNull((error as DangerZoneRateLimitedException).retryAfterSeconds)
    }

    @Test
    fun otherErrorStatusRaisesGenericFailure() = runTest {
        val raw = okhttp3.Response.Builder()
            .request(Request.Builder().url("http://localhost/api/setting/danger-zone/verify").build())
            .protocol(Protocol.HTTP_1_1)
            .code(500)
            .message("Internal Server Error")
            .build()
        val service = serviceWith(
            Response.error("{}".toResponseBody("application/json".toMediaType()), raw)
        )

        val error = runCatching { service.verifyPassword("wrong") }.exceptionOrNull()

        assertTrue(error != null && error !is DangerZoneRateLimitedException)
    }
}
