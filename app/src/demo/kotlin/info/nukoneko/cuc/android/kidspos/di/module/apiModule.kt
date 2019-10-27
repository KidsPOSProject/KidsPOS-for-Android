package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import org.koin.dsl.module
import java.util.*

val apiModule = module {
    single<APIService> {
        object : APIService {
            override fun getStatus(): Deferred<Any> {
                return CompletableDeferred(Any())
            }

            override fun createSale(storeId: Int, staffBarcode: String, deposit: Int, itemIds: String): Deferred<Sale> {
                val sale = Sale(1, "123456", Date(), itemIds.split(",").size, 0, itemIds, storeId, 0)
                return CompletableDeferred(sale)
            }

            override fun fetchStores(): Deferred<List<Store>> {
                return CompletableDeferred(listOf(Store(1, "お店1"), Store(2, "お店2")))
            }

            override fun getItem(itemBarcode: String): Deferred<Item> {
                return CompletableDeferred(Item(1, itemBarcode, "DemoItem", 100, 1, 1))
            }

            override fun getStaff(staffBarcode: String): Deferred<Staff> {
                return CompletableDeferred(Staff(staffBarcode, "DemoStaff"))
            }
        }
    }
}