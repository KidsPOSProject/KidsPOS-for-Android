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

import info.nukoneko.cuc.android.kidspos.KPApplicationController;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.FragmentDialogStoreListBinding;
import info.nukoneko.cuc.android.kidspos.databinding.ItemStoreListBinding;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment;
import info.nukoneko.cuc.android.kidspos.util.rx.RxWrap;
import info.nukoneko.kidspos4j.api.APIManager;
import info.nukoneko.kidspos4j.model.ModelStore;

public final class StoreListDialogFragment extends BaseDialogFragment {
    public static StoreListDialogFragment newInstance() {
        return new StoreListDialogFragment();
    }

    private ViewAdapter mAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final FragmentDialogStoreListBinding binding = DataBindingUtil.bind(LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_dialog_store_list, null, false));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mAdapter = new ViewAdapter(getContext(), KPApplicationController.get(getContext()).getCurrentStore());
        binding.recyclerView.setAdapter(mAdapter);

        binding.setStatus("読み込み中...");
        RxWrap.create(APIManager.Store().getList(), bindToLifecycle())
                .subscribe(modelStores -> {
                    mAdapter.addAll(modelStores);
                    binding.setStatus("完了");
                }, throwable -> {
                    binding.setStatus("失敗");
                    AlertUtil.showErrorDialog(getContext(), "リストの取得に失敗しました", false, (dialog, which) -> getDialog().dismiss());
                });

        return new AlertDialog.Builder(getContext())
                .setTitle("おみせの変更")
                .setView(binding.getRoot())
                .setPositiveButton("決定", (d, w) -> {
                    KPApplicationController.get(getContext()).updateCurrentStore(mAdapter.getCurrentStore());
                    d.dismiss();
                }).create();
    }

    @SuppressWarnings("WeakerAccess")
    static class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

        private ArrayList<ModelStore> mData = new ArrayList<>();
        @Nullable
        private ModelStore mCurrentStore;

        private final Context mContext;

        public ViewAdapter(@NonNull final Context context, @Nullable ModelStore currentStore) {
            this.mContext = context;
            this.mCurrentStore = currentStore;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_store_list, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ModelStore store = mData.get(position);
            holder.getBinding().setStore(store);
            if (mCurrentStore == null) holder.getBinding().setSelected(false);
            else {
                holder.getBinding().setSelected(mCurrentStore.getId().equals(store.getId()));
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

        public void addAll(Collection<ModelStore> stores) {
            mData.addAll(stores);
            notifyDataSetChanged();
        }

        @Nullable
        public ModelStore getCurrentStore() {
            return mCurrentStore;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
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
