package info.nukoneko.cuc.android.kidspos;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import info.nukoneko.cuc.android.kidspos.api.APIService;
import info.nukoneko.cuc.android.kidspos.api.APIAdapter;
import info.nukoneko.cuc.android.kidspos.repository.ItemManager;
import info.nukoneko.cuc.android.kidspos.repository.SaleManager;
import info.nukoneko.cuc.android.kidspos.api.manager.SettingsManager;
import info.nukoneko.cuc.android.kidspos.repository.StaffManager;
import info.nukoneko.cuc.android.kidspos.repository.StoreManager;
import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.event.StaffUpdateEvent;
import info.nukoneko.cuc.android.kidspos.event.StoreUpdateEvent;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class KidsPOSApplication extends Application {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private StoreManager mStoreManager = null;
    private StaffManager mStaffManager = null;
    private SaleManager mSaleManager = null;
    private ItemManager mItemManager = null;

    private SettingsManager mSettingsManager = null;
    private APIService mApiService = null;
    private final APIAdapter mApiAdapter = new APIAdapter() {
        @Override
        public APIService getAPIService() {
            return KidsPOSApplication.this.getApiService();
        }
    };
    private Scheduler mDefaultSubscribeScheduler;

    @Nullable
    public static KidsPOSApplication get(@NonNull Context context) {
        return (KidsPOSApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.addLogAdapter(new AndroidLogAdapter());

        mStoreManager = new StoreManager(this, mApiAdapter, new StoreManager.Listener() {
            @Override
            public void onUpdateStaff(@NonNull Staff staff) {
                postEvent(new StaffUpdateEvent(staff));
            }

            @Override
            public void onUpdateStore(@NonNull Store store) {
                postEvent(new StoreUpdateEvent(store));
            }
        });

        mSaleManager = new SaleManager(this, mApiAdapter);

        mStaffManager = new StaffManager(this, mApiAdapter);

        mItemManager = new ItemManager(this, mApiAdapter);

        mSettingsManager = new SettingsManager(this);
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

    public String getServerIpPortText() {
        return String.format(Locale.getDefault(), "%s:%s", getServerIp(), getServerPort());
    }

    public String getServerIp() {
        return mSettingsManager.getServerIP();
    }

    public String getServerPort() {
        return mSettingsManager.getServerPort();
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

    public Scheduler defaultSubscribeScheduler() {
        if (mDefaultSubscribeScheduler == null) {
            mDefaultSubscribeScheduler = Schedulers.io();
        }
        return mDefaultSubscribeScheduler;
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    public void setDefaultSubscribeScheduler(Scheduler subscribeScheduler) {
        mDefaultSubscribeScheduler = subscribeScheduler;
    }

    private APIService getApiService() {
        if (mApiService == null) {
            final OkHttpClient.Builder builder = new OkHttpClient.Builder();
            OkHttpClient httpClient = builder.build();
            return new Retrofit.Builder()
                    .baseUrl("http://" + getServerIpPortText() + "/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build().create(APIService.class);
        } else {
            return mApiService;
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    public void setApiService(APIService apiService) {
        mApiService = apiService;
    }

    public StoreManager getStoreManager() {
        return mStoreManager;
    }

    public SaleManager getSaleManager() {
        return mSaleManager;
    }

    public StaffManager getStaffManager() {
        return mStaffManager;
    }

    public ItemManager getItemManager() {
        return mItemManager;
    }
}
