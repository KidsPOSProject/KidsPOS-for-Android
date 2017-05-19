package info.nukoneko.cuc.android.kidspos;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;

import info.nukoneko.cuc.android.kidspos.util.MiscUtil;
import info.nukoneko.cuc.android.kidspos.util.manager.SettingsManager;
import info.nukoneko.cuc.android.kidspos.util.manager.StoreManager;
import info.nukoneko.cuc.android.kidspos.util.rx.RxWrap;
import info.nukoneko.cuc.kidspos4j.KidsPos4jConfig;
import info.nukoneko.cuc.kidspos4j.model.ModelStaff;
import info.nukoneko.cuc.kidspos4j.model.ModelStore;
import rx.Observable;

public class KidPOSApplication extends Application {

    // こちらを有効にすると普通の動作ができなくなります
    private final static boolean isTestMode = false;

    private StoreManager mStoreManager = null;
    private SettingsManager mSettingsManager = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mStoreManager = new StoreManager(this);
        mSettingsManager = new SettingsManager(this) {{
            if (TextUtils.isEmpty(getServerIP()) || !MiscUtil.isIpAddressValid(getServerIP())) {
                backToDefaultIpSetting();
            }
            if (TextUtils.isEmpty(getServerPort()) || !MiscUtil.isPortValid(getServerPort())) {
                backToDefaultPortSetting();
            }
        }};

        KidsPos4jConfig.setDebug(true);
        KidsPos4jConfig.setDefaultUrl(false, getServerIpPortText());
    }

    @NonNull
    public static KidPOSApplication get(@NonNull Context context){
        return (KidPOSApplication) context.getApplicationContext();
    }

    @Nullable
    public ModelStore getCurrentStore() {
        return mStoreManager.getCurrentStore();
    }

    @Nullable
    public ModelStaff getCurrentStaff() {
        return mStoreManager.getCurrentStaff();
    }

    public void updateCurrentStore(ModelStore store) {
        mStoreManager.setCurrentStore(store);
    }

    public void updateCurrentStaff(ModelStaff staff) {
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
