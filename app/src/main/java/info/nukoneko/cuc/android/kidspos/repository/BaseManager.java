package info.nukoneko.cuc.android.kidspos.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import info.nukoneko.cuc.android.kidspos.api.APIService;
import info.nukoneko.cuc.android.kidspos.api.APIAdapter;

public abstract class BaseManager {
    @NonNull
    private final Context mContext;
    @NonNull
    private final APIAdapter mApiAdapter;

    BaseManager(@NonNull Context context, @NonNull APIAdapter apiAdapter) {
        mContext = context;
        mApiAdapter = apiAdapter;
    }

    @NonNull
    protected final Context getContext() {
        return mContext;
    }

    final APIService getAPIService() {
        return mApiAdapter.getAPIService();
    }

    final SharedPreferences getSharedPreference(String preferenceName) {
        return mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }
}
