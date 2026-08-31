package info.nukoneko.cuc.android.kidspos.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 端末の時刻をリクエストヘッダーで申告する
 *
 * サーバーはイントラネットに閉じていて NTP に届かず、Raspberry Pi は RTC を持たないため
 * 電源を入れるたびに時刻が巻き戻る。RTC を持つ端末の時刻の方が信頼できるので、
 * レジが通信するだけでサーバーの時刻が合うようにする。
 */
class ClientTimeInterceptor(
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header(CLIENT_TIME_HEADER, currentTimeMillis().toString())
            .build()
        return chain.proceed(request)
    }

    companion object {
        const val CLIENT_TIME_HEADER = "X-Client-Time"
    }
}
