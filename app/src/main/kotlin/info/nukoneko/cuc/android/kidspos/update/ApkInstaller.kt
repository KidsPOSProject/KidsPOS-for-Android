package info.nukoneko.cuc.android.kidspos.update

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

interface ApkInstaller {
    fun canRequestInstall(): Boolean
    suspend fun install(apk: File)
}

class PackageInstallerApkInstaller(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher
) : ApkInstaller {

    override fun canRequestInstall(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
            context.packageManager.canRequestPackageInstalls()

    override suspend fun install(apk: File) = withContext(dispatcher) {
        val installer = context.packageManager.packageInstaller
        val params =
            PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val sessionId = installer.createSession(params)
        try {
            installer.openSession(sessionId).use { session ->
                session.openWrite(WRITE_NAME, 0, apk.length()).use { sink ->
                    apk.inputStream().use { source -> source.copyTo(sink) }
                    session.fsync(sink)
                }
                session.commit(createStatusIntent(sessionId).intentSender)
            }
        } catch (e: Exception) {
            installer.abandonSession(sessionId)
            throw e
        }
    }

    private fun createStatusIntent(sessionId: Int): PendingIntent {
        val intent = Intent(context, ApkInstallReceiver::class.java)
            .setAction(ApkInstallReceiver.ACTION_INSTALL_STATUS)
        var flags = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = flags or PendingIntent.FLAG_MUTABLE
        }
        return PendingIntent.getBroadcast(context, sessionId, intent, flags)
    }

    private companion object {
        const val WRITE_NAME = "kidspos"
    }
}
