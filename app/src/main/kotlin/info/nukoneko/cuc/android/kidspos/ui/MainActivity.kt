package info.nukoneko.cuc.android.kidspos.ui

import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeKeyEventDecoder
import info.nukoneko.cuc.android.kidspos.ui.theme.KidsPosTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var barcodeEventBus: BarcodeEventBus

    private val decoder = BarcodeKeyEventDecoder()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        val splashStartTime = SystemClock.elapsedRealtime()
        splashScreen.setKeepOnScreenCondition {
            SystemClock.elapsedRealtime() - splashStartTime < SPLASH_DURATION_MS
        }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KidsPosTheme {
                AppNavHost()
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val input = decoder.onKey(event.action, event.keyCode)
        if (input != null) {
            barcodeEventBus.emit(input)
        }
        return super.dispatchKeyEvent(event)
    }

    private companion object {
        const val SPLASH_DURATION_MS = 2000L
    }
}
