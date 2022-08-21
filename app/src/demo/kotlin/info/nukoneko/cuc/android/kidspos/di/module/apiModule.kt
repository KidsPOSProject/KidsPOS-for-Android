package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import org.koin.dsl.module
import java.util.*

val apiModule = module {
    single<APIService> {
        object : APIService {
            override suspend fun getStatus(): Any = Any()

            override suspend fun createSale(
                storeId: Int,
                staffBarcode: String,
                deposit: Int,
                itemIds: String
            ): Sale = Sale(1, "123456", Date(), itemIds.split(",").size, 0, itemIds, storeId, 0)

            override suspend fun fetchStores(): List<Store> =
                listOf(Store(1, "お店1"), Store(2, "お店2"))

            override suspend fun getItem(itemBarcode: String): Item =
                Item(1, itemBarcode, "DemoItem", 100, 1, 1)

            override suspend fun getStaff(staffBarcode: String): Staff =
                Staff(staffBarcode, "DemoStaff")
        }
    }
}
