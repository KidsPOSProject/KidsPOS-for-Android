package info.nukoneko.cuc.android.kidspos.util.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.StaffUpdateEvent;
import info.nukoneko.cuc.android.kidspos.event.obj.StoreUpdateEvent;

public final class StoreManager {
    private static final String KEY_PREFERENCE_STORE_MANAGER = "preference_store_manager";
    private static final String KEY_LATEST_STORE = "LATEST_STORE";
    private static final String KEY_LATEST_STAFF = "LATEST_STAFF";

    @Nullable private Store mCurrentStore = null;
    @Nullable private Staff mCurrentStaff = null;
    private final Context mContext;

    public StoreManager(final Context context) {
        this.mContext = context;
        this.mCurrentStaff = getLatestStaff();
        this.mCurrentStore = getLatestStore();
    }

    @NonNull
    private SharedPreferences getPreference() {
        return mContext.getSharedPreferences(KEY_PREFERENCE_STORE_MANAGER, Context.MODE_PRIVATE);
    }

    @Nullable
    public Store getCurrentStore() {
        return mCurrentStore;
    }

    @Nullable
    public Staff getCurrentStaff() {
        return mCurrentStaff;
    }

    public void setCurrentStaff(Staff staff) {
        mCurrentStaff = staff;
        saveLatestStaff(staff);
        KPEventBusProvider.getInstance().send(new StaffUpdateEvent(staff));
    }

    public void setCurrentStore(Store store) {
        mCurrentStore = store;
        saveLatestStore(store);
        KPEventBusProvider.getInstance().send(new StoreUpdateEvent(store));
    }

    private void saveLatestStaff(@Nullable Staff staff) {
        final SharedPreferences.Editor editor = getPreference().edit().remove(KEY_LATEST_STAFF);
        if (staff != null) {
            editor.putString(KEY_LATEST_STAFF, new Gson().toJson(staff));
        }
        editor.apply();
    }

    @Nullable
    private Staff getLatestStaff() {
        final String staff = getPreference().getString(KEY_LATEST_STAFF, "");
        if (TextUtils.isEmpty(staff)) return null;
        return new Gson().fromJson(staff, Staff.class);
    }

    private void saveLatestStore(@Nullable Store store) {
        final SharedPreferences.Editor editor = getPreference().edit().remove(KEY_LATEST_STORE);
        if (store != null) {
            editor.putString(KEY_LATEST_STORE, new Gson().toJson(store));
        }
        editor.apply();
    }

    @Nullable
    private Store getLatestStore() {
        final String store = getPreference().getString(KEY_LATEST_STORE, "");
        if (TextUtils.isEmpty(store)) return null;
        return new Gson().fromJson(store, Store.class);
    }
}
