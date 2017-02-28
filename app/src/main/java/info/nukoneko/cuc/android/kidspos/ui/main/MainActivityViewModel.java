package info.nukoneko.cuc.android.kidspos.ui.main;


import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import info.nukoneko.kidspos4j.model.ModelStaff;
import info.nukoneko.kidspos4j.model.ModelStore;

public final class MainActivityViewModel {
    public interface Listener {
        void onClickClear();
        void onClickAccount();
    }

    private ObservableInt mSumPrice = new ObservableInt(0);
    private ObservableField<ModelStore> mCurrentStore = new ObservableField<>();
    private ObservableField<ModelStaff> mCurrentStaff = new ObservableField<>();

    public int getSumPrice() {
        return mSumPrice.get();
    }

    public ModelStore getCurrentStore() {
        return mCurrentStore.get();
    }

    public ModelStaff getCurrentStaff() {
        return mCurrentStaff.get();
    }

    public void setSumPrice(int sumPrice) {
        this.mSumPrice.set(sumPrice);
    }

    public void initSumPrice() {
        this.mSumPrice.set(0);
    }

    public void addSubPrice(int price) {
        this.mSumPrice.set(mSumPrice.get() + price);
    }

    public void setmCurrentStore(ModelStore currentStore) {
        this.mCurrentStore.set(currentStore);
    }

    public void setCurrentStaff(ModelStaff currentStaff) {
        this.mCurrentStaff.set(currentStaff);
    }
}
