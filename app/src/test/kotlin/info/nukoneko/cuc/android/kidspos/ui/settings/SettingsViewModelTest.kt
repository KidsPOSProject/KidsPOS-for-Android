package info.nukoneko.cuc.android.kidspos.ui.settings

import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import info.nukoneko.cuc.android.kidspos.testutil.FakeApkDownloader
import info.nukoneko.cuc.android.kidspos.testutil.FakeApkInstaller
import info.nukoneko.cuc.android.kidspos.testutil.FakeAppUpdateService
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.createSettingsViewModel
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.update.ApkInstallResult
import info.nukoneko.cuc.android.kidspos.update.ApkInstallResultBus
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

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
    fun checkUpdateWithoutNewerVersionReportsUpToDate() = runTest {
        val viewModel = createSettingsViewModel(settingsRepository)

        viewModel.onCheckUpdate()

        assertEquals(UpdateStatus.UpToDate, viewModel.uiState.value.updateStatus)
    }

    @Test
    fun checkUpdateWithNewerVersionExposesIt() = runTest {
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
    fun checkUpdateFailureIsReported() = runTest {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = { throw Exception("boom") }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateService = updateService
        )

        viewModel.onCheckUpdate()

        assertEquals(
            UpdateStatus.Failed(UpdateFailure.CHECK),
            viewModel.uiState.value.updateStatus
        )
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
    fun startUpdateWithoutInstallPermissionAsksForIt() = runTest {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = { appUpdate() }
        val downloader = FakeApkDownloader()
        val installer = FakeApkInstaller()
        installer.installAllowed = false
        val viewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateService = updateService,
            apkDownloader = downloader,
            apkInstaller = installer
        )

        viewModel.onCheckUpdate()
        viewModel.onStartUpdate()

        assertEquals(UpdateStatus.InstallNotPermitted, viewModel.uiState.value.updateStatus)
        assertTrue(downloader.downloadedUpdates.isEmpty())
    }

    @Test
    fun downloadFailureIsReported() = runTest {
        val updateService = FakeAppUpdateService()
        updateService.checkForUpdateHandler = { appUpdate() }
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { _, _ -> throw Exception("boom") }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            appUpdateService = updateService,
            apkDownloader = downloader
        )

        viewModel.onCheckUpdate()
        viewModel.onStartUpdate()

        assertEquals(
            UpdateStatus.Failed(UpdateFailure.DOWNLOAD),
            viewModel.uiState.value.updateStatus
        )
    }

    @Test
    fun installResultFromBusUpdatesStatus() = runTest {
        val bus = ApkInstallResultBus()
        val viewModel = createSettingsViewModel(
            settingsRepository,
            apkInstallResultBus = bus
        )

        bus.emit(ApkInstallResult.CANCELLED)
        assertEquals(UpdateStatus.Idle, viewModel.uiState.value.updateStatus)

        bus.emit(ApkInstallResult.FAILURE)
        assertEquals(
            UpdateStatus.Failed(UpdateFailure.INSTALL),
            viewModel.uiState.value.updateStatus
        )

        bus.emit(ApkInstallResult.SUCCESS)
        assertEquals(UpdateStatus.UpToDate, viewModel.uiState.value.updateStatus)
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
}
