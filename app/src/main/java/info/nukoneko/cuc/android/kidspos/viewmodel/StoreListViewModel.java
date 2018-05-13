package info.nukoneko.cuc.android.kidspos.viewmodel;

import android.content.Context;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public final class StoreListViewModel implements ViewModel {

    public ObservableInt progressVisibility;
    public ObservableInt recyclerViewVisibility;
    public ObservableInt errorButtonVisibility;

    @Nullable
    private Context mContext;
    @Nullable
    private DataListener mListener;

    public StoreListViewModel(@NonNull Context context, @NonNull DataListener listener) {
        mContext = context;
        mListener = listener;

        progressVisibility = new ObservableInt(View.GONE);
        recyclerViewVisibility = new ObservableInt(View.GONE);
        errorButtonVisibility = new ObservableInt(View.GONE);
    }

    @Override
    public void destroy() {
        mContext = null;
        mListener = null;
    }

    public void onActivityCreated() {
        loadStores();
    }

    private void loadStores() {
        progressVisibility.set(View.VISIBLE);
        recyclerViewVisibility.set(View.GONE);
        errorButtonVisibility.set(View.GONE);

        if (mContext == null) return;
        KidsPOSApplication app = KidsPOSApplication.get(mContext);
        if (app == null) return;
        app.getStoreRepository().fetchStores()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(app.defaultSubscribeScheduler())
                .subscribe(new Consumer<List<Store>>() {
                    @Override
                    public void accept(List<Store> stores) throws Exception {
                        if (mListener != null) mListener.onStoresChanged(stores);
                        progressVisibility.set(View.GONE);
                        if (!stores.isEmpty()) {
                            recyclerViewVisibility.set(View.VISIBLE);
                        } else {
                            errorButtonVisibility.set(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mListener != null) mListener.onError("リストの取得に失敗しました");
                        progressVisibility.set(View.GONE);
                        errorButtonVisibility.set(View.VISIBLE);
                    }
                });
    }

    public interface DataListener {
        void onStoresChanged(List<Store> stores);

        void onError(String message);
    }
}
