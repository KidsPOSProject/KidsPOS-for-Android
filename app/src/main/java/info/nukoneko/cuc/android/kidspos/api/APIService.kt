package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    @GET("store/list")
    fun storeList(): Observable<List<Store>>

    @FormUrlEncoded
    @POST("sale/create")
    fun createSale(
            @Field("received") receiveMoney: Int,
            @Field("points") saleItemCount: Int,
            @Field("price") sumPrice: Int,
            @Field("items") saleItemsList: String,
            @Field("storeId") storeId: Int,
            @Field("staffBarcode") staffCode: String): Single<Sale>

    @GET("item/{barcode}")
    fun getItem(@Path("barcode") itemBarcode: String): Single<Item>

    @GET("staff")
    fun getStaff(@Query("barcode") staffBarcode: String): Single<Staff>
}