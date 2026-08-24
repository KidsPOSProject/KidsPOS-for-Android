package info.nukoneko.cuc.android.kidspos.ui.settings

import androidx.lifecycle.viewModelScope
import info.nukoneko.cuc.android.kidspos.api.DangerZoneRateLimitedException
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import info.nukoneko.cuc.android.kidspos.entity.DangerZoneVerification
import info.nukoneko.cuc.android.kidspos.testutil.FakeApkDownloader
import info.nukoneko.cuc.android.kidspos.testutil.FakeApkInstaller
import info.nukoneko.cuc.android.kidspos.testutil.FakeAppUpdateService
import info.nukoneko.cuc.android.kidspos.testutil.FakeDangerZoneService
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

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
    fun unconfiguredPasswordLeavesDangerZoneUnlocked() = runTest {
        val viewModel = createSettingsViewModel(settingsRepository)

        assertEquals(
            DangerZoneStatus.Unlocked(DangerZoneReason.NOT_CONFIGURED),
            viewModel.uiState.value.dangerZoneStatus
        )
    }

    @Test
    fun configuredPasswordLocksDangerZone() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { true }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        assertEquals(DangerZoneStatus.Locked(), viewModel.uiState.value.dangerZoneStatus)
    }

    @Test
    fun statusFailureLeavesDangerZoneUnlocked() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { throw Exception("boom") }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        assertEquals(
            DangerZoneStatus.Unlocked(DangerZoneReason.STATUS_UNAVAILABLE),
            viewModel.uiState.value.dangerZoneStatus
        )
    }

    @Test
    fun correctPasswordUnlocksDangerZone() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { true }
        dangerZoneService.verifyPasswordHandler = {
            DangerZoneVerification(valid = true, configured = true, message = "OK")
        }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        viewModel.onDangerZonePasswordChange("secret")
        viewModel.onUnlockDangerZone()

        assertEquals(
            DangerZoneStatus.Unlocked(DangerZoneReason.VERIFIED),
            viewModel.uiState.value.dangerZoneStatus
        )
        assertEquals(listOf("secret"), dangerZoneService.verifiedPasswords)
        assertEquals("", viewModel.uiState.value.dangerZonePassword)
    }

    @Test
    fun wrongPasswordKeepsDangerZoneLockedWithMessage() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { true }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        viewModel.onDangerZonePasswordChange("wrong")
        viewModel.onUnlockDangerZone()

        assertEquals(
            DangerZoneStatus.Locked(DangerZoneError.Rejected("パスワードが違います")),
            viewModel.uiState.value.dangerZoneStatus
        )
        assertEquals("wrong", viewModel.uiState.value.dangerZonePassword)
    }

    @Test
    fun verifyFailureKeepsDangerZoneLocked() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { true }
        dangerZoneService.verifyPasswordHandler = { throw Exception("boom") }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        viewModel.onDangerZonePasswordChange("secret")
        viewModel.onUnlockDangerZone()

        assertEquals(
            DangerZoneStatus.Locked(DangerZoneError.Unreachable),
            viewModel.uiState.value.dangerZoneStatus
        )
    }

    @Test
    fun rateLimitedVerifyKeepsDangerZoneLockedWithRetryAfter() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { true }
        dangerZoneService.verifyPasswordHandler = { throw DangerZoneRateLimitedException(45) }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        viewModel.onDangerZonePasswordChange("wrong")
        viewModel.onUnlockDangerZone()

        assertEquals(
            DangerZoneStatus.Locked(DangerZoneError.RateLimited(45)),
            viewModel.uiState.value.dangerZoneStatus
        )
        assertEquals("wrong", viewModel.uiState.value.dangerZonePassword)
    }

    @Test
    fun rateLimitedVerifyWithoutRetryAfterKeepsDangerZoneLocked() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { true }
        dangerZoneService.verifyPasswordHandler = { throw DangerZoneRateLimitedException(null) }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        viewModel.onDangerZonePasswordChange("wrong")
        viewModel.onUnlockDangerZone()

        assertEquals(
            DangerZoneStatus.Locked(DangerZoneError.RateLimited(null)),
            viewModel.uiState.value.dangerZoneStatus
        )
    }

    @Test
    fun passwordClearedOnServerUnlocksDangerZone() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { true }
        dangerZoneService.verifyPasswordHandler = {
            DangerZoneVerification(valid = false, configured = false, message = "未設定です")
        }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        viewModel.onDangerZonePasswordChange("secret")
        viewModel.onUnlockDangerZone()

        assertEquals(
            DangerZoneStatus.Unlocked(DangerZoneReason.NOT_CONFIGURED),
            viewModel.uiState.value.dangerZoneStatus
        )
    }

    @Test
    fun emptyPasswordDoesNotCallServer() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { true }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        viewModel.onUnlockDangerZone()

        assertTrue(dangerZoneService.verifiedPasswords.isEmpty())
        assertEquals(DangerZoneStatus.Locked(), viewModel.uiState.value.dangerZoneStatus)
    }

    @Test
    fun lockDangerZoneRechecksServerStatus() = runTest {
        val dangerZoneService = FakeDangerZoneService()
        dangerZoneService.isPasswordConfiguredHandler = { true }
        dangerZoneService.verifyPasswordHandler = {
            DangerZoneVerification(valid = true, configured = true, message = "OK")
        }
        val viewModel = createSettingsViewModel(
            settingsRepository,
            dangerZoneService = dangerZoneService
        )

        viewModel.onDangerZonePasswordChange("secret")
        viewModel.onUnlockDangerZone()
        assertTrue(viewModel.uiState.value.dangerZoneUnlocked)

        viewModel.onLockDangerZone()

        assertEquals(DangerZoneStatus.Locked(), viewModel.uiState.value.dangerZoneStatus)
    }
}
