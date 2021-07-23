package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.domain.entity.Sale
import info.nukoneko.cuc.android.kidspos.domain.entity.Store
import retrofit2.http.*

interface APIService {

    @GET("store/list")
    suspend fun fetchStores(): List<Store>

    @FormUrlEncoded
    @POST("sale/create")
    suspend fun createSale(
        @Field("storeId") storeId: Int,
        @Field("staffBarcode") staffBarcode: String,
        @Field("deposit") deposit: Int,
        @Field("itemIds") itemIds: String
    ): Sale

    @GET("item/{barcode}")
    suspend fun getItem(@Path("barcode") itemBarcode: String): info.nukoneko.cuc.android.kidspos.domain.entity.Item

    @GET("staff/{barcode}")
    suspend fun getStaff(@Path("barcode") staffBarcode: String): info.nukoneko.cuc.android.kidspos.domain.entity.Staff

    @GET("setting/status")
    suspend fun getStatus()
}
