package info.nukoneko.cuc.android.kidspos.api.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;

public final class StoreManager {
    private static final String KEY_PREFERENCE_STORE_MANAGER = "preference_store_manager";
    private static final String KEY_LATEST_STORE = "LATEST_STORE";
    private static final String KEY_LATEST_STAFF = "LATEST_STAFF";
    @NonNull
    private final Context mContext;
    @NonNull
    private final Listener mListener;
    @Nullable
    private Staff mCurrentStaff = null;
    @Nullable
    private Store mCurrentStore = null;

    public StoreManager(@NonNull final Context context, @NonNull final Listener listener) {
        this.mContext = context;
        this.mListener = listener;
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

    public void setCurrentStore(Store store) {
        mCurrentStore = store;
        saveLatestStore(store);
        mListener.onUpdateStore(store);
    }

    @Nullable
    public Staff getCurrentStaff() {
        return mCurrentStaff;
    }

    public void setCurrentStaff(Staff staff) {
        mCurrentStaff = staff;
        saveLatestStaff(staff);
        mListener.onUpdateStaff(staff);
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

    public interface Listener {
        void onUpdateStaff(@NonNull final Staff staff);

        void onUpdateStore(@NonNull final Store store);
    }
}
