package info.nukoneko.cuc.android.kidspos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import info.nukoneko.kidspos4j.model.ModelStaff;
import info.nukoneko.kidspos4j.model.ModelStore;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class StoreManager {
    private ModelStore currentStore = null;
    private ModelStaff currentStaff = null;
    private Context context;

    public StoreManager(final Context context) {
        this.context = context;
        this.currentStore = new ModelStore();
        this.currentStore.setId(1);
        this.currentStore.setName("おみせ");
    }

    @NonNull
    public ModelStore getCurrentStore() {
        return currentStore;
    }

    @Nullable
    public ModelStaff getCurrentStaff() {
        return currentStaff;
    }

    public void setCurrentStaff(ModelStaff currentStaff) {
        this.currentStaff = currentStaff;
    }
}
