package info.nukoneko.cuc.android.kidspos.ui.common

import android.view.KeyEvent
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.error.IllegalReadValueException
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import info.nukoneko.cuc.android.kidspos.util.BarcodeReadDelegate

abstract class BaseBarcodeReadableActivity : BaseActivity() {

    private val onBarcodeReadListener = object : BarcodeReadDelegate.OnBarcodeReadListener {
        override fun onReadFailed(e: IllegalReadValueException) {
            // TODO
            Logger.e(e, "onReadFailed")
        }

        override fun onReadSuccess(barcode: String, kind: BarcodeKind) {
            onBarcodeInput(barcode, kind)
        }
    }

    private val barcodeDelegate = BarcodeReadDelegate(onBarcodeReadListener)

    abstract fun onBarcodeInput(barcode: String, prefix: BarcodeKind)

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return barcodeDelegate.onKeyEvent(event)
    }
}
