package info.nukoneko.cuc.android.kidspos.api.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;
import info.nukoneko.cuc.android.kidspos.api.APIService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiManager {
    private final OkHttpClient mOkHttpClient;
    private final Context mContext;
    private APIService mApiService;

    public ApiManager(@NonNull final Context context) {
        mContext = context;
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        mOkHttpClient = builder.build();
    }

    public void updateApiService() {
        mApiService = new Retrofit.Builder()
                .baseUrl("http://" + KidsPOSApplication.get(mContext).getServerIpPortText() + "/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build().create(APIService.class);
    }

    public APIService getApiService() {
        if (mApiService == null) {
            updateApiService();
        }
        return mApiService;
    }
}
