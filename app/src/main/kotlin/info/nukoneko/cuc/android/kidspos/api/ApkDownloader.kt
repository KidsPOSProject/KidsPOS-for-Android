package info.nukoneko.cuc.android.kidspos.api

import android.content.Context
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

interface ApkDownloader {
    suspend fun download(update: AppUpdate, onProgress: (Float) -> Unit): File
}

class OkHttpApkDownloader(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val client: OkHttpClient
) : ApkDownloader {

    override suspend fun download(update: AppUpdate, onProgress: (Float) -> Unit): File {
        val server = settingsRepository.serverAddress.first().toHttpUrlOrNull()
            ?: throw Exception("Invalid server address")
        val url = server.newBuilder().encodedPath(update.downloadPath).build()

        val directory = File(context.cacheDir, DOWNLOAD_DIRECTORY)
        directory.mkdirs()
        val destination = File(directory, "kidspos-${update.versionCode}.apk")

        client.newCall(Request.Builder().url(url).build()).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to download apk: ${response.code}")
            }
            val body = response.body
            val total = body.contentLength().takeIf { it > 0 } ?: update.fileSize
            body.byteStream().use { source ->
                destination.outputStream().use { sink ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var copied = 0L
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        val read = source.read(buffer)
                        if (read < 0) break
                        sink.write(buffer, 0, read)
                        copied += read
                        if (total > 0) {
                            onProgress((copied.toFloat() / total).coerceIn(0f, 1f))
                        }
                    }
                }
            }
        }
        return destination
    }

    private companion object {
        const val DOWNLOAD_DIRECTORY = "updates"
    }
}
