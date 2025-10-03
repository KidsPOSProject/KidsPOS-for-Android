package info.nukoneko.cuc.android.kidspos.di

import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Response

class ServerSelectionInterceptor(var serverAddress: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (serverAddress.isNotEmpty()) {
            val url = "$serverAddress/${request.url.pathSegments.joinToString("/")}"
            request = request.newBuilder().url(url).build()
        }
        
        // リクエスト情報をダンプ
        Logger.d("HTTP Request: ${request.method} ${request.url}")
        Logger.d("  Headers: ${request.headers}")
        if (request.url.querySize > 0) {
            Logger.d("  Query params: ${request.url.query}")
        }
        request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            val bodyString = buffer.readUtf8()
            Logger.d("  Body: content-type=${body.contentType()}")
            Logger.d("  Body content: $bodyString")
        }
        
        val startTime = System.currentTimeMillis()
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - startTime
        
        Logger.i("HTTP Response: ${response.code} ${request.method} ${request.url} (${duration}ms)")
        
        return response
    }
}