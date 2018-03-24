package info.nukoneko.cuc.android.kidspos.ui.main.storelist;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ItemStoreListBinding;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.viewmodel.ItemStoreViewModel;

public final class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> implements ItemStoreViewModel.Listener {

    private List<Store> mStores;
    private OnItemClickListener mListener;

    StoreAdapter(OnItemClickListener listener) {
        mStores = Collections.emptyList();
        mListener = listener;
    }

    void setStores(List<Store> stores) {
        mStores = stores;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Store store = mStores.get(position);
        holder.bindStore(store, this);
    }

    @Override
    public int getItemCount() {
        return mStores.size();
    }

    @Override
    public void onStoreSelected(Store store) {
        if (mListener != null) mListener.onItemClick(store);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemStoreListBinding mBinding;

        ViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        private void bindStore(Store store, ItemStoreViewModel.Listener listener) {
            if (mBinding.getViewModel() == null) {
                mBinding.setViewModel(new ItemStoreViewModel(store, listener));
            } else {
                mBinding.getViewModel().setStore(store);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Store store);
    }
}
