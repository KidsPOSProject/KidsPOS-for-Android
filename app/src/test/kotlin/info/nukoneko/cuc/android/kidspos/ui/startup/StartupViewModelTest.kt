package info.nukoneko.cuc.android.kidspos.ui.startup

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.connection.ConnectionMonitor
import info.nukoneko.cuc.android.kidspos.connection.StageStatus
import info.nukoneko.cuc.android.kidspos.data.repository.ServerStatusRepository
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.entity.ServerStatus
import info.nukoneko.cuc.android.kidspos.testutil.FakeAPIService
import info.nukoneko.cuc.android.kidspos.testutil.FakeReachabilityProbe
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class StartupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        settingsRepository: SettingsRepository,
        probe: FakeReachabilityProbe = FakeReachabilityProbe(),
        apiService: FakeAPIService = FakeAPIService()
    ): StartupViewModel {
        val dispatcher = mainDispatcherRule.dispatcher
        val monitor = ConnectionMonitor(
            settingsRepository,
            probe,
            ServerStatusRepository(apiService, dispatcher),
            CoroutineScope(dispatcher)
        )
        return StartupViewModel(settingsRepository, monitor)
    }

    @Test
    fun practiceModeGoesToMainWithoutChecking() = runTest {
        val probe = FakeReachabilityProbe()
        val viewModel = createViewModel(fakeSettingsRepository(), probe = probe)

        assertEquals(StartupDestination.MAIN, viewModel.uiState.value.destination)
        assertTrue(probe.probeCalls.isEmpty())
    }

    @Test
    fun productionModeGoesToMainWhenConnected() = runTest {
        val settingsRepository = fakeSettingsRepository()
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        val viewModel = createViewModel(settingsRepository)

        assertEquals(StartupDestination.MAIN, viewModel.uiState.value.destination)
        assertTrue(viewModel.uiState.value.connection.isConnected)
    }

    @Test
    fun productionModeGoesToSettingsWhenUnreachable() = runTest {
        val settingsRepository = fakeSettingsRepository()
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        val probe = FakeReachabilityProbe()
        probe.probeHandler = { _, _ -> throw IOException("refused") }
        val viewModel = createViewModel(settingsRepository, probe = probe)

        assertEquals(StartupDestination.SETTINGS, viewModel.uiState.value.destination)
        assertEquals(StageStatus.FAILED, viewModel.uiState.value.connection.reachability)
    }

    @Test
    fun productionModeGoesToSettingsWhenApiVersionMismatches() = runTest {
        val settingsRepository = fakeSettingsRepository()
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        val apiService = FakeAPIService()
        apiService.getServerStatusHandler = {
            ServerStatus(status = "OK", version = "9.9.9", apiVersion = APIService.SUPPORTED_API_VERSION + 1)
        }
        val viewModel = createViewModel(settingsRepository, apiService = apiService)

        assertEquals(StartupDestination.SETTINGS, viewModel.uiState.value.destination)
    }
}
