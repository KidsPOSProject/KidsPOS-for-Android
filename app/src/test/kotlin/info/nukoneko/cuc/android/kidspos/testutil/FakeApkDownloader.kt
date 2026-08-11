package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.api.ApkDownloader
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import java.io.File

class FakeApkDownloader : ApkDownloader {
    val downloadedUpdates = mutableListOf<AppUpdate>()

    var downloadHandler: suspend (AppUpdate, (Float) -> Unit) -> File = { update, onProgress ->
        onProgress(1f)
        File("kidspos-${update.versionCode}.apk")
    }

    override suspend fun download(update: AppUpdate, onProgress: (Float) -> Unit): File {
        downloadedUpdates += update
        return downloadHandler(update, onProgress)
    }
}
