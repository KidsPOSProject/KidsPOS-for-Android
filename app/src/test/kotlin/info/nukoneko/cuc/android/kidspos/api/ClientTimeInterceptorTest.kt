package info.nukoneko.cuc.android.kidspos.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

class ClientTimeInterceptorTest {

    private lateinit var capturedRequest: Request

    private fun execute(interceptor: ClientTimeInterceptor, request: Request) {
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(
                Interceptor { chain ->
                    capturedRequest = chain.request()
                    Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body("".toResponseBody(null))
                        .build()
                }
            )
            .build()
            .newCall(request)
            .execute()
            .close()
    }

    @Test
    fun deviceTimeIsSentAsHeader() {
        execute(
            ClientTimeInterceptor { 1_700_000_000_000L },
            Request.Builder().url("http://192.168.0.220:8080/api/store").build()
        )

        assertEquals("1700000000000", capturedRequest.header("X-Client-Time"))
    }

    @Test
    fun eachRequestCarriesTheCurrentTime() {
        var now = 1_700_000_000_000L
        val interceptor = ClientTimeInterceptor { now }
        val request = Request.Builder().url("http://192.168.0.220:8080/api/store").build()

        execute(interceptor, request)
        assertEquals("1700000000000", capturedRequest.header("X-Client-Time"))

        now = 1_700_000_060_000L
        execute(interceptor, request)
        assertEquals("1700000060000", capturedRequest.header("X-Client-Time"))
    }

    @Test
    fun existingHeaderIsReplaced() {
        execute(
            ClientTimeInterceptor { 1_700_000_000_000L },
            Request.Builder()
                .url("http://192.168.0.220:8080/api/store")
                .header("X-Client-Time", "1")
                .build()
        )

        assertEquals("1700000000000", capturedRequest.header("X-Client-Time"))
    }

    @Test
    fun otherRequestPropertiesArePreserved() {
        execute(
            ClientTimeInterceptor { 1_700_000_000_000L },
            Request.Builder()
                .url("http://192.168.0.220:8080/api/item/barcode/1234?a=b")
                .header("Accept", "application/json")
                .build()
        )

        assertEquals(
            "http://192.168.0.220:8080/api/item/barcode/1234?a=b",
            capturedRequest.url.toString()
        )
        assertEquals("application/json", capturedRequest.header("Accept"))
    }
}
