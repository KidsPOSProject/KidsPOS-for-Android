package info.nukoneko.cuc.android.kidspos.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.api.APIAdapter;
import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import io.reactivex.Observable;

public final class StoreManager extends BaseManager {
    private static final String KEY_PREFERENCE_STORE_MANAGER = "preference_store_manager";
    private static final String KEY_LATEST_STORE = "LATEST_STORE";
    private static final String KEY_LATEST_STAFF = "LATEST_STAFF";
    @NonNull
    private final Listener mListener;
    @Nullable
    private Staff mCurrentStaff = null;
    @Nullable
    private Store mCurrentStore = null;

    public StoreManager(@NonNull final Context context, @NonNull APIAdapter apiAdapter, @NonNull final Listener listener) {
        super(context, apiAdapter);
        this.mListener = listener;
        this.mCurrentStaff = getLatestStaff();
        this.mCurrentStore = getLatestStore();
    }

    @NonNull
    private SharedPreferences getPreference() {
        return getSharedPreference(KEY_PREFERENCE_STORE_MANAGER);
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
        try {
            final String staff = getPreference().getString(KEY_LATEST_STAFF, "");
            if (TextUtils.isEmpty(staff)) return null;
            return new Gson().fromJson(staff, Staff.class);
        } catch (ClassCastException e) {
            getPreference().edit().putString(KEY_LATEST_STAFF, "").apply();
            return null;
        }
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
        try {
            final String store = getPreference().getString(KEY_LATEST_STORE, "");
            if (TextUtils.isEmpty(store)) return null;
            return new Gson().fromJson(store, Store.class);
        } catch (ClassCastException e) {
            getPreference().edit().putString(KEY_LATEST_STORE, "").apply();
            return null;
        }
    }

    public Observable<List<Store>> fetchStores() {
        return getAPIService().getStoreList();
    }

    public interface Listener {
        void onUpdateStaff(@NonNull final Staff staff);

        void onUpdateStore(@NonNull final Store store);
    }
}
