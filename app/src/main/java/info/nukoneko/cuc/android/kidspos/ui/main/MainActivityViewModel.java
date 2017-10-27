package info.nukoneko.cuc.android.kidspos.ui.main;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;

public final class MainActivityViewModel extends BaseObservable {
    private ObservableInt mSumPrice = new ObservableInt(0);
    private ObservableField<Store> mCurrentStore = new ObservableField<>();
    private ObservableField<Staff> mCurrentStaff = new ObservableField<>();

    public int getSumPrice() {
        return mSumPrice.get();
    }

    void setSumPrice(int sumPrice) {
        mSumPrice.set(sumPrice);
        notifyChange();
    }

    public Store getCurrentStore() {
        return mCurrentStore.get();
    }

    void setCurrentStore(Store currentStore) {
        mCurrentStore.set(currentStore);
        notifyChange();
    }

    public Staff getCurrentStaff() {
        return mCurrentStaff.get();
    }

    void setCurrentStaff(Staff currentStaff) {
        mCurrentStaff.set(currentStaff);
        notifyChange();
    }

    public interface Listener {
        void onClickClear(View view);

        void onClickAccount(View view);
    }
}
