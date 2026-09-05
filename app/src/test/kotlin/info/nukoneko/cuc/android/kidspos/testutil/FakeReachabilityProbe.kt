package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.connection.ReachabilityProbe

class FakeReachabilityProbe : ReachabilityProbe {
    val probeCalls = mutableListOf<Pair<String, Int>>()
    var probeHandler: suspend (String, Int) -> Unit = { _, _ -> }
    override suspend fun probe(host: String, port: Int) {
        probeCalls += host to port
        probeHandler(host, port)
    }
}
