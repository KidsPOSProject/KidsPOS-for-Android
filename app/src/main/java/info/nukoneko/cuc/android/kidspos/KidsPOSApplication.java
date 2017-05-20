package info.nukoneko.cuc.android.kidspos;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;

import info.nukoneko.cuc.android.kidspos.api.APIService;
import info.nukoneko.cuc.android.kidspos.api.ApiManager;
import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.util.MiscUtil;
import info.nukoneko.cuc.android.kidspos.util.manager.SettingsManager;
import info.nukoneko.cuc.android.kidspos.util.manager.StoreManager;
import info.nukoneko.cuc.android.kidspos.util.rx.RxWrap;
import rx.Observable;

public class KidsPOSApplication extends Application {

    // こちらを有効にすると普通の動作ができなくなります
    private final static boolean isTestMode = false;

    private StoreManager mStoreManager = null;
    private SettingsManager mSettingsManager = null;
    private ApiManager mApiManager = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mStoreManager = new StoreManager(this);
        mApiManager = new ApiManager(this);
        mSettingsManager = new SettingsManager(this, () -> mApiManager.updateApiService()) {{
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
        return String.format(Locale.getDefault(), "%s:%s",
                mSettingsManager.getServerIP(), mSettingsManager.getServerPort());
    }

    public void sendErrorReport(@NonNull String errorMessage) {

    }

    public APIService getApiService() {
        return mApiManager.getApiService();
    }

    public Observable<Boolean> checkServerReachable() {
        return RxWrap.create(Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                final Socket sock = new Socket();
                sock.connect(new InetSocketAddress(mSettingsManager.getServerIP(), Integer.parseInt(mSettingsManager.getServerPort())), 2000);
                sock.close();
                subscriber.onNext(true);
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onNext(false);
            }
            subscriber.onCompleted();
        }));
    }
}
