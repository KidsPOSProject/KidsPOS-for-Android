package info.nukoneko.cuc.android.kidspos.update

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File

// Robolectric の SDK 36 実行は JDK 21 が必要なため、CI の JDK 17 で動く SDK 35 に固定する
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class PackageInstallerApkInstallerTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun installFailureAbandonsCreatedSession() = runTest {
        val installer = PackageInstallerApkInstaller(context, Dispatchers.Unconfined)
        val missingApk = File(context.cacheDir, "missing.apk")

        try {
            installer.install(missingApk)
            fail("expected exception")
        } catch (e: Exception) {
            assertTrue(
                context.packageManager.packageInstaller.mySessions.isEmpty()
            )
        }
    }
}
