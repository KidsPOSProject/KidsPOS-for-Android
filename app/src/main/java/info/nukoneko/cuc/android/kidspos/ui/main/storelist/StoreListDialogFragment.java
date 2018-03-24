package info.nukoneko.cuc.android.kidspos.ui.main.storelist;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.FragmentDialogStoreListBinding;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment;
import info.nukoneko.cuc.android.kidspos.viewmodel.StoreListViewModel;

public final class StoreListDialogFragment extends BaseDialogFragment
        implements StoreListViewModel.DataListener, StoreAdapter.OnItemClickListener {
    private FragmentDialogStoreListBinding mBinding;
    private StoreListViewModel mViewModel;
    private StoreAdapter mAdapter;

    public static StoreListDialogFragment newInstance() {
        return new StoreListDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.bind(LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_dialog_store_list, null, false));
        mViewModel = new StoreListViewModel(getContext(), this);
        mBinding.setViewModel(mViewModel);
        mAdapter = new StoreAdapter(this);
        mBinding.recyclerView.setAdapter(mAdapter);
        setupRecyclerView();

        Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("おみせの変更")
                .setView(mBinding.getRoot())
                .create();

        final Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.onActivityCreated();
    }

    private void setupRecyclerView() {
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStoresChanged(List<Store> stores) {
        mAdapter.setStores(stores);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Store store) {
        KidsPOSApplication.get(getContext()).updateCurrentStore(store);
        dismiss();
    }
}
