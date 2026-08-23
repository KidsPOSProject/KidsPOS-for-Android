package info.nukoneko.cuc.android.kidspos.ui.barcode

import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BarcodeKeyEventDecoderTest {
    private val decoder = BarcodeKeyEventDecoder()

    private fun keyCodeOf(char: Char): Int = if (char in '0'..'9') {
        char - '0' + KEYCODE_0
    } else {
        char.uppercaseChar() - 'A' + KEYCODE_A
    }

    private fun press(char: Char): BarcodeKeyResult {
        val keyCode = keyCodeOf(char)
        val down = decoder.onKey(ACTION_DOWN, keyCode, char.code)
        decoder.onKey(ACTION_UP, keyCode, char.code)
        return down
    }

    private fun pressEnter(): BarcodeKeyResult = decoder.onKey(ACTION_DOWN, KEYCODE_ENTER, 0)

    private fun type(value: String) {
        value.forEach { press(it) }
    }

    private fun scan(value: String): BarcodeInput? {
        type(value)
        return pressEnter().input
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
        decoder.onKey(ACTION_DOWN, KEYCODE_SHIFT_LEFT, 0)
        decoder.onKey(ACTION_UP, KEYCODE_SHIFT_LEFT, 0)
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
    fun eleventhCharacterStartsANewRead() {
        type("0000000000")

        assertEquals(BarcodeInput("A01000008A", BarcodeKind.ITEM), scan("A01000008A"))
    }

    @Test
    fun unsupportedCharacterClearsTheBuffer() {
        type("A0100")
        decoder.onKey(ACTION_DOWN, KEYCODE_MINUS, '-'.code)
        type("0008A")

        assertNull(pressEnter().input)
    }

    @Test
    fun everyScannedCharacterIsConsumedIncludingTheFirstOne() {
        assertTrue(press('A').consumed)
        assertTrue(press('0').consumed)
        assertTrue(press('1').consumed)
    }

    @Test
    fun enterIsConsumedEvenWhenTheReadFailed() {
        type("123456789")

        assertTrue(pressEnter().consumed)
    }

    @Test
    fun keyUpOfAConsumedKeyIsAlsoConsumed() {
        assertTrue(decoder.onKey(ACTION_UP, keyCodeOf('1'), '1'.code).consumed)
        assertTrue(decoder.onKey(ACTION_UP, KEYCODE_ENTER, 0).consumed)
    }

    @Test
    fun keyUpOfEnterDoesNotEmitABarcode() {
        type("A01000008A")

        assertNull(decoder.onKey(ACTION_UP, KEYCODE_ENTER, 0).input)
        assertEquals(BarcodeInput("A01000008A", BarcodeKind.ITEM), pressEnter().input)
    }

    @Test
    fun keysWithoutACharacterArePassedToTheScreen() {
        assertFalse(decoder.onKey(ACTION_DOWN, KEYCODE_BACK, 0).consumed)
        assertFalse(decoder.onKey(ACTION_DOWN, KEYCODE_DEL, 0).consumed)
    }

    @Test
    fun modifierKeysArePassedToTheScreen() {
        assertFalse(decoder.onKey(ACTION_DOWN, KEYCODE_SHIFT_LEFT, 0).consumed)
    }

    @Test
    fun unsupportedCharacterIsPassedToTheScreen() {
        assertFalse(decoder.onKey(ACTION_DOWN, KEYCODE_MINUS, '-'.code).consumed)
    }

    private companion object {
        const val ACTION_DOWN = 0
        const val ACTION_UP = 1
        const val KEYCODE_0 = 7
        const val KEYCODE_A = 29
        const val KEYCODE_SHIFT_LEFT = 59
        const val KEYCODE_BACK = 4
        const val KEYCODE_ENTER = 66
        const val KEYCODE_DEL = 67
        const val KEYCODE_MINUS = 69
    }
}
