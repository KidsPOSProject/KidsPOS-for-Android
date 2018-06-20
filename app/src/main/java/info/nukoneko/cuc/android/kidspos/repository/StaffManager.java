package info.nukoneko.cuc.android.kidspos.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import info.nukoneko.cuc.android.kidspos.api.APIAdapter;
import info.nukoneko.cuc.android.kidspos.entity.Staff;
import io.reactivex.Observable;

public final class StaffManager extends BaseManager {
    public StaffManager(@NonNull Context context, @NonNull APIAdapter apiAdapter) {
        super(context, apiAdapter);
    }

    public Observable<Staff> getStaff(String barcode) {
        return getAPIService().getStaff(barcode);
    }
}
