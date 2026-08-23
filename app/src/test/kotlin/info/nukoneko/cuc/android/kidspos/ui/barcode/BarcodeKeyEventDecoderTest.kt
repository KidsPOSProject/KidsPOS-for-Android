package info.nukoneko.cuc.android.kidspos.ui.barcode

import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BarcodeKeyEventDecoderTest {
    private val decoder = BarcodeKeyEventDecoder()
    private var now = 10_000L

    private fun keyCodeOf(char: Char): Int = if (char in '0'..'9') {
        char - '0' + KEYCODE_0
    } else {
        char.uppercaseChar() - 'A' + KEYCODE_A
    }

    private fun press(char: Char, gap: Long = SCAN_GAP): BarcodeKeyResult {
        now += gap
        val keyCode = keyCodeOf(char)
        val down = decoder.onKey(ACTION_DOWN, keyCode, char.code, now)
        decoder.onKey(ACTION_UP, keyCode, char.code, now)
        return down
    }

    private fun pressEnter(gap: Long = SCAN_GAP): BarcodeKeyResult {
        now += gap
        return decoder.onKey(ACTION_DOWN, KEYCODE_ENTER, 0, now)
    }

    private fun type(value: String, gap: Long = SCAN_GAP) {
        value.forEach { press(it, gap) }
    }

    private fun scan(value: String, gap: Long = SCAN_GAP): BarcodeInput? {
        type(value, gap)
        return pressEnter(gap).input
    }

    @Test
    fun numericItemBarcodeIsDecoded() {
        assertEquals(BarcodeInput("1001000000", BarcodeKind.ITEM), scan("1001000000"))
    }

    @Test
    fun numericStaffBarcodeIsDecodedFromThirdAndFourthDigits() {
        assertEquals(BarcodeInput("1000000000", BarcodeKind.STAFF), scan("1000000000"))
    }

    @Test
    fun numericSaleBarcodeIsDecoded() {
        assertEquals(BarcodeInput("1002000000", BarcodeKind.SALE), scan("1002000000"))
    }

    @Test
    fun unmappedNumericPrefixIsDecodedAsUnknown() {
        assertEquals(BarcodeInput("1099000000", BarcodeKind.UNKNOWN), scan("1099000000"))
    }

    @Test
    fun alphanumericItemBarcodeIsDecoded() {
        assertEquals(BarcodeInput("A01000008A", BarcodeKind.ITEM), scan("A01000008A"))
    }

    @Test
    fun alphanumericStaffBarcodeIsDecodedFromSecondAndThirdCharacters() {
        assertEquals(BarcodeInput("A00000001A", BarcodeKind.STAFF), scan("A00000001A"))
    }

    @Test
    fun alphanumericSaleBarcodeIsDecoded() {
        assertEquals(BarcodeInput("A02000001A", BarcodeKind.SALE), scan("A02000001A"))
    }

    @Test
    fun unmappedAlphanumericPrefixIsDecodedAsUnknown() {
        assertEquals(BarcodeInput("A99000001A", BarcodeKind.UNKNOWN), scan("A99000001A"))
    }

    @Test
    fun lowercaseInputIsNormalizedToUppercase() {
        assertEquals(BarcodeInput("A01000008A", BarcodeKind.ITEM), scan("a01000008a"))
    }

    @Test
    fun shiftKeyBetweenCharactersDoesNotBreakTheRead() {
        press('A')
        now += SCAN_GAP
        decoder.onKey(ACTION_DOWN, KEYCODE_SHIFT_LEFT, 0, now)
        decoder.onKey(ACTION_UP, KEYCODE_SHIFT_LEFT, 0, now)
        type("01000008A")

        assertEquals(BarcodeInput("A01000008A", BarcodeKind.ITEM), pressEnter().input)
    }

    @Test
    fun enterWithoutTenCharactersProducesNothing() {
        assertNull(scan("123456789"))
    }

    @Test
    fun bufferIsClearedAfterIncompleteRead() {
        assertNull(scan("123456789"))

        assertEquals(BarcodeInput("1001000000", BarcodeKind.ITEM), scan("1001000000"))
    }

    @Test
    fun bufferIsClearedAfterSuccessfulRead() {
        scan("1001000000")

        assertEquals(BarcodeInput("1000000000", BarcodeKind.STAFF), scan("1000000000"))
    }

    @Test
    fun unsupportedCharacterClearsTheBuffer() {
        type("A0100")
        now += SCAN_GAP
        decoder.onKey(ACTION_DOWN, KEYCODE_MINUS, '-'.code, now)
        type("0008A")

        assertNull(pressEnter().input)
    }

    @Test
    fun scannedCharactersAreConsumedSoTheScreenDoesNotSeeThem() {
        assertFalse(press('A').consumed)
        assertTrue(press('0').consumed)
        assertTrue(press('1').consumed)
    }

    @Test
    fun enterIsConsumedOnlyWhenABarcodeWasRead() {
        type("123456789")
        assertFalse(pressEnter().consumed)

        type("A01000008A")
        assertTrue(pressEnter().consumed)
    }

    @Test
    fun keyUpOfAConsumedKeyIsAlsoConsumed() {
        type("A0")
        now += SCAN_GAP
        assertTrue(decoder.onKey(ACTION_DOWN, keyCodeOf('1'), '1'.code, now).consumed)
        assertTrue(decoder.onKey(ACTION_UP, keyCodeOf('1'), '1'.code, now).consumed)
    }

    @Test
    fun keyUpOfAnUntouchedKeyIsNotConsumed() {
        assertFalse(decoder.onKey(ACTION_UP, KEYCODE_ENTER, 0, now).consumed)
    }

    @Test
    fun slowlyTypedCharactersAreNotConsumedButAreStillRead() {
        val slowGap = BarcodeKeyEventDecoder.SCAN_GAP_MILLIS + 100L
        "A01000008A".forEach { char ->
            assertFalse(press(char, slowGap).consumed)
        }

        assertEquals(BarcodeInput("A01000008A", BarcodeKind.ITEM), pressEnter(slowGap).input)
    }

    private companion object {
        const val ACTION_DOWN = 0
        const val ACTION_UP = 1
        const val KEYCODE_0 = 7
        const val KEYCODE_A = 29
        const val KEYCODE_SHIFT_LEFT = 59
        const val KEYCODE_ENTER = 66
        const val KEYCODE_MINUS = 69
        const val SCAN_GAP = 10L
    }
}
