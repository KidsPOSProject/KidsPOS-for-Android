package info.nukoneko.cuc.android.kidspos.ui.common

import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.error.IllegalBarcodeException
import info.nukoneko.cuc.android.kidspos.util.BarcodeReadDelegate

abstract class BaseBarcodeReadableActivity : AppCompatActivity() {

    private val onBarcodeReadListener = object : BarcodeReadDelegate.OnBarcodeReadListener {
        override fun onReadFailed(e: IllegalBarcodeException) {
            Logger.e(e, "onReadFailed")
        }

        override fun onReadSuccess(barcode: String) {
            onBarcodeInput(barcode)
        }
    }

    private val barcodeDelegate = BarcodeReadDelegate(onBarcodeReadListener)

    abstract fun onBarcodeInput(barcode: String)

    final override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return barcodeDelegate.onKeyEvent(event)
    }
}
