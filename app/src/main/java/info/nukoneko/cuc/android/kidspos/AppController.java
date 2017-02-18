package info.nukoneko.cuc.android.kidspos;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import info.nukoneko.cuc.android.kidspos.util.SQLiteAdapter;
import info.nukoneko.kidspos4j.KidsPos4jConfig;
import info.nukoneko.kidspos4j.util.config.SQLiteSetting;

public class AppController extends Application {

    private StoreManager storeManager = null;

    @Override
    public void onCreate() {
        super.onCreate();

        KidsPos4jConfig.setDebug(true);

        SQLiteSetting.setSqlProvider(new SQLiteAdapter());

        KidsPos4jConfig.setDefaultUrl(false, "192.168.0.220:9500");

        storeManager = new StoreManager(this);
    }

    @NonNull
    public static AppController get(@NonNull Context context){
        return (AppController) context.getApplicationContext();
    }

    @NonNull
    public StoreManager getStoreManager() {
        if (storeManager == null) {
            throw new NullPointerException();
        }
        return storeManager;
    }
}
