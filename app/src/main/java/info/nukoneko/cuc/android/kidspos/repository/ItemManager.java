package info.nukoneko.cuc.android.kidspos.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import info.nukoneko.cuc.android.kidspos.api.APIAdapter;
import info.nukoneko.cuc.android.kidspos.entity.Item;
import io.reactivex.Observable;

public final class ItemManager extends BaseManager {
    public ItemManager(@NonNull Context context, @NonNull APIAdapter apiAdapter) {
        super(context, apiAdapter);
    }

    public Observable<Item> getItem(String barcode) {
        return getAPIService().getItem(barcode);
    }
}
