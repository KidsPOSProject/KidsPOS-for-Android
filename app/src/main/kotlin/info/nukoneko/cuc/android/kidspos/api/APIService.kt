package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface APIService {

    @GET("store/list")
    fun fetchStores(): Deferred<List<Store>>

    @FormUrlEncoded
    @POST("sale/create")
    fun createSale(
            @Field("received") receiveMoney: Int,
            @Field("points") saleItemCount: Int,
            @Field("price") sumPrice: Int,
            @Field("items") saleItemsList: String,
            @Field("storeId") storeId: Int,
            @Field("staffBarcode") staffCode: String): Deferred<Sale>

    @GET("item/{barcode}")
    fun getItem(@Path("barcode") itemBarcode: String): Single<Item>

    @GET("staff")
    fun getStaff(@Query("barcode") staffBarcode: String): Single<Staff>
}