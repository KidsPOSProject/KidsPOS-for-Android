package info.nukoneko.cuc.android.kidspos.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

class ServerSelectionInterceptorTest {

    private lateinit var capturedRequest: Request

    private fun clientWith(interceptor: ServerSelectionInterceptor): OkHttpClient =
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

    private fun execute(interceptor: ServerSelectionInterceptor, url: String) {
        clientWith(interceptor)
            .newCall(Request.Builder().url(url).build())
            .execute()
            .close()
    }

    @Test
    fun requestIsRoutedToConfiguredServerWithoutDuplicatingApiPrefix() {
        val interceptor = ServerSelectionInterceptor("http://10.0.0.1:9090")

        execute(interceptor, "http://192.168.0.220:8080/api/item/barcode/1234")

        assertEquals(
            "http://10.0.0.1:9090/api/item/barcode/1234",
            capturedRequest.url.toString()
        )
    }

    @Test
    fun queryParametersArePreserved() {
        val interceptor = ServerSelectionInterceptor("http://10.0.0.1:9090")

        execute(interceptor, "http://192.168.0.220:8080/api/apk/version/check?currentVersionCode=8")

        assertEquals(
            "http://10.0.0.1:9090/api/apk/version/check?currentVersionCode=8",
            capturedRequest.url.toString()
        )
    }

    @Test
    fun serverAddressChangeTakesEffectOnNextRequest() {
        val interceptor = ServerSelectionInterceptor("http://10.0.0.1:9090")

        execute(interceptor, "http://192.168.0.220:8080/api/store")
        interceptor.serverAddress = "http://10.0.0.2:8080"
        execute(interceptor, "http://192.168.0.220:8080/api/store")

        assertEquals("http://10.0.0.2:8080/api/store", capturedRequest.url.toString())
    }

    @Test
    fun invalidServerAddressLeavesRequestUntouched() {
        val interceptor = ServerSelectionInterceptor("not a url")

        execute(interceptor, "http://192.168.0.220:8080/api/store")

        assertEquals("http://192.168.0.220:8080/api/store", capturedRequest.url.toString())
    }
}
