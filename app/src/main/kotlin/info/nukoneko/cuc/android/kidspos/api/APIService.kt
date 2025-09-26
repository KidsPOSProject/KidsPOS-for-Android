package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.api.generated.*
import info.nukoneko.cuc.android.kidspos.api.generated.model.*
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import retrofit2.Response

/**
 * OpenAPI Generator で生成されたクライアントを使用するAPIService実装
 */
open class APIService(
    private val itemsApi: ItemsApi,
    private val salesApi: SalesApi,
    private val staffApi: StaffApi,
    private val storesApi: StoresApi,
    private val settingsApi: SettingsApi
) {

    open suspend fun fetchStores(): List<Store> {
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

    open suspend fun createSale(
        storeId: Int,
        staffBarcode: String,
        deposit: Int,
        itemIds: String
    ): Sale {
        // itemIdsをカンマ区切りからリストに変換
        // 注意: 新しいAPIではitemIdではなくバーコードを使用
        val itemBarcodes = itemIds.split(",")

        val request = CreateSaleRequest(
            storeId = storeId,
            staffBarcode = staffBarcode,
            deposit = deposit,
            items = itemBarcodes.map { barcode ->
                CreateSaleRequestItemsInner(
                    barcode = barcode,
                    quantity = 1 // デフォルトで1個とする
                )
            }
        )

        val response = salesApi.createSale(request)
        return if (response.isSuccessful) {
            val saleResponse = response.body()!!
            Sale(
                id = saleResponse.saleId ?: 0,
                barcode = saleResponse.saleId?.toString() ?: "", // バーコードはIDから生成
                createdAt = java.util.Date().toString(), // 現在時刻を設定
                points = 0, // ポイントは新APIにない
                price = saleResponse.totalAmount ?: 0,
                items = itemIds, // 元のitemIdsをそのまま使用
                storeId = storeId,
                staffId = 0 // スタッフIDは取得できないため仮値
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
                storeId = 0, // 新APIではstoreIdが返ってこない
                genreId = 0  // 新APIではgenreIdが返ってこない
            )
        } else {
            throw Exception("Failed to get item: ${response.code()}")
        }
    }

    open suspend fun getStaff(staffBarcode: String): Staff {
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

    open suspend fun getStatus(): Any {
        val response = settingsApi.getStatus()
        return if (response.isSuccessful) {
            response.body() ?: mapOf<String, Any>()
        } else {
            throw Exception("Failed to get status: ${response.code()}")
        }
    }
}