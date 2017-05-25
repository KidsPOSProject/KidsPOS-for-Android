package info.nukoneko.cuc.android.kidspos.api;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.entity.Item;
import info.nukoneko.cuc.android.kidspos.entity.Sale;
import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @FormUrlEncoded
    @POST("sale/create")
    Call<Sale> createSale(
            @Field("received") int receiveMoney,
            @Field("points") int saleItemCount,
            @Field("price") int sumPrice,
            @Field("items") String saleItemsList,
            @Field("storeId") int storeId,
            @Field("staffBarcode") String staffCode);

    @GET("item/{barcode}")
    Call<Item> readItem(@Path("barcode") String itemBarcode);

    @GET("staff")
    Call<Staff> getStaff(@Query("barcode") String staffBarcode);

    @GET("store/list")
    Call<List<Store>> getStoreList();
}