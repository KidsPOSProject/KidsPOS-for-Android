package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.ServerStatus
import info.nukoneko.cuc.android.kidspos.entity.Store
import java.util.Date

class DemoAPIService : APIService {
    override suspend fun fetchStores(): List<Store> =
        listOf(
            Store(1, "お店1", null),
            Store(2, "お店2", null)
        )

    override suspend fun createSale(
        storeId: Int,
        deposit: Int,
        itemIds: String
    ): Sale = Sale(
        id = 1,
        barcode = "123456",
        createdAt = Date().toString(),
        points = itemIds.split(",").size,
        price = 100,
        items = itemIds,
        storeId = storeId,
        staffId = 0
    )

    override suspend fun getItem(itemBarcode: String): Item =
        Item(
            id = 1,
            barcode = itemBarcode,
            name = "DemoItem",
            price = 100,
            storeId = 1,
            genreId = 1
        )

    override suspend fun getServerStatus(): ServerStatus =
        ServerStatus(
            status = "OK",
            version = "demo",
            apiVersion = APIService.SUPPORTED_API_VERSION
        )
}
