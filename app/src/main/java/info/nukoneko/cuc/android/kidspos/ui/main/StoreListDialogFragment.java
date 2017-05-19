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

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.FragmentDialogStoreListBinding;
import info.nukoneko.cuc.android.kidspos.databinding.ItemStoreListBinding;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        mAdapter = new ViewAdapter(getContext(), KidsPOSApplication.get(getContext()).getCurrentStore());
        binding.recyclerView.setAdapter(mAdapter);

        binding.setStatus("読み込み中...");

        final KidsPOSApplication app = KidsPOSApplication.get(getContext());
        app.getApiService().getStoreList()
                .enqueue(new Callback<List<Store>>() {
                    @Override
                    public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                        mAdapter.addAll(response.body());
                        binding.setStatus("完了");
                    }

                    @Override
                    public void onFailure(Call<List<Store>> call, Throwable t) {
                        binding.setStatus("失敗");
                        AlertUtil.showErrorDialog(getContext(), "リストの取得に失敗しました", false, (dialog, which) -> getDialog().dismiss());
                    }
                });

        return new AlertDialog.Builder(getContext())
                .setTitle("おみせの変更")
                .setView(binding.getRoot())
                .setPositiveButton("決定", (d, w) -> {
                    KidsPOSApplication.get(getContext()).updateCurrentStore(mAdapter.getCurrentStore());
                    d.dismiss();
                }).create();
    }

    static class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

        private List<Store> mData = new ArrayList<>();
        @Nullable
        private Store mCurrentStore;

        private final Context mContext;

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
            if (mCurrentStore == null) holder.getBinding().setSelected(false);
            else {
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
