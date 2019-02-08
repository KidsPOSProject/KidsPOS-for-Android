package info.nukoneko.cuc.android.kidspos.di

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class HostSelectionInterceptor(baseUrl: String) : Interceptor {
    var host: String = baseUrl

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        host.let {
            if (it.isNotEmpty()) {
                request = request.newBuilder().url(HttpUrl.parse(it)!!).build()
            }
        }
        return chain.proceed(request)
    }
}