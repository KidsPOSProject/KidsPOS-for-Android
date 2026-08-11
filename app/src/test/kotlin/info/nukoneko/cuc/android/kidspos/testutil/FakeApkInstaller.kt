package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.update.ApkInstaller
import java.io.File

class FakeApkInstaller : ApkInstaller {
    val installedApks = mutableListOf<File>()

    var installAllowed = true
    var installHandler: (File) -> Unit = {}

    override fun canRequestInstall(): Boolean = installAllowed

    override suspend fun install(apk: File) {
        installedApks += apk
        installHandler(apk)
    }
}
