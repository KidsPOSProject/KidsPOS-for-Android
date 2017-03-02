package info.nukoneko.cuc.android.kidspos;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import info.nukoneko.cuc.android.kidspos.util.manager.KPSettingsManager;
import info.nukoneko.cuc.android.kidspos.util.manager.KPStoreManager;
import info.nukoneko.kidspos4j.KidsPos4jConfig;
import info.nukoneko.kidspos4j.model.ModelStaff;
import info.nukoneko.kidspos4j.model.ModelStore;

public class KPApplicationController extends Application {

    private KPStoreManager storeManager = null;
    private KPSettingsManager settingsManager = null;

    @Override
    public void onCreate() {
        super.onCreate();

        storeManager = new KPStoreManager(this) {{
            final ModelStore store = new ModelStore();
            store.setId(1);
            store.setName("おみせ");
            setCurrentStore(store);
        }};
        settingsManager = new KPSettingsManager(this) {{
            if (!TextUtils.isEmpty(getServerIP())) setServerIp("192.168.0.220:9500");
        }};

        KidsPos4jConfig.setDebug(settingsManager.isDebugModeEnabled());
        KidsPos4jConfig.setDefaultUrl(false, settingsManager.getServerIP());
    }

    @NonNull
    public static KPApplicationController get(@NonNull Context context){
        return (KPApplicationController) context.getApplicationContext();
    }

    @NonNull
    public ModelStore getCurrentStore() {
        return storeManager.getCurrentStore();
    }

    @Nullable
    public ModelStaff getCurrentStaff() {
        return storeManager.getCurrentStaff();
    }

    public boolean isPracticeModeEnabled() {
        return settingsManager.isPracticeModeEnabled();
    }

    public boolean isDebugModeEnabled() {
        return settingsManager.isDebugModeEnabled();
    }
}
