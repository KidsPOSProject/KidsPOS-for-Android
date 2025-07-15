package info.nukoneko.cuc.android.kidspos.di

import okhttp3.Interceptor
import okhttp3.Response

class ServerSelectionInterceptor(var serverAddress: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (serverAddress.isNotEmpty()) {
            val url = "$serverAddress/api/${request.url.pathSegments.joinToString("/")}"
            request = request.newBuilder().url(url).build()
        }
        return chain.proceed(request)
    }
}
