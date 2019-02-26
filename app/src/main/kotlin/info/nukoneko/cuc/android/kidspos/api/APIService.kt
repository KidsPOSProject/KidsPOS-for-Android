package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface APIService {

    @GET("store/list")
    fun fetchStores(): Deferred<List<Store>>

    @FormUrlEncoded
    @POST("sale/create")
    fun createSale(
            @Field("storeId") storeId: Int,
            @Field("staffBarcode") staffBarcode: String,
            @Field("deposit") deposit: Int,
            @Field("itemIds") itemIds: String): Deferred<Sale>

    @GET("item/{barcode}")
    fun getItem(@Path("barcode") itemBarcode: String): Deferred<Item>

    @GET("staff/{barcode}")
    fun getStaff(@Path("barcode") staffBarcode: String): Deferred<Staff>
}