package info.nukoneko.cuc.android.kidspos.ui.settings

import androidx.lifecycle.viewModelScope
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.testutil.FakeApkDownloader
import info.nukoneko.cuc.android.kidspos.testutil.FakeApkInstaller
import info.nukoneko.cuc.android.kidspos.testutil.FakeAppUpdateService
import info.nukoneko.cuc.android.kidspos.testutil.FakeReachabilityProbe
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.createAppUpdateManager
import info.nukoneko.cuc.android.kidspos.testutil.createSettingsViewModel
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.update.UpdateStatus
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.IOException

class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingsRepository = fakeSettingsRepository()

    private fun appUpdate(versionCode: Int = 99) = AppUpdate(
        versionName = "9.9.9",
        versionCode = versionCode,
        fileSize = 1024,
        releaseNotes = "テスト用リリースノート",
        downloadPath = "/api/apk/download/1"
    )

    @Test
    fun initialStateReflectsStoredSettings() = runTest {
        settingsRepository.setServerAddress("http://10.0.0.2:8080")
        settingsRepository.setRunningMode(Mode.PRODUCTION)

        val viewModel = createSettingsViewModel(settingsRepository)

        assertEquals("http://10.0.0.2:8080", viewModel.uiState.value.serverAddress)
        assertEquals(Mode.PRODUCTION, viewModel.uiState.value.mode)
    }

    @Test
    fun serverAddressChangeIsPersistedAndReflected() = runTest {
        val viewModel = createSettingsViewModel(settingsRepository)

        viewModel.onServerAddressChange("http://192.168.1.10:8080")

        assertEquals("http://192.168.1.10:8080", viewModel.uiState.value.serverAddress)
    }

    @Test
    fun toggleModeSwitchesBetweenPracticeAndProduction() = runTest {
        val viewModel = createSettingsViewModel(settingsRepository)
        assertEquals(Mode.PRACTICE, viewModel.uiState.value.mode)

        viewModel.onToggleMode()
        assertEquals(Mode.PRODUCTION, viewModel.uiState.value.mode)

        viewModel.onToggleMode()
        assertEquals(Mode.PRACTICE, viewModel.uiState.value.mode)
    }

    @Test
    fun toggleModeIsPersistedEvenWhenTheScreenIsLeftImmediately() = runTest {
        val viewModel = createSettingsViewModel(settingsRepository)

        viewModel.viewModelScope.cancel()
        viewModel.onToggleMode()

        assertEquals(Mode.PRODUCTION, settingsRepository.runningMode.first())
    }

    @Test
    fun serverAddressIsPersistedEvenWhenTheScreenIsLeftImmediately() = runTest {
        val viewModel = createSettingsViewModel(settingsRepository)

        viewModel.viewModelScope.cancel()
        viewModel.onServerAddressChange("http://192.168.1.20:8080")

        assertEquals("http://192.168.1.20:8080", settingsRepository.serverAddress.first())
    }

    @Test
    fun checkUpdateExposesAvailableUpdate() = runTest {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = { appUpdate() }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateService = updateService
        )

        viewModel.onCheckUpdate()

        val status = viewModel.uiState.value.updateStatus
        assertTrue(status is UpdateStatus.Available)
        assertEquals(99, (status as UpdateStatus.Available).update.versionCode)
    }

    @Test
    fun startUpdateDownloadsAndInstalls() = runTest {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = { appUpdate() }
        val downloader = FakeApkDownloader()
        val installer = FakeApkInstaller()
        val viewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateService = updateService,
            apkDownloader = downloader,
            apkInstaller = installer
        )

        viewModel.onCheckUpdate()
        viewModel.onStartUpdate()

        assertEquals(1, downloader.downloadedUpdates.size)
        assertEquals(1, installer.installedApks.size)
        assertEquals(UpdateStatus.Installing, viewModel.uiState.value.updateStatus)
    }

    @Test
    fun dismissUpdateReturnsToIdle() = runTest {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = { appUpdate() }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateService = updateService
        )

        viewModel.onCheckUpdate()
        viewModel.onDismissUpdate()

        assertEquals(UpdateStatus.Idle, viewModel.uiState.value.updateStatus)
    }

    @Test
    fun updateStatusIsRetainedWhenViewModelIsRecreated() = runTest {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = { appUpdate() }
        val manager = createAppUpdateManager(appUpdateService = updateService)
        val firstViewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateManager = manager
        )

        firstViewModel.onCheckUpdate()
        assertTrue(firstViewModel.uiState.value.updateStatus is UpdateStatus.Available)

        val secondViewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateManager = manager
        )

        assertTrue(secondViewModel.uiState.value.updateStatus is UpdateStatus.Available)
    }

    @Test
    fun downloadInProgressSurvivesViewModelRecreation() = runTest {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = { appUpdate() }
        val downloadGate = CompletableDeferred<Unit>()
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { update, onProgress ->
            onProgress(0.5f)
            downloadGate.await()
            File("kidspos-${update.versionCode}.apk")
        }
        val installer = FakeApkInstaller()
        val manager = createAppUpdateManager(
            appUpdateService = updateService,
            apkDownloader = downloader,
            apkInstaller = installer
        )
        val firstViewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateManager = manager
        )

        firstViewModel.onCheckUpdate()
        firstViewModel.onStartUpdate()
        assertEquals(UpdateStatus.Downloading(0.5f), firstViewModel.uiState.value.updateStatus)

        val secondViewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateManager = manager
        )
        assertEquals(UpdateStatus.Downloading(0.5f), secondViewModel.uiState.value.updateStatus)

        downloadGate.complete(Unit)

        assertEquals(1, installer.installedApks.size)
        assertEquals(UpdateStatus.Installing, secondViewModel.uiState.value.updateStatus)
    }

    @Test
    fun connectionTestUpdatesConnectionState() = runTest {
        val viewModel = createSettingsViewModel(settingsRepository)

        viewModel.onConnectionTest()

        assertTrue(viewModel.uiState.value.connection.isConnected)
    }

    @Test
    fun loadingServerAddressTriggersConnectionCheck() = runTest {
        val probe = FakeReachabilityProbe()
        val viewModel = createSettingsViewModel(settingsRepository, reachabilityProbe = probe)

        viewModel.onServerAddressChange("http://10.0.0.5:8080")

        assertEquals(listOf("10.0.0.5" to 8080), probe.probeCalls)
    }

    @Test
    fun switchingToProductionClearsStoreAndChecksConnection() = runTest {
        val probe = FakeReachabilityProbe()
        settingsRepository.setCurrentStore(Store(1, "テスト店"))
        val viewModel = createSettingsViewModel(settingsRepository, reachabilityProbe = probe)

        viewModel.onToggleMode()

        assertNull(settingsRepository.currentStore.first())
        assertEquals(1, probe.probeCalls.size)
        assertEquals(Mode.PRODUCTION, viewModel.uiState.value.mode)
    }

    @Test
    fun switchingToPracticeDoesNotCheckConnection() = runTest {
        val probe = FakeReachabilityProbe()
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        val viewModel = createSettingsViewModel(settingsRepository, reachabilityProbe = probe)

        viewModel.onToggleMode()

        assertTrue(probe.probeCalls.isEmpty())
        assertEquals(Mode.PRACTICE, viewModel.uiState.value.mode)
    }

    @Test
    fun canLeaveIsFalseInProductionUntilConnected() = runTest {
        val probe = FakeReachabilityProbe()
        probe.probeHandler = { _, _ -> throw IOException("refused") }
        settingsRepository.setRunningMode(Mode.PRODUCTION)
        val viewModel = createSettingsViewModel(settingsRepository, reachabilityProbe = probe)

        viewModel.onConnectionTest()

        assertFalse(viewModel.uiState.value.canLeave)

        probe.probeHandler = { _, _ -> }
        viewModel.onConnectionTest()

        assertTrue(viewModel.uiState.value.canLeave)
    }

    @Test
    fun canLeaveIsAlwaysTrueInPracticeMode() = runTest {
        val probe = FakeReachabilityProbe()
        probe.probeHandler = { _, _ -> throw IOException("refused") }
        val viewModel = createSettingsViewModel(settingsRepository, reachabilityProbe = probe)

        assertTrue(viewModel.uiState.value.canLeave)

        viewModel.onConnectionTest()

        assertTrue(viewModel.uiState.value.canLeave)
    }
}
