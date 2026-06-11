package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.api.generated.ItemsApi
import info.nukoneko.cuc.android.kidspos.api.generated.SalesApi
import info.nukoneko.cuc.android.kidspos.api.generated.StaffApi
import info.nukoneko.cuc.android.kidspos.api.generated.StoresApi
import info.nukoneko.cuc.android.kidspos.api.generated.model.CreateSaleRequest
import info.nukoneko.cuc.android.kidspos.api.generated.model.CreateSaleRequestItemsInner
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store

class OpenApiAPIService(
    private val itemsApi: ItemsApi,
    private val salesApi: SalesApi,
    private val staffApi: StaffApi,
    private val storesApi: StoresApi
) : APIService {

    override suspend fun fetchStores(): List<Store> {
        val response = storesApi.getAllStores()
        return if (response.isSuccessful) {
            response.body()?.map { storeEntity ->
                Store(
                    id = storeEntity.id ?: 0,
                    name = storeEntity.name ?: "",
                    printerUri = storeEntity.printerUri
                )
            } ?: emptyList()
        } else {
            throw Exception("Failed to fetch stores: ${response.code()}")
        }
    }

    override suspend fun createSale(
        storeId: Int,
        staffBarcode: String,
        deposit: Int,
        itemIds: String
    ): Sale {
        val itemBarcodes = itemIds.split(",")
        val request = CreateSaleRequest(
            storeId = storeId,
            staffBarcode = staffBarcode,
            deposit = deposit,
            items = itemBarcodes.map { barcode ->
                CreateSaleRequestItemsInner(barcode = barcode, quantity = 1)
            }
        )
        val response = salesApi.createSale(request)
        return if (response.isSuccessful) {
            val saleResponse = response.body()!!
            Sale(
                id = saleResponse.saleId ?: 0,
                barcode = saleResponse.saleId?.toString() ?: "",
                createdAt = java.util.Date().toString(),
                points = 0,
                price = saleResponse.totalAmount ?: 0,
                items = itemIds,
                storeId = storeId,
                staffId = 0
            )
        } else {
            throw Exception("Failed to create sale: ${response.code()}")
        }
    }

    override suspend fun getItem(itemBarcode: String): Item {
        val response = itemsApi.getItemByBarcode(itemBarcode)
        return if (response.isSuccessful) {
            val itemResponse = response.body()!!
            Item(
                id = itemResponse.id,
                barcode = itemResponse.barcode,
                name = itemResponse.name,
                price = itemResponse.price,
                storeId = 0,
                genreId = 0
            )
        } else {
            throw Exception("Failed to get item: ${response.code()}")
        }
    }

    override suspend fun getStaff(staffBarcode: String): Staff {
        val response = staffApi.getStaffByBarcode(staffBarcode)
        return if (response.isSuccessful) {
            val staffResponse = response.body()!!
            Staff(
                barcode = staffResponse.barcode ?: staffBarcode,
                name = staffResponse.name ?: ""
            )
        } else {
            throw Exception("Failed to get staff: ${response.code()}")
        }
    }
}
