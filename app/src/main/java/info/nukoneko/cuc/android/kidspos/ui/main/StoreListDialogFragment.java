package info.nukoneko.cuc.android.kidspos.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.FragmentDialogStoreListBinding;
import info.nukoneko.cuc.android.kidspos.databinding.ItemStoreListBinding;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment;
import info.nukoneko.cuc.android.kidspos.util.rx.RxWrap;
import rx.Observable;

public final class StoreListDialogFragment extends BaseDialogFragment {
    public static StoreListDialogFragment newInstance() {
        return new StoreListDialogFragment();
    }

    private FragmentDialogStoreListBinding mBinding;
    private ViewAdapter mAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.bind(LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_dialog_store_list, null, false));
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mAdapter = new ViewAdapter(getContext(), KidsPOSApplication.get(getContext()).getCurrentStore());
        mBinding.recyclerView.setAdapter(mAdapter);

        return new AlertDialog.Builder(getContext())
                .setTitle("おみせの変更")
                .setView(mBinding.getRoot())
                .setPositiveButton("決定", (d, w) -> {
                    KidsPOSApplication.get(getContext()).updateCurrentStore(mAdapter.getCurrentStore());
                    d.dismiss();
                }).create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding.setLoading(true);

        final KidsPOSApplication app = KidsPOSApplication.get(getContext());
        final Observable<List<Store>> observable = app.getApiService().getStoreList();
        RxWrap.create(observable, bindToLifecycle())
                .subscribe(stores -> {
                    mBinding.setLoading(false);
                    mAdapter.addAll(stores);
                }, throwable -> {
                    mBinding.setLoading(false);
                    AlertUtil.showErrorDialog(getContext(), "リストの取得に失敗しました", false, (dialog, which) -> getDialog().dismiss());
                });
    }

    static class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {
        @NonNull final private List<Store> mData = new ArrayList<>();
        @NonNull private final Context mContext;
        @Nullable private Store mCurrentStore;

        ViewAdapter(@NonNull final Context context, @Nullable Store currentStore) {
            mContext = context;
            mCurrentStore = currentStore;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_store_list, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Store store = mData.get(position);
            holder.getBinding().setStore(store);
            if (mCurrentStore == null) {
                holder.getBinding().setSelected(false);
            } else {
                holder.getBinding().setSelected(mCurrentStore.getId() == store.getId());
            }
            holder.getBinding().radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mCurrentStore = store;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        void addAll(Collection<Store> stores) {
            mData.addAll(stores);
            notifyDataSetChanged();
        }

        @Nullable
        Store getCurrentStore() {
            return mCurrentStore;
        }

        static final class ViewHolder extends RecyclerView.ViewHolder {
            private ItemStoreListBinding binding;

            ViewHolder(View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }

            public ItemStoreListBinding getBinding() {
                return binding;
            }
        }
    }
}
