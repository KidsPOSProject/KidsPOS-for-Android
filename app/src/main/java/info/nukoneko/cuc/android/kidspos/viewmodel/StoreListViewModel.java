package info.nukoneko.cuc.android.kidspos.viewmodel;

import android.content.Context;
import android.databinding.ObservableInt;
import android.view.View;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class StoreListViewModel implements ViewModel {

    public ObservableInt progressVisibility;
    public ObservableInt recyclerViewVisibility;
    public ObservableInt errorButtonVisibility;

    private Context mContext;
    private DataListener mListener;
    private List<Store> mStores;

    public StoreListViewModel(Context context, DataListener listener) {
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

        KidsPOSApplication app = KidsPOSApplication.get(mContext);
        app.getApiService().getStoreList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(app.defaultSubscribeScheduler())
                .subscribe(stores -> StoreListViewModel.this.mStores = stores,
                        throwable -> {
                            if (mListener != null) mListener.onError("リストの取得に失敗しました");
                            progressVisibility.set(View.GONE);
                            errorButtonVisibility.set(View.VISIBLE);
                        }, () -> {
                            if (mListener != null) mListener.onStoresChanged(mStores);
                            progressVisibility.set(View.GONE);
                            if (!mStores.isEmpty()) {
                                recyclerViewVisibility.set(View.VISIBLE);
                            } else {
                                errorButtonVisibility.set(View.VISIBLE);
                            }
                        });
    }

    public interface DataListener {
        void onStoresChanged(List<Store> stores);

        void onError(String message);
    }
}
