package info.nukoneko.cuc.android.kidspos;

import android.app.Application;
import android.content.Context;

import info.nukoneko.cuc.android.kidspos.util.SQLiteAdapter;
import info.nukoneko.kidspos4j.KidsPos4jConfig;
import info.nukoneko.kidspos4j.util.config.SQLiteSetting;

/**
 * created at 2015/06/13.
 */
public class AppController extends Application {
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        StoreManager.Initialize(this);
        KidsPos4jConfig.setDebug(true);

        SQLiteSetting.setSqlProvider(new SQLiteAdapter());

//        KidsPos4jConfig.setBaseUrl("localhost:8080");
        KidsPos4jConfig.setDefaultUrl(false, "10.99.8.86:8080");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized AppController get(){
        return mInstance;
    }

}
