package info.nukoneko.cuc.android.kidspos;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import info.nukoneko.cuc.android.kidspos.api.APIService;
import info.nukoneko.cuc.android.kidspos.util.manager.ApiManager;
import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.event.StaffUpdateEvent;
import info.nukoneko.cuc.android.kidspos.event.StoreUpdateEvent;
import info.nukoneko.cuc.android.kidspos.util.MiscUtil;
import info.nukoneko.cuc.android.kidspos.util.manager.SettingsManager;
import info.nukoneko.cuc.android.kidspos.util.manager.StoreManager;

public class KidsPOSApplication extends Application {

    // こちらを有効にすると普通の動作ができなくなります
    private final static boolean isTestMode = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private StoreManager mStoreManager = null;
    private SettingsManager mSettingsManager = null;
    private ApiManager mApiManager = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.addLogAdapter(new AndroidLogAdapter());

        mStoreManager = new StoreManager(this, new StoreManager.Listener() {
            @Override
            public void onUpdateStaff(@NonNull Staff staff) {
                postEvent(new StaffUpdateEvent(staff));
            }

            @Override
            public void onUpdateStore(@NonNull Store store) {
                postEvent(new StoreUpdateEvent(store));
            }
        });
        mApiManager = new ApiManager(this);
        mSettingsManager = new SettingsManager(this, new SettingsManager.Listener() {
            @Override
            public void updateIpPort() {
                mApiManager.updateApiService();
            }
        }) {{
            if (TextUtils.isEmpty(getServerIP()) || !MiscUtil.isIpAddressValid(getServerIP())) {
                backToDefaultIpSetting();
            }
            if (TextUtils.isEmpty(getServerPort()) || !MiscUtil.isPortValid(getServerPort())) {
                backToDefaultPortSetting();
            }
        }};
    }

    @NonNull
    public static KidsPOSApplication get(@NonNull Context context){
        return (KidsPOSApplication) context.getApplicationContext();
    }

    @Nullable
    public Store getCurrentStore() {
        return mStoreManager.getCurrentStore();
    }

    @Nullable
    public Staff getCurrentStaff() {
        return mStoreManager.getCurrentStaff();
    }

    public void updateCurrentStore(Store store) {
        mStoreManager.setCurrentStore(store);
    }

    public void updateCurrentStaff(Staff staff) {
        mStoreManager.setCurrentStaff(staff);
    }

    public boolean isPracticeModeEnabled() {
        return mSettingsManager.isPracticeModeEnabled();
    }

    public boolean isTestModeEnabled() {
        return isTestMode;
    }

    public String getServerIpPortText() {
        return String.format(Locale.getDefault(), "%s:%s", getServerIp(), getServerPort());
    }

    public String getServerIp() {
        return mSettingsManager.getServerIP();
    }

    public String getServerPort() {
        return mSettingsManager.getServerPort();
    }

    public APIService getApiService() {
        return mApiManager.getApiService();
    }

    public void postEvent(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            EventBus.getDefault().post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(event);
                }
            });
        }
    }
}
