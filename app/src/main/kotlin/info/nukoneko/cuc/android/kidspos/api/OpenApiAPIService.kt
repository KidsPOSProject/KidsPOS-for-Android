package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.api.generated.ItemsApi
import info.nukoneko.cuc.android.kidspos.api.generated.SalesApi
import info.nukoneko.cuc.android.kidspos.api.generated.StatusApi
import info.nukoneko.cuc.android.kidspos.api.generated.StoresApi
import info.nukoneko.cuc.android.kidspos.api.generated.model.CreateSaleRequest
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.ServerStatus
import info.nukoneko.cuc.android.kidspos.entity.Store

class OpenApiAPIService(
    private val itemsApi: ItemsApi,
    private val salesApi: SalesApi,
    private val statusApi: StatusApi,
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
        deposit: Int,
        itemIds: String
    ): Sale {
        val request = CreateSaleRequest(
            storeId = storeId,
            itemIds = itemIds,
            deposit = deposit
        )
        val response = salesApi.createSale(request)
        return if (response.isSuccessful) {
            val saleResponse = response.body()!!
            Sale(
                id = saleResponse.id ?: 0,
                barcode = saleResponse.id?.toString() ?: "",
                createdAt = java.util.Date().toString(),
                points = 0,
                price = saleResponse.amount ?: 0,
                items = itemIds,
                storeId = saleResponse.storeId ?: storeId,
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

    override suspend fun getServerStatus(): ServerStatus {
        val response = statusApi.getServerStatus()
        return if (response.isSuccessful) {
            val statusResponse = response.body()!!
            ServerStatus(
                status = statusResponse.status,
                version = statusResponse.version,
                apiVersion = statusResponse.apiVersion
            )
        } else {
            throw Exception("Failed to get server status: ${response.code()}")
        }
    }
}
