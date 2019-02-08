package info.nukoneko.cuc.android.kidspos.support

import info.nukoneko.cuc.android.kidspos.TestApp
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.reflect.Method

class KidsPOSRobolectricTestRunner(testClass: Class<*>?) : RobolectricTestRunner(testClass) {
    override fun getConfig(method: Method): Config {
        val defaultConfig = super.getConfig(method)
        return Config.Implementation(
                intArrayOf(26), defaultConfig.minSdk,
                defaultConfig.maxSdk,
                defaultConfig.manifest,
                defaultConfig.qualifiers,
                defaultConfig.packageName,
                defaultConfig.resourceDir,
                defaultConfig.assetDir,
                arrayOf(),
                defaultConfig.instrumentedPackages,
                TestApp::class.java,
                defaultConfig.libraries
        )
    }
}