package info.nukoneko.cuc.android.kidspos.ui.barcode

import app.cash.turbine.test
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BarcodeEventBusTest {

    @Test
    fun emittedInputIsDeliveredToCollector() = runTest {
        val bus = BarcodeEventBus()
        bus.events.test {
            bus.emit(BarcodeInput("1001000000", BarcodeKind.ITEM))
            assertEquals(BarcodeInput("1001000000", BarcodeKind.ITEM), awaitItem())
        }
    }

    @Test
    fun multipleInputsAreDeliveredInOrder() = runTest {
        val bus = BarcodeEventBus()
        bus.events.test {
            bus.emit(BarcodeInput("1000000000", BarcodeKind.STAFF))
            assertEquals(BarcodeKind.STAFF, awaitItem().kind)
            bus.emit(BarcodeInput("1001000000", BarcodeKind.ITEM))
            assertEquals(BarcodeKind.ITEM, awaitItem().kind)
        }
    }
}
