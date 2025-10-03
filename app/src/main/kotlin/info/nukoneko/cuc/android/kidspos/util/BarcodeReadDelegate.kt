package info.nukoneko.cuc.android.kidspos.util

import android.view.KeyEvent
import com.orhanobut.logger.Logger
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
                Logger.d("Barcode read: raw=$readingValue")

                // 両脇が 5222 だったらそれぞれ A に変換する
                var processedValue = readingValue
                if (processedValue.startsWith("5222") && processedValue.endsWith("5222")) {
                    processedValue =
                        "A" + processedValue.substring(4, processedValue.length - 4) + "A"
                    Logger.d("Barcode converted: $readingValue -> $processedValue")
                }

                // 10桁なら返す
                when (processedValue.length) {
                    10 -> {
                        listener?.onReadSuccess(processedValue)
                    }

                    else -> {
                        Logger.e("Barcode failed: invalid length (${processedValue.length}): $processedValue")
                        listener?.onReadFailed(IllegalBarcodeException(processedValue))
                    }
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
        fun onReadSuccess(barcode: String)

        fun onReadFailed(e: IllegalBarcodeException)
    }
}
