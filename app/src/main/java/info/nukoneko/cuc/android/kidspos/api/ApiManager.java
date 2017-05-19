package info.nukoneko.cuc.android.kidspos.api;

import android.content.Context;
import android.support.annotation.NonNull;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiManager {
    private final OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();
    private final Context mContext;
    private APIService mApiService;

    public ApiManager(@NonNull final Context context) {
        mContext = context;
    }

    public void updateApiService() {
        mApiService = new Retrofit.Builder()
                .baseUrl("http://" + KidsPOSApplication.get(mContext).getServerIpPortText() + "/api/")
                .addConverterFactory(GsonConverterFactory.create())
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
