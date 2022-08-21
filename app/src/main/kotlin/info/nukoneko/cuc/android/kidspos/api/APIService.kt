package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import retrofit2.http.*

interface APIService {

    @GET("store/list")
    suspend fun fetchStores(): Store

    @FormUrlEncoded
    @POST("sale/create")
    suspend fun createSale(
        @Field("storeId") storeId: Int,
        @Field("staffBarcode") staffBarcode: String,
        @Field("deposit") deposit: Int,
        @Field("itemIds") itemIds: String
    ): Sale

    @GET("item/{barcode}")
    suspend fun getItem(@Path("barcode") itemBarcode: String): Item

    @GET("staff/{barcode}")
    suspend fun getStaff(@Path("barcode") staffBarcode: String): Staff

    @GET("setting/status")
    suspend fun getStatus(): Any
}
