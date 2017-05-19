package info.nukoneko.cuc.android.kidspos.util.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventUpdateStaff;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventUpdateStore;
import info.nukoneko.cuc.kidspos4j.model.ModelStaff;
import info.nukoneko.cuc.kidspos4j.model.ModelStore;

@SuppressWarnings({"WeakerAccess", "unused"})
public class StoreManager {
    private static final String KEY_PREFERENCE_STORE_MANAGER = "preference_store_manager";
    private static final String KEY_LATEST_STORE = "LATEST_STORE";
    private static final String KEY_LATEST_STAFF = "LATEST_STAFF";
    private static final String KEY_SET_ID = "set_id";
    private static final String KEY_SET_NAME = "set_name";
    private static final String KEY_SET_BARCODE = "set_barcode";

    @Nullable private ModelStore mCurrentStore = null;
    @Nullable private ModelStaff mCurrentStaff = null;
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
    public ModelStore getCurrentStore() {
        return mCurrentStore;
    }

    @Nullable
    public ModelStaff getCurrentStaff() {
        return mCurrentStaff;
    }

    public void setCurrentStaff(ModelStaff currentStaff) {
        this.mCurrentStaff = currentStaff;
        saveLatestStaff(currentStaff);
        KPEventBusProvider.getInstance().send(new KPEventUpdateStaff(currentStaff));
    }

    public void setCurrentStore(ModelStore currentStore) {
        this.mCurrentStore = currentStore;
        saveLatestStore(currentStore);
        KPEventBusProvider.getInstance().send(new KPEventUpdateStore(currentStore));
    }

    private void saveLatestStaff(@Nullable ModelStaff modelStaff) {
        final SharedPreferences.Editor editor = getPreference().edit().remove(KEY_LATEST_STAFF);
        if (modelStaff != null) {
            editor.putStringSet(KEY_LATEST_STAFF, new HashSet<String>() {{
                add(String.format("%s,%s", KEY_SET_BARCODE, modelStaff.getBarcode()));
                add(String.format("%s,%s", KEY_SET_NAME, modelStaff.getName()));
            }});
        }
        editor.apply();
    }

    @Nullable
    private ModelStaff getLatestStaff() {
        final String barcode = getValueFromKeySet(KEY_LATEST_STAFF, KEY_SET_ID);
        final String name = getValueFromKeySet(KEY_LATEST_STAFF, KEY_SET_NAME);
        if (TextUtils.isEmpty(barcode) || TextUtils.isEmpty(name)) return null;
        final ModelStaff staff = new ModelStaff();
        staff.setBarcode(barcode);
        staff.setName(name);
        return staff;
    }

    private void saveLatestStore(@Nullable ModelStore modelStore) {
        final SharedPreferences.Editor editor = getPreference().edit().remove(KEY_LATEST_STORE);
        if (modelStore != null) {
            editor.putStringSet(KEY_LATEST_STORE, new HashSet<String>() {{
                add(String.format("%s,%s", KEY_SET_ID, modelStore.getId()));
                add(String.format("%s,%s", KEY_SET_NAME, modelStore.getName()));
            }});
        }
        editor.apply();
    }

    @Nullable
    private ModelStore getLatestStore() {
        final String id = getValueFromKeySet(KEY_LATEST_STORE, KEY_SET_ID);
        final String name = getValueFromKeySet(KEY_LATEST_STORE, KEY_SET_NAME);
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name)) return null;
        final ModelStore store = new ModelStore();
        store.setId(Integer.parseInt(id));
        store.setName(name);
        return store;
    }

    private String getValueFromKeySet(@NonNull String key, @NonNull String hashText) {
        String ret = "";
        if (TextUtils.isEmpty(key)) return ret;
        final Set<String> values = getPreference().getStringSet(key, new HashSet<>());
        if (values.size() == 0) return ret;
        for (String value : values) {
            final String[] rows = value.split(",");
            if (rows.length == 2 && rows[0].equals(hashText)) return rows[1];
        }
        return ret;
    }
}
