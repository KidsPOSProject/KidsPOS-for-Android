package info.nukoneko.cuc.android.kidspos.ui.barcode

import info.nukoneko.cuc.android.kidspos.util.BarcodeKind

class BarcodeKeyEventDecoder {
    private var readingValue: String = ""

    fun onKey(action: Int, keyCode: Int): BarcodeInput? {
        if (action != ACTION_DOWN) {
            return null
        }
        if (keyCode == KEYCODE_ENTER) {
            var result: BarcodeInput? = null
            if (readingValue.length == 10) {
                val prefix = readingValue.substring(2, 4)
                result = BarcodeInput(readingValue, BarcodeKind.prefixOf(prefix))
            }
            readingValue = ""
            return result
        }
        readingValue += (keyCode - 7).toString()
        return null
    }

    companion object {
        private const val ACTION_DOWN = 0
        private const val KEYCODE_ENTER = 66
    }
}
