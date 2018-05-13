package info.nukoneko.cuc.android.kidspos.viewmodel;

import android.databinding.BaseObservable;
import android.view.View;

import info.nukoneko.cuc.android.kidspos.entity.Store;

public final class ItemStoreViewModel extends BaseObservable implements ViewModel {
    private final Listener mListener;
    private Store mStore;

    public ItemStoreViewModel(Store store, Listener listener) {
        mStore = store;
        mListener = listener;
    }

    @SuppressWarnings("unused")
    public void onItemClick(View view) {
        mListener.onStoreSelected(mStore);
    }

    public void setStore(Store store) {
        mStore = store;
        notifyChange();
    }

    public String getStoreName() {
        return mStore.getName();
    }

    @Override
    public void destroy() {
        // nothing
    }

    public interface Listener {
        void onStoreSelected(Store store);
    }
}
