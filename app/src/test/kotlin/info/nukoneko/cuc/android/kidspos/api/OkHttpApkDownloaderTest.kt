package info.nukoneko.cuc.android.kidspos.api

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File

// Robolectric の SDK 36 実行は JDK 21 が必要なため、CI の JDK 17 で動く SDK 35 に固定する
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class OkHttpApkDownloaderTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val settingsRepository = fakeSettingsRepository()

    private fun appUpdate(versionCode: Int) = AppUpdate(
        versionName = "9.9.9",
        versionCode = versionCode,
        fileSize = 8,
        releaseNotes = null,
        downloadPath = "/api/apk/download/1"
    )

    private fun stubClient(code: Int = 200, body: String = "apk-data") =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(code)
                    .message("stub")
                    .body(
                        body.toByteArray()
                            .toResponseBody("application/vnd.android.package-archive".toMediaType())
                    )
                    .build()
            }
            .build()

    @Before
    fun setUp() = runTest {
        settingsRepository.setServerAddress("http://127.0.0.1:8080")
    }

    @Test
    fun downloadWritesApkIntoUpdatesDirectory() = runTest {
        val downloader = OkHttpApkDownloader(context, settingsRepository, stubClient())

        val file = downloader.download(appUpdate(versionCode = 2)) {}

        assertEquals("kidspos-2.apk", file.name)
        assertEquals("apk-data", file.readText())
    }

    @Test
    fun downloadDeletesStaleFilesFromPreviousUpdates() = runTest {
        val directory = File(context.cacheDir, "updates").apply { mkdirs() }
        val staleApk = File(directory, "kidspos-1.apk").apply { writeText("old") }
        val downloader = OkHttpApkDownloader(context, settingsRepository, stubClient())

        val file = downloader.download(appUpdate(versionCode = 2)) {}

        assertFalse(staleApk.exists())
        assertTrue(file.exists())
        assertEquals(listOf("kidspos-2.apk"), directory.listFiles()?.map { it.name })
    }

    @Test
    fun downloadReportsProgress() = runTest {
        val downloader = OkHttpApkDownloader(context, settingsRepository, stubClient())
        val progress = mutableListOf<Float>()

        downloader.download(appUpdate(versionCode = 2)) { progress.add(it) }

        assertTrue(progress.isNotEmpty())
        assertEquals(1f, progress.last())
    }

    @Test
    fun downloadFailsOnHttpError() = runTest {
        val downloader = OkHttpApkDownloader(context, settingsRepository, stubClient(code = 500))

        try {
            downloader.download(appUpdate(versionCode = 2)) {}
            fail("expected exception")
        } catch (e: Exception) {
            assertTrue(e.message.orEmpty().contains("500"))
        }
    }
}
