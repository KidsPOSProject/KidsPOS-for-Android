package info.nukoneko.cuc.android.kidspos.connection

import info.nukoneko.cuc.android.kidspos.api.ApiHttpException
import info.nukoneko.cuc.android.kidspos.data.repository.ServerStatusRepository
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.entity.ServerStatus
import info.nukoneko.cuc.android.kidspos.testutil.FakeAPIService
import info.nukoneko.cuc.android.kidspos.testutil.FakeReachabilityProbe
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ConnectionMonitorTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun monitor(
        probe: FakeReachabilityProbe = FakeReachabilityProbe(),
        apiService: FakeAPIService = FakeAPIService(),
        settingsRepository: SettingsRepository = fakeSettingsRepository()
    ): ConnectionMonitor {
        val dispatcher = mainDispatcherRule.dispatcher
        return ConnectionMonitor(
            settingsRepository,
            probe,
            ServerStatusRepository(apiService, dispatcher),
            CoroutineScope(dispatcher)
        )
    }

    @Test
    fun successReportsConnectedWithServerStatus() = runTest {
        val probe = FakeReachabilityProbe()
        val monitor = monitor(probe = probe)

        val result = monitor.check()

        assertEquals(StageStatus.OK, result.reachability)
        assertEquals(StageStatus.OK, result.response)
        assertTrue(result.isConnected)
        assertEquals(listOf("192.168.0.220" to 8080), probe.probeCalls)
        assertEquals("OK", result.serverStatus?.status)
    }

    @Test
    fun unreachableProbeReportsFailure() = runTest {
        val probe = FakeReachabilityProbe()
        probe.probeHandler = { _, _ -> throw IOException("refused") }
        val monitor = monitor(probe = probe)

        val result = monitor.check()

        assertEquals(StageStatus.FAILED, result.reachability)
        assertEquals(StageStatus.PENDING, result.response)
        assertTrue(result.failure is ConnectionFailure.Unreachable)
    }

    @Test
    fun httpErrorFromServerStatusReportsHttpStatus() = runTest {
        val apiService = FakeAPIService()
        apiService.getServerStatusHandler = { throw ApiHttpException(500) }
        val monitor = monitor(apiService = apiService)

        val result = monitor.check()

        assertEquals(StageStatus.OK, result.reachability)
        assertEquals(StageStatus.FAILED, result.response)
        assertEquals(ConnectionFailure.HttpStatus(500), result.failure)
    }

    @Test
    fun apiVersionMismatchReportsFailure() = runTest {
        val apiService = FakeAPIService()
        apiService.getServerStatusHandler = {
            ServerStatus(status = "OK", version = "9.9.9", apiVersion = 99)
        }
        val monitor = monitor(apiService = apiService)

        val result = monitor.check()

        assertEquals(StageStatus.OK, result.reachability)
        assertEquals(StageStatus.FAILED, result.response)
        assertEquals(ConnectionFailure.ApiVersionMismatch(99), result.failure)
    }

    @Test
    fun invalidAddressReportsFailureWithoutProbing() = runTest {
        val probe = FakeReachabilityProbe()
        val settingsRepository = fakeSettingsRepository()
        settingsRepository.setServerAddress("not a url")
        val monitor = monitor(probe = probe, settingsRepository = settingsRepository)

        val result = monitor.check()

        assertEquals(StageStatus.FAILED, result.reachability)
        assertEquals(ConnectionFailure.InvalidAddress, result.failure)
        assertTrue(probe.probeCalls.isEmpty())
    }

    @Test
    fun addressChangeAfterSuccessResetsState() = runTest {
        val settingsRepository = fakeSettingsRepository()
        val monitor = monitor(settingsRepository = settingsRepository)

        monitor.check()
        settingsRepository.setServerAddress("http://192.168.0.221:9090")

        assertEquals(ConnectionCheck(), monitor.state.value)
    }

    @Test
    fun sameAddressReNotificationDoesNotResetState() = runTest {
        val settingsRepository = fakeSettingsRepository()
        val monitor = monitor(settingsRepository = settingsRepository)

        val result = monitor.check()
        settingsRepository.setServerAddress(SettingsRepository.DEFAULT_SERVER_ADDRESS)

        assertEquals(result, monitor.state.value)
    }

    @Test
    fun concurrentCheckCallsShareASingleProbeInvocation() = runTest {
        val gate = CompletableDeferred<Unit>()
        val probe = FakeReachabilityProbe()
        probe.probeHandler = { _, _ -> gate.await() }
        val monitor = monitor(probe = probe)

        val first = async { monitor.check() }
        val second = async { monitor.check() }
        gate.complete(Unit)

        assertEquals(first.await(), second.await())
        assertEquals(1, probe.probeCalls.size)
    }

    @Test
    fun reachabilityTimeoutReportsTimeout() = runTest {
        val probe = FakeReachabilityProbe()
        probe.probeHandler = { _, _ -> awaitCancellation() }
        val monitor = monitor(probe = probe)

        val result = monitor.check()

        assertEquals(StageStatus.FAILED, result.reachability)
        assertEquals(ConnectionFailure.Timeout, result.failure)
    }
}
