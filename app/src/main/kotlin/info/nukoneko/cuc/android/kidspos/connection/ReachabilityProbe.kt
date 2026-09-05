package info.nukoneko.cuc.android.kidspos.connection

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

fun interface ReachabilityProbe {
    suspend fun probe(host: String, port: Int)
}

class SocketReachabilityProbe(
    private val dispatcher: CoroutineDispatcher
) : ReachabilityProbe {
    override suspend fun probe(host: String, port: Int) {
        withContext(dispatcher) {
            Socket().use { it.connect(InetSocketAddress(host, port), CONNECT_TIMEOUT_MILLIS) }
        }
    }

    private companion object {
        const val CONNECT_TIMEOUT_MILLIS = 3000
    }
}
