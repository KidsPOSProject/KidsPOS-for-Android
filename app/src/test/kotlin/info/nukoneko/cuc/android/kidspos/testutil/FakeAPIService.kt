package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.ServerStatus
import info.nukoneko.cuc.android.kidspos.entity.Store

class FakeAPIService : APIService {
    data class CreateSaleArgs(
        val storeId: Int,
        val deposit: Int,
        val itemIds: String
    )

    val createSaleCalls = mutableListOf<CreateSaleArgs>()
    val fetchStoresCalls = mutableListOf<Unit>()
    val getItemCalls = mutableListOf<String>()
    val fetchItemsCalls = mutableListOf<Unit>()
    val getServerStatusCalls = mutableListOf<Unit>()

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
    var fetchItemsHandler: suspend () -> List<Item> = { emptyList() }
    var getServerStatusHandler: suspend () -> ServerStatus = {
        ServerStatus(
            status = "OK",
            version = "1.0.0",
            apiVersion = APIService.SUPPORTED_API_VERSION
        )
    }

    override suspend fun fetchStores(): List<Store> {
        fetchStoresCalls += Unit
        return fetchStoresHandler()
    }

    override suspend fun createSale(
        storeId: Int,
        deposit: Int,
        itemIds: String
    ): Sale {
        val args = CreateSaleArgs(storeId, deposit, itemIds)
        createSaleCalls += args
        return createSaleHandler(args)
    }

    override suspend fun getItem(itemBarcode: String): Item {
        getItemCalls += itemBarcode
        return getItemHandler(itemBarcode)
    }

    override suspend fun fetchItems(): List<Item> {
        fetchItemsCalls += Unit
        return fetchItemsHandler()
    }

    override suspend fun getServerStatus(): ServerStatus {
        getServerStatusCalls += Unit
        return getServerStatusHandler()
    }
}
