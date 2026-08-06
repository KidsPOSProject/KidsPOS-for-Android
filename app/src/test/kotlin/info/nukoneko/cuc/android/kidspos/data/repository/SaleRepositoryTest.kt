package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.testutil.FakeAPIService
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SaleRepositoryTest {

    private fun item(id: Int) = Item(
        id = id,
        barcode = "100100000$id",
        name = "item$id",
        price = 100,
        storeId = 1,
        genreId = 1
    )

    @Test
    fun createSaleJoinsItemIdsWithComma() = runTest {
        val apiService = FakeAPIService()
        val repository = SaleRepository(apiService, StandardTestDispatcher(testScheduler))

        repository.createSale(
            storeId = 3,
            staffBarcode = "1000000001",
            deposit = 500,
            items = listOf(item(1), item(2), item(3))
        )

        val args = apiService.createSaleCalls.single()
        assertEquals(3, args.storeId)
        assertEquals("1000000001", args.staffBarcode)
        assertEquals(500, args.deposit)
        assertEquals("1,2,3", args.itemIds)
    }

    @Test
    fun createSaleWithSingleItemSendsBareId() = runTest {
        val apiService = FakeAPIService()
        val repository = SaleRepository(apiService, StandardTestDispatcher(testScheduler))

        repository.createSale(
            storeId = 1,
            staffBarcode = "",
            deposit = 100,
            items = listOf(item(7))
        )

        assertEquals("7", apiService.createSaleCalls.single().itemIds)
    }
}
