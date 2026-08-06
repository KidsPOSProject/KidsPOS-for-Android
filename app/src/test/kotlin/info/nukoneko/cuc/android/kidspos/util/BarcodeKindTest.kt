package info.nukoneko.cuc.android.kidspos.util

import org.junit.Assert.assertEquals
import org.junit.Test

class BarcodeKindTest {

    @Test
    fun prefixOfReturnsStaff() {
        assertEquals(BarcodeKind.STAFF, BarcodeKind.prefixOf("00"))
    }

    @Test
    fun prefixOfReturnsItem() {
        assertEquals(BarcodeKind.ITEM, BarcodeKind.prefixOf("01"))
    }

    @Test
    fun prefixOfReturnsSale() {
        assertEquals(BarcodeKind.SALE, BarcodeKind.prefixOf("02"))
    }

    @Test
    fun prefixOfReturnsUnknownForUnmappedPrefix() {
        assertEquals(BarcodeKind.UNKNOWN, BarcodeKind.prefixOf("03"))
        assertEquals(BarcodeKind.UNKNOWN, BarcodeKind.prefixOf(""))
        assertEquals(BarcodeKind.UNKNOWN, BarcodeKind.prefixOf("0"))
    }
}
