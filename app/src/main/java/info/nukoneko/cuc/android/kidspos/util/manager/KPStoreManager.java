package info.nukoneko.cuc.android.kidspos.util.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import info.nukoneko.kidspos4j.model.ModelStaff;
import info.nukoneko.kidspos4j.model.ModelStore;

public class KPStoreManager {
    private ModelStore mCurrentStore = null;
    private ModelStaff mCurrentStaff = null;
    private Context mContext;

    public KPStoreManager(final Context context) {
        this.mContext = context;
    }

    @NonNull
    public ModelStore getCurrentStore() {
        return mCurrentStore;
    }

    @Nullable
    public ModelStaff getCurrentStaff() {
        return mCurrentStaff;
    }

    public void setCurrentStaff(ModelStaff currentStaff) {
        this.mCurrentStaff = currentStaff;
    }

    public void setCurrentStore(ModelStore currentStore) {
        this.mCurrentStore = currentStore;
    }
}
