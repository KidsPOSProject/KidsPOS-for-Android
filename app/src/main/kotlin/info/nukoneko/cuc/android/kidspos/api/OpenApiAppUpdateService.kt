package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.api.generated.ApkApi
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate

class OpenApiAppUpdateService(
    private val apkApi: ApkApi
) : AppUpdateService {

    override suspend fun checkForUpdate(currentVersionCode: Int): AppUpdate? {
        val response = apkApi.checkApkUpdate(currentVersionCode)
        if (!response.isSuccessful) {
            throw ApiHttpException(response.code())
        }
        val body = response.body()!!
        if (!body.hasUpdate) return null
        val latest = body.latestVersion ?: return null
        return AppUpdate(
            versionName = latest.version,
            versionCode = latest.versionCode,
            fileSize = latest.fileSize,
            releaseNotes = latest.releaseNotes,
            downloadPath = latest.downloadUrl
        )
    }
}
