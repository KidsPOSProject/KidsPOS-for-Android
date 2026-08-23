package info.nukoneko.cuc.android.kidspos.ui.barcode

import info.nukoneko.cuc.android.kidspos.util.BarcodeKind

data class BarcodeKeyResult(
    val consumed: Boolean = false,
    val input: BarcodeInput? = null
)

class BarcodeKeyEventDecoder {
    private val reading = StringBuilder()
    private val consumedKeys = mutableSetOf<Int>()
    private var lastKeyTime = NO_KEY_TIME

    fun onKey(
        action: Int,
        keyCode: Int,
        unicodeChar: Int,
        eventTimeMillis: Long
    ): BarcodeKeyResult {
        if (action == ACTION_UP) {
            return BarcodeKeyResult(consumed = consumedKeys.remove(keyCode))
        }
        if (action != ACTION_DOWN) {
            return BarcodeKeyResult()
        }
        if (keyCode in MODIFIER_KEY_CODES) {
            return BarcodeKeyResult()
        }
        if (keyCode in ENTER_KEY_CODES) {
            return onEnter(keyCode)
        }
        return onCharacter(keyCode, unicodeChar, eventTimeMillis)
    }

    private fun onEnter(keyCode: Int): BarcodeKeyResult {
        val input = parse(reading.toString())
        reading.setLength(0)
        lastKeyTime = NO_KEY_TIME
        if (input == null) {
            return BarcodeKeyResult()
        }
        consumedKeys += keyCode
        return BarcodeKeyResult(consumed = true, input = input)
    }

    private fun onCharacter(
        keyCode: Int,
        unicodeChar: Int,
        eventTimeMillis: Long
    ): BarcodeKeyResult {
        val char = unicodeChar.toChar()
        if (unicodeChar == 0 || !isBarcodeChar(char)) {
            reading.setLength(0)
            lastKeyTime = NO_KEY_TIME
            return BarcodeKeyResult()
        }
        // 人が手で打つ速さでは読み取り機とみなさず、キーを画面側へ通す
        val burst = reading.isNotEmpty() && eventTimeMillis - lastKeyTime <= SCAN_GAP_MILLIS
        if (reading.length >= BARCODE_LENGTH) {
            reading.setLength(0)
        }
        reading.append(char.uppercaseChar())
        lastKeyTime = eventTimeMillis
        if (!burst) {
            return BarcodeKeyResult()
        }
        consumedKeys += keyCode
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

    companion object {
        const val SCAN_GAP_MILLIS = 200L

        private const val NO_KEY_TIME = 0L
        private const val ACTION_DOWN = 0
        private const val ACTION_UP = 1
        private const val BARCODE_LENGTH = 10
        private const val PREFIX_LENGTH = 2
        private const val NUMERIC_PREFIX_START = 2
        private const val ALPHA_PREFIX_START = 1
        private val ENTER_KEY_CODES = setOf(66, 160)
        private val MODIFIER_KEY_CODES = setOf(59, 60, 57, 58, 113, 114)
    }
}
