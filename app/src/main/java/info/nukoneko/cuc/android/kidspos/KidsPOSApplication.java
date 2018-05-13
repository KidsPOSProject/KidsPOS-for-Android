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
import info.nukoneko.cuc.android.kidspos.repository.ItemRepository;
import info.nukoneko.cuc.android.kidspos.repository.SaleRepository;
import info.nukoneko.cuc.android.kidspos.api.manager.SettingsManager;
import info.nukoneko.cuc.android.kidspos.repository.StaffRepository;
import info.nukoneko.cuc.android.kidspos.repository.StoreRepository;
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

    private StoreRepository mStoreRepository = null;
    private StaffRepository mStaffRepository = null;
    private SaleRepository mSaleRepository = null;
    private ItemRepository mItemRepository = null;

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

        mStoreRepository = new StoreRepository(this, mApiAdapter, new StoreRepository.Listener() {
            @Override
            public void onUpdateStaff(@NonNull Staff staff) {
                postEvent(new StaffUpdateEvent(staff));
            }

            @Override
            public void onUpdateStore(@NonNull Store store) {
                postEvent(new StoreUpdateEvent(store));
            }
        });

        mSaleRepository = new SaleRepository(this, mApiAdapter);

        mStaffRepository = new StaffRepository(this, mApiAdapter);

        mItemRepository = new ItemRepository(this, mApiAdapter);

        mSettingsManager = new SettingsManager(this);
    }

    @Nullable
    public Store getCurrentStore() {
        return mStoreRepository.getCurrentStore();
    }

    @Nullable
    public Staff getCurrentStaff() {
        return mStoreRepository.getCurrentStaff();
    }

    public void updateCurrentStore(Store store) {
        mStoreRepository.setCurrentStore(store);
    }

    public void updateCurrentStaff(Staff staff) {
        mStoreRepository.setCurrentStaff(staff);
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

    public StoreRepository getStoreRepository() {
        return mStoreRepository;
    }

    public SaleRepository getSaleRepository() {
        return mSaleRepository;
    }

    public StaffRepository getStaffRepository() {
        return mStaffRepository;
    }

    public ItemRepository getItemRepository() {
        return mItemRepository;
    }
}
