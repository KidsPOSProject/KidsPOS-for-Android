package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store

class FakeAPIService : APIService {
    data class CreateSaleArgs(
        val storeId: Int,
        val staffBarcode: String,
        val deposit: Int,
        val itemIds: String
    )

    val createSaleCalls = mutableListOf<CreateSaleArgs>()

    var fetchStoresHandler: suspend () -> List<Store> = { emptyList() }
    var createSaleHandler: suspend (CreateSaleArgs) -> Sale = { args ->
        Sale(
            id = 1,
            barcode = "1002000000",
            createdAt = "",
            points = 0,
            price = 0,
            items = args.itemIds,
            storeId = args.storeId,
            staffId = 0
        )
    }
    var getItemHandler: suspend (String) -> Item = { barcode ->
        Item(id = 1, barcode = barcode, name = "item", price = 100, storeId = 1, genreId = 1)
    }
    var getStaffHandler: suspend (String) -> Staff = { barcode ->
        Staff(barcode = barcode, name = "staff")
    }

    override suspend fun fetchStores(): List<Store> = fetchStoresHandler()

    override suspend fun createSale(
        storeId: Int,
        staffBarcode: String,
        deposit: Int,
        itemIds: String
    ): Sale {
        val args = CreateSaleArgs(storeId, staffBarcode, deposit, itemIds)
        createSaleCalls += args
        return createSaleHandler(args)
    }

    override suspend fun getItem(itemBarcode: String): Item = getItemHandler(itemBarcode)

    override suspend fun getStaff(staffBarcode: String): Staff = getStaffHandler(staffBarcode)

    override suspend fun getStatus(): Any = Unit
}
