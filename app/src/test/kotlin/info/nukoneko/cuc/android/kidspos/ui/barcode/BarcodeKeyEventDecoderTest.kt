package info.nukoneko.cuc.android.kidspos.ui.barcode

import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BarcodeKeyEventDecoderTest {
    private val decoder = BarcodeKeyEventDecoder()

    private fun typeDigits(digits: String) {
        digits.forEach { digit ->
            assertNull(decoder.onKey(ACTION_DOWN, digit.digitToInt() + KEYCODE_0))
        }
    }

    @Test
    fun tenDigitsFollowedByEnterProducesItemInput() {
        typeDigits("1001000000")
        val result = decoder.onKey(ACTION_DOWN, KEYCODE_ENTER)
        assertEquals(BarcodeInput("1001000000", BarcodeKind.ITEM), result)
    }

    @Test
    fun staffPrefixIsDecodedFromThirdAndFourthDigits() {
        typeDigits("1000000000")
        val result = decoder.onKey(ACTION_DOWN, KEYCODE_ENTER)
        assertEquals(BarcodeInput("1000000000", BarcodeKind.STAFF), result)
    }

    @Test
    fun salePrefixIsDecoded() {
        typeDigits("1002000000")
        val result = decoder.onKey(ACTION_DOWN, KEYCODE_ENTER)
        assertEquals(BarcodeInput("1002000000", BarcodeKind.SALE), result)
    }

    @Test
    fun unmappedPrefixIsDecodedAsUnknown() {
        typeDigits("1099000000")
        val result = decoder.onKey(ACTION_DOWN, KEYCODE_ENTER)
        assertEquals(BarcodeInput("1099000000", BarcodeKind.UNKNOWN), result)
    }

    @Test
    fun enterWithoutTenDigitsProducesNothing() {
        typeDigits("123456789")
        assertNull(decoder.onKey(ACTION_DOWN, KEYCODE_ENTER))
    }

    @Test
    fun bufferIsClearedAfterIncompleteRead() {
        typeDigits("123456789")
        assertNull(decoder.onKey(ACTION_DOWN, KEYCODE_ENTER))
        typeDigits("1001000000")
        val result = decoder.onKey(ACTION_DOWN, KEYCODE_ENTER)
        assertEquals(BarcodeInput("1001000000", BarcodeKind.ITEM), result)
    }

    @Test
    fun bufferIsClearedAfterSuccessfulRead() {
        typeDigits("1001000000")
        decoder.onKey(ACTION_DOWN, KEYCODE_ENTER)
        typeDigits("1000000000")
        val result = decoder.onKey(ACTION_DOWN, KEYCODE_ENTER)
        assertEquals(BarcodeInput("1000000000", BarcodeKind.STAFF), result)
    }

    @Test
    fun nonDownActionsAreIgnored() {
        typeDigits("1001000000")
        assertNull(decoder.onKey(ACTION_UP, KEYCODE_ENTER))
        val result = decoder.onKey(ACTION_DOWN, KEYCODE_ENTER)
        assertEquals(BarcodeInput("1001000000", BarcodeKind.ITEM), result)
    }

    private companion object {
        const val ACTION_DOWN = 0
        const val ACTION_UP = 1
        const val KEYCODE_0 = 7
        const val KEYCODE_ENTER = 66
    }
}
