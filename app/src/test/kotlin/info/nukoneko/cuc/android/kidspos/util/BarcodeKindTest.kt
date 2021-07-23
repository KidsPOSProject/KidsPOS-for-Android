package info.nukoneko.cuc.android.kidspos.util

import org.junit.Assert.assertEquals
import org.junit.Test

class BarcodeKindTest {
    @Test
    fun values() {
        val values = arrayOf(
            BarcodeKind.ITEM,
            BarcodeKind.SALE,
            BarcodeKind.STAFF,
            BarcodeKind.UNKNOWN
        )

        assertEquals(values.size, BarcodeKind.values().size)
    }

    @Test
    fun prefixOf() {
        BarcodeKind.values().forEach {
            assertEquals(BarcodeKind.prefixOf(it.prefix), it)
        }

        assertEquals(BarcodeKind.prefixOf(""), BarcodeKind.UNKNOWN)
        assertEquals(BarcodeKind.prefixOf("abc"), BarcodeKind.UNKNOWN)
        assertEquals(BarcodeKind.prefixOf("12341"), BarcodeKind.UNKNOWN)
    }
}
