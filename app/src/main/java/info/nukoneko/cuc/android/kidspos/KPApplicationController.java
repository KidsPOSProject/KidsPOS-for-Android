package info.nukoneko.cuc.android.kidspos;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;

import info.nukoneko.cuc.android.kidspos.util.MiscUtil;
import info.nukoneko.cuc.android.kidspos.util.manager.KPSettingsManager;
import info.nukoneko.cuc.android.kidspos.util.manager.KPStoreManager;
import info.nukoneko.cuc.android.kidspos.util.rx.RxWrap;
import info.nukoneko.cuc.kidspos4j.KidsPos4jConfig;
import info.nukoneko.cuc.kidspos4j.model.ModelStaff;
import info.nukoneko.cuc.kidspos4j.model.ModelStore;
import rx.Observable;

public class KPApplicationController extends Application {

    // こちらを有効にすると普通の動作ができなくなります
    private final static boolean isTestMode = false;

    private KPStoreManager storeManager = null;
    private KPSettingsManager settingsManager = null;

    @Override
    public void onCreate() {
        super.onCreate();

        storeManager = new KPStoreManager(this);
        settingsManager = new KPSettingsManager(this) {{
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
    public static KPApplicationController get(@NonNull Context context){
        return (KPApplicationController) context.getApplicationContext();
    }

    @Nullable
    public ModelStore getCurrentStore() {
        return storeManager.getCurrentStore();
    }

    @Nullable
    public ModelStaff getCurrentStaff() {
        return storeManager.getCurrentStaff();
    }

    public void updateCurrentStore(ModelStore store) {
        storeManager.setCurrentStore(store);
    }

    public void updateCurrentStaff(ModelStaff staff) {
        storeManager.setCurrentStaff(staff);
    }

    public boolean isPracticeModeEnabled() {
        return settingsManager.isPracticeModeEnabled();
    }

    public boolean isTestModeEnabled() {
        return isTestMode;
    }

    public String getServerIpPortText() {
        return String.format(Locale.getDefault(), "%s:%s",
                settingsManager.getServerIP(), settingsManager.getServerPort());
    }

    public void sendErrorReport(@NonNull String errorMessage) {
//        RxWrap.create(APIManager.System().errorReport(errorMessage));
        Log.d("KP-ERROR", errorMessage);
    }

    public Observable<Boolean> checkServerReachable() {
        return RxWrap.create(Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                final Socket sock = new Socket();
                sock.connect(new InetSocketAddress(settingsManager.getServerIP(), Integer.parseInt(settingsManager.getServerPort())), 2000);
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
