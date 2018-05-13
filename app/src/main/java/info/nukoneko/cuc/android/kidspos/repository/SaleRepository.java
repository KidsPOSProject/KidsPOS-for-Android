package info.nukoneko.cuc.android.kidspos.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import info.nukoneko.cuc.android.kidspos.api.APIAdapter;
import info.nukoneko.cuc.android.kidspos.entity.Sale;
import io.reactivex.Observable;

public final class SaleRepository extends Repository {

    public SaleRepository(@NonNull Context context, @NonNull APIAdapter apiAdapter) {
        super(context, apiAdapter);
    }

    public Observable<Sale> createSale(int receiveMoney, int saleItemCount, int sumPrice, String saleItemsList, int storeId, String staffCode) {
        return getAPIService().createSale(receiveMoney, saleItemCount, sumPrice, saleItemsList, storeId, staffCode);
    }
}
