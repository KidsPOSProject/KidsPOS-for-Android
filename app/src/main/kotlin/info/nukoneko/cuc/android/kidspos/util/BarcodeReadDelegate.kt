package info.nukoneko.cuc.android.kidspos.util

import android.view.KeyEvent
import info.nukoneko.cuc.android.kidspos.error.IllegalBarcodeException

class BarcodeReadDelegate(var listener: OnBarcodeReadListener?) {
    private var readingValue: String = ""

    // android パッケージのKeyEventを受け取りたくない...
    fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) {
            return false
        }
        if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
            if (readingValue.isNotEmpty() && readingValue.length >= 5) {
                // 10桁なら返す
                when {
                    readingValue.length == 10 -> {
                        val prefix = readingValue.substring(2, 4)
                        listener?.onReadSuccess(readingValue, BarcodeKind.prefixOf(prefix))
                    }
                    else -> listener?.onReadFailed(IllegalBarcodeException(readingValue))
                }
            }
            readingValue = ""
            return false

        } else {
            readingValue += (event.keyCode - 7).toString()
        }

        return true
    }

    interface OnBarcodeReadListener {
        fun onReadSuccess(barcode: String, kind: BarcodeKind)

        fun onReadFailed(e: IllegalBarcodeException)
    }
}