package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import org.koin.dsl.module
import java.util.*

/**
 * Demo用のAPIService実装
 * ローカルで動作確認用のモックデータを返す
 */
class DemoAPIService : APIService(
    itemsApi = throw NotImplementedError("Demo mode - itemsApi not used"),
    salesApi = throw NotImplementedError("Demo mode - salesApi not used"),
    staffApi = throw NotImplementedError("Demo mode - staffApi not used"),
    storesApi = throw NotImplementedError("Demo mode - storesApi not used"),
    settingsApi = throw NotImplementedError("Demo mode - settingsApi not used")
) {
    override suspend fun getStatus(): Any = mapOf("status" to "OK", "mode" to "demo")

    override suspend fun createSale(
        storeId: Int,
        staffBarcode: String,
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

    override suspend fun fetchStores(): List<Store> =
        listOf(
            Store(1, "お店1", null),
            Store(2, "お店2", null)
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

    override suspend fun getStaff(staffBarcode: String): Staff =
        Staff(staffBarcode, "DemoStaff")
}

val apiModule = module {
    // Demo用のAPIServiceを登録
    single {
        DemoAPIService()
    }
}
