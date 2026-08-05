package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.api.generated.*
import info.nukoneko.cuc.android.kidspos.api.generated.model.*
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store

/**
 * OpenAPI Generator で生成されたクライアントを使用するAPIService実装
 */
open class APIService(
    private val itemsApi: ItemsApi,
    private val salesApi: SalesApi,
    private val storesApi: StoresApi,
    private val settingsApi: SettingsApi,
    private val statusApi: StatusApi
) {
    companion object {
        const val SUPPORTED_API_VERSION = 1
    }

    open suspend fun fetchStores(): List<Store> {
        val response = storesApi.getAllStores()
        return if (response.isSuccessful) {
            response.body()?.map { storeEntity ->
                Store(
                    id = storeEntity.id ?: 0,
                    name = storeEntity.name,
                    printerUri = storeEntity.printerUri
                )
            } ?: emptyList()
        } else {
            throw Exception("Failed to fetch stores: ${response.code()}")
        }
    }

    open suspend fun createSale(
        storeId: Int,
        staffBarcode: String,
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

    open suspend fun getItem(itemBarcode: String): Item {
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

    open suspend fun getStaff(staffBarcode: String): Staff =
        throw UnsupportedOperationException("Staff API is not supported by the server")

    open suspend fun getStatus(): StatusResponse {
        val response = statusApi.getServerStatus()
        return if (response.isSuccessful) {
            response.body()!!
        } else {
            throw Exception("Failed to get status: ${response.code()}")
        }
    }
}
