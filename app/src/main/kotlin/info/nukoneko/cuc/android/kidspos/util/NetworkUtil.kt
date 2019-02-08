package info.nukoneko.cuc.android.kidspos.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtil {
    suspend fun checkReachability(hostName: String, serverPort: Int) = withContext(Dispatchers.IO) {
        try {
            val sock = Socket()
            sock.connect(InetSocketAddress(hostName, serverPort), 2000)
            sock.close()
            true
        } catch (e: Throwable) {
            false
        }
    }
}