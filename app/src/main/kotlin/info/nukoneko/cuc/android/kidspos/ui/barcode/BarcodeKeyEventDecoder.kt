package info.nukoneko.cuc.android.kidspos.ui.barcode

import info.nukoneko.cuc.android.kidspos.util.BarcodeKind

data class BarcodeKeyResult(
    val consumed: Boolean = false,
    val input: BarcodeInput? = null
)

class BarcodeKeyEventDecoder {
    private val reading = StringBuilder()

    fun onKey(action: Int, keyCode: Int, unicodeChar: Int): BarcodeKeyResult {
        if (keyCode in MODIFIER_KEY_CODES) {
            return BarcodeKeyResult()
        }
        if (keyCode in ENTER_KEY_CODES) {
            return onEnter(action)
        }
        return onCharacter(action, unicodeChar)
    }

    private fun onEnter(action: Int): BarcodeKeyResult {
        if (action != ACTION_DOWN) {
            return BarcodeKeyResult(consumed = true)
        }
        val input = parse(reading.toString())
        reading.setLength(0)
        return BarcodeKeyResult(consumed = true, input = input)
    }

    private fun onCharacter(action: Int, unicodeChar: Int): BarcodeKeyResult {
        val char = unicodeChar.toChar()
        if (unicodeChar == 0 || !isBarcodeChar(char)) {
            if (action == ACTION_DOWN) {
                reading.setLength(0)
            }
            return BarcodeKeyResult()
        }
        if (action == ACTION_DOWN) {
            if (reading.length >= BARCODE_LENGTH) {
                reading.setLength(0)
            }
            reading.append(char.uppercaseChar())
        }
        return BarcodeKeyResult(consumed = true)
    }

    private fun parse(value: String): BarcodeInput? {
        if (value.length != BARCODE_LENGTH) {
            return null
        }
        val prefix = if (value.first().isDigit()) {
            value.substring(NUMERIC_PREFIX_START, NUMERIC_PREFIX_START + PREFIX_LENGTH)
        } else {
            value.substring(ALPHA_PREFIX_START, ALPHA_PREFIX_START + PREFIX_LENGTH)
        }
        return BarcodeInput(value, BarcodeKind.prefixOf(prefix))
    }

    private fun isBarcodeChar(char: Char): Boolean =
        char in '0'..'9' || char in 'A'..'Z' || char in 'a'..'z'

    private companion object {
        const val ACTION_DOWN = 0
        const val BARCODE_LENGTH = 10
        const val PREFIX_LENGTH = 2
        const val NUMERIC_PREFIX_START = 2
        const val ALPHA_PREFIX_START = 1
        val ENTER_KEY_CODES = setOf(66, 160)
        val MODIFIER_KEY_CODES = setOf(59, 60, 57, 58, 113, 114)
    }
}
