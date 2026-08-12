package info.nukoneko.cuc.android.kidspos.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import timber.log.Timber

// BroadcastReceiver.onReceive は abstract なので、@AndroidEntryPoint が注入のために要求する
// super.onReceive() を Kotlin から呼べない。EntryPoint 経由で同じ Singleton を取得する
class ApkInstallReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ApkInstallEntryPoint {
        fun installResultBus(): ApkInstallResultBus
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_INSTALL_STATUS) return

        val installResultBus = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ApkInstallEntryPoint::class.java
        ).installResultBus()

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
