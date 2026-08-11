package info.nukoneko.cuc.android.kidspos.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ApkInstallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var installResultBus: ApkInstallResultBus

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != ACTION_INSTALL_STATUS) return

        when (val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, Int.MIN_VALUE)) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                @Suppress("DEPRECATION")
                val confirmIntent = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
                if (confirmIntent == null) {
                    installResultBus.emit(ApkInstallResult.FAILURE)
                } else {
                    confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(confirmIntent)
                }
            }

            PackageInstaller.STATUS_SUCCESS -> installResultBus.emit(ApkInstallResult.SUCCESS)

            PackageInstaller.STATUS_FAILURE_ABORTED ->
                installResultBus.emit(ApkInstallResult.CANCELLED)

            else -> {
                Timber.w(
                    "Apk install failed: status=%d, message=%s",
                    status,
                    intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                )
                installResultBus.emit(ApkInstallResult.FAILURE)
            }
        }
    }

    companion object {
        const val ACTION_INSTALL_STATUS =
            "info.nukoneko.cuc.android.kidspos.ACTION_INSTALL_STATUS"
    }
}
