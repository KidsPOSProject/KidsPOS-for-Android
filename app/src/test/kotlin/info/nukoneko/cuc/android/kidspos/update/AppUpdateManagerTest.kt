package info.nukoneko.cuc.android.kidspos.update

import info.nukoneko.cuc.android.kidspos.api.ApiHttpException
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import info.nukoneko.cuc.android.kidspos.testutil.FakeApkDownloader
import info.nukoneko.cuc.android.kidspos.testutil.FakeApkInstaller
import info.nukoneko.cuc.android.kidspos.testutil.FakeAppUpdateService
import info.nukoneko.cuc.android.kidspos.testutil.createAppUpdateManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.net.ConnectException
import java.net.SocketTimeoutException

class AppUpdateManagerTest {
    private fun appUpdate(versionCode: Int = 99) = AppUpdate(
        versionName = "9.9.9",
        versionCode = versionCode,
        fileSize = 1024,
        releaseNotes = "テスト用リリースノート",
        downloadPath = "/api/apk/download/1"
    )

    private fun updateService(update: AppUpdate? = appUpdate()) = FakeAppUpdateService().apply {
        checkForUpdateHandler = { update }
    }

    @Test
    fun initialStatusIsIdle() {
        assertEquals(UpdateStatus.Idle, createAppUpdateManager().status.value)
    }

    @Test
    fun checkForUpdateWithoutNewerVersionReportsUpToDate() = runTest {
        val manager = createAppUpdateManager(appUpdateService = updateService(null))

        manager.checkForUpdate(10)

        assertEquals(UpdateStatus.UpToDate, manager.status.value)
    }

    @Test
    fun checkForUpdatePassesCurrentVersionCode() = runTest {
        val service = updateService()
        val manager = createAppUpdateManager(appUpdateService = service)

        manager.checkForUpdate(42)

        assertEquals(listOf(42), service.checkedVersionCodes)
    }

    @Test
    fun checkForUpdateExposesAvailableUpdate() = runTest {
        val manager = createAppUpdateManager(appUpdateService = updateService())

        manager.checkForUpdate(10)

        val status = manager.status.value
        assertTrue(status is UpdateStatus.Available)
        assertEquals(99, (status as UpdateStatus.Available).update.versionCode)
    }

    @Test
    fun checkForUpdateFailureIsReported() = runTest {
        val service = FakeAppUpdateService()
        service.checkForUpdateHandler = { throw Exception("boom") }
        val manager = createAppUpdateManager(appUpdateService = service)

        manager.checkForUpdate(10)

        assertEquals(
            UpdateStatus.Failed(UpdateFailure.CHECK, UpdateFailureReason.Other("Exception: boom")),
            manager.status.value
        )
    }

    @Test
    fun startUpdateDownloadsAndInstalls() = runTest {
        val downloader = FakeApkDownloader()
        val installer = FakeApkInstaller()
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkDownloader = downloader,
            apkInstaller = installer
        )

        manager.checkForUpdate(10)
        manager.startUpdate()

        assertEquals(1, downloader.downloadedUpdates.size)
        assertEquals(1, installer.installedApks.size)
        assertEquals(UpdateStatus.Installing, manager.status.value)
    }

    @Test
    fun startUpdateWithoutAvailableUpdateIsIgnored() = runTest {
        val downloader = FakeApkDownloader()
        val manager = createAppUpdateManager(
            appUpdateService = updateService(null),
            apkDownloader = downloader
        )

        manager.checkForUpdate(10)
        manager.startUpdate()

        assertTrue(downloader.downloadedUpdates.isEmpty())
        assertEquals(UpdateStatus.UpToDate, manager.status.value)
    }

    @Test
    fun startUpdateWithoutInstallPermissionAsksForIt() = runTest {
        val downloader = FakeApkDownloader()
        val installer = FakeApkInstaller()
        installer.installAllowed = false
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkDownloader = downloader,
            apkInstaller = installer
        )

        manager.checkForUpdate(10)
        manager.startUpdate()

        assertEquals(UpdateStatus.InstallNotPermitted, manager.status.value)
        assertTrue(downloader.downloadedUpdates.isEmpty())
    }

    @Test
    fun downloadProgressIsPublished() = runTest {
        val progressGate = CompletableDeferred<Unit>()
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { update, onProgress ->
            onProgress(0.25f)
            progressGate.await()
            File("kidspos-${update.versionCode}.apk")
        }
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkDownloader = downloader
        )

        manager.checkForUpdate(10)
        manager.startUpdate()
        assertEquals(UpdateStatus.Downloading(0.25f), manager.status.value)

        progressGate.complete(Unit)
        assertEquals(UpdateStatus.Installing, manager.status.value)
    }

    @Test
    fun downloadFailureIsReported() = runTest {
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { _, _ -> throw Exception("boom") }
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkDownloader = downloader
        )

        manager.checkForUpdate(10)
        manager.startUpdate()

        assertEquals(
            UpdateStatus.Failed(UpdateFailure.DOWNLOAD, UpdateFailureReason.Other("Exception: boom")),
            manager.status.value
        )
    }

    @Test
    fun downloadHttpErrorReportsStatusCode() = runTest {
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { _, _ -> throw ApiHttpException(404) }
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkDownloader = downloader
        )

        manager.checkForUpdate(10)
        manager.startUpdate()

        assertEquals(
            UpdateStatus.Failed(UpdateFailure.DOWNLOAD, UpdateFailureReason.HttpStatus(404)),
            manager.status.value
        )
    }

    @Test
    fun downloadTimeoutReportsTimeout() = runTest {
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { _, _ -> throw SocketTimeoutException("timeout") }
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkDownloader = downloader
        )

        manager.checkForUpdate(10)
        manager.startUpdate()

        assertEquals(
            UpdateStatus.Failed(UpdateFailure.DOWNLOAD, UpdateFailureReason.Timeout),
            manager.status.value
        )
    }

    @Test
    fun downloadConnectionFailureReportsUnreachable() = runTest {
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { _, _ -> throw ConnectException("refused") }
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkDownloader = downloader
        )

        manager.checkForUpdate(10)
        manager.startUpdate()

        assertEquals(
            UpdateStatus.Failed(UpdateFailure.DOWNLOAD, UpdateFailureReason.Unreachable),
            manager.status.value
        )
    }

    @Test
    fun installFailureIsReported() = runTest {
        val installer = FakeApkInstaller()
        installer.installHandler = { throw Exception("boom") }
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkInstaller = installer
        )

        manager.checkForUpdate(10)
        manager.startUpdate()

        assertEquals(
            UpdateStatus.Failed(UpdateFailure.INSTALL, UpdateFailureReason.Other("Exception: boom")),
            manager.status.value
        )
    }

    @Test
    fun checkForUpdateIsIgnoredWhileDownloading() = runTest {
        val downloadGate = CompletableDeferred<Unit>()
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { update, onProgress ->
            onProgress(0.5f)
            downloadGate.await()
            File("kidspos-${update.versionCode}.apk")
        }
        val service = updateService()
        val manager = createAppUpdateManager(
            appUpdateService = service,
            apkDownloader = downloader
        )

        manager.checkForUpdate(10)
        manager.startUpdate()
        manager.checkForUpdate(10)

        assertEquals(listOf(10), service.checkedVersionCodes)
        assertEquals(UpdateStatus.Downloading(0.5f), manager.status.value)

        downloadGate.complete(Unit)
    }

    @Test
    fun startUpdateIsIgnoredWhileDownloading() = runTest {
        val downloadGate = CompletableDeferred<Unit>()
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { update, onProgress ->
            onProgress(0.5f)
            downloadGate.await()
            File("kidspos-${update.versionCode}.apk")
        }
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkDownloader = downloader
        )

        manager.checkForUpdate(10)
        manager.startUpdate()
        manager.startUpdate()

        assertEquals(1, downloader.downloadedUpdates.size)

        downloadGate.complete(Unit)
    }

    @Test
    fun dismissIsIgnoredWhileDownloading() = runTest {
        val downloadGate = CompletableDeferred<Unit>()
        val downloader = FakeApkDownloader()
        downloader.downloadHandler = { update, onProgress ->
            onProgress(0.5f)
            downloadGate.await()
            File("kidspos-${update.versionCode}.apk")
        }
        val manager = createAppUpdateManager(
            appUpdateService = updateService(),
            apkDownloader = downloader
        )

        manager.checkForUpdate(10)
        manager.startUpdate()
        manager.dismiss()

        assertEquals(UpdateStatus.Downloading(0.5f), manager.status.value)

        downloadGate.complete(Unit)
    }

    @Test
    fun dismissReturnsToIdle() = runTest {
        val manager = createAppUpdateManager(appUpdateService = updateService())

        manager.checkForUpdate(10)
        manager.dismiss()

        assertEquals(UpdateStatus.Idle, manager.status.value)
    }

    @Test
    fun installResultFromBusUpdatesStatus() = runTest {
        val bus = ApkInstallResultBus()
        val manager = createAppUpdateManager(apkInstallResultBus = bus)

        bus.emit(ApkInstallResult.CANCELLED)
        assertEquals(UpdateStatus.Idle, manager.status.value)

        bus.emit(ApkInstallResult.FAILURE)
        assertEquals(UpdateStatus.Failed(UpdateFailure.INSTALL), manager.status.value)

        bus.emit(ApkInstallResult.SUCCESS)
        assertEquals(UpdateStatus.UpToDate, manager.status.value)
    }

    @Test
    fun installResultEmittedBeforeManagerCreationIsDelivered() = runTest {
        val bus = ApkInstallResultBus()
        bus.emit(ApkInstallResult.FAILURE)

        val manager = createAppUpdateManager(apkInstallResultBus = bus)

        assertEquals(UpdateStatus.Failed(UpdateFailure.INSTALL), manager.status.value)
    }

    @Test
    fun consumedInstallResultIsNotRedeliveredToNewManager() = runTest {
        val bus = ApkInstallResultBus()
        val first = createAppUpdateManager(apkInstallResultBus = bus)
        bus.emit(ApkInstallResult.FAILURE)
        assertEquals(UpdateStatus.Failed(UpdateFailure.INSTALL), first.status.value)

        val second = createAppUpdateManager(apkInstallResultBus = bus)

        assertEquals(UpdateStatus.Idle, second.status.value)
    }
}
