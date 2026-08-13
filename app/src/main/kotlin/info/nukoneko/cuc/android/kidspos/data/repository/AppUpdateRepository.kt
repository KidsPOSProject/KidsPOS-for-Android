package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.api.ApkDownloader
import info.nukoneko.cuc.android.kidspos.api.AppUpdateService
import info.nukoneko.cuc.android.kidspos.di.hilt.IoDispatcher
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUpdateRepository @Inject constructor(
    private val appUpdateService: AppUpdateService,
    private val apkDownloader: ApkDownloader,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun checkForUpdate(currentVersionCode: Int): AppUpdate? = withContext(dispatcher) {
        appUpdateService.checkForUpdate(currentVersionCode)
    }

    suspend fun downloadApk(update: AppUpdate, onProgress: (Float) -> Unit): File =
        withContext(dispatcher) {
            apkDownloader.download(update, onProgress)
        }
}
