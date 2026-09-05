package info.nukoneko.cuc.android.kidspos.connection

class AlwaysReachableProbe : ReachabilityProbe {
    override suspend fun probe(host: String, port: Int) = Unit
}
