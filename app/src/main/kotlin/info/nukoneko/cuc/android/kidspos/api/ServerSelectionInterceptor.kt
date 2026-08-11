package info.nukoneko.cuc.android.kidspos.api

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class ServerSelectionInterceptor(@Volatile var serverAddress: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val server = serverAddress.toHttpUrlOrNull() ?: return chain.proceed(request)
        val url = request.url.newBuilder()
            .scheme(server.scheme)
            .host(server.host)
            .port(server.port)
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}
