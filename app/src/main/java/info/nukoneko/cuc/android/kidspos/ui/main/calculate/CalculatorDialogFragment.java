package info.nukoneko.cuc.android.kidspos.ui.main.calculate;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.FragmentCalculatorBinding;
import info.nukoneko.cuc.android.kidspos.entity.Item;
import info.nukoneko.cuc.android.kidspos.entity.Sale;
import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.event.SuccessSentSaleEvent;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment;
import info.nukoneko.cuc.android.kidspos.util.AlertUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class CalculatorDialogFragment extends BaseDialogFragment implements CalculatorLayout.Listener, AccountResultDialogFragment.Listener {
    private static final String EXTRA_SUM_RIVER = "sum_price";
    private static final String EXTRA_SALE_ITEMS = "sales_model";

    private int mReceiveMoney = 0;
    private FragmentCalculatorBinding mBinding;
    private AccountResultDialogFragment mDialogFragment = null;

    @NonNull
    public static CalculatorDialogFragment newInstance(final int sumRiver, @NonNull final List<Item> saleItems) {
        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SUM_RIVER, sumRiver);
        bundle.putParcelableArray(EXTRA_SALE_ITEMS, saleItems.toArray(new Item[saleItems.size()]));
        final CalculatorDialogFragment fragment = new CalculatorDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_calculator, container, true);
        mBinding.calculatorLayout.setOnCalculatorClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().setCancelable(false);

        final Window dialogWindow = getDialog().getWindow();
        if (dialogWindow != null) {
            dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        mBinding.sumRiver.setText(String.valueOf(getSumRiver()));
        mBinding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValid()) return;

                mDialogFragment = AccountResultDialogFragment.newInstance(getSumRiver(), mReceiveMoney);
                mDialogFragment.setCancelable(false);
                mDialogFragment.setListener(CalculatorDialogFragment.this);
                mDialogFragment.show(getChildFragmentManager(), "yesNoDialog");
            }
        });
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onClickNumber(int number) {
        if (mReceiveMoney > 100000) return;

        if (mReceiveMoney == 0) {
            mReceiveMoney = number;
        } else {
            mReceiveMoney = mReceiveMoney * 10 + number;
        }
        mBinding.receiveRiver.setText(String.valueOf(mReceiveMoney));
    }

    @Override
    public void onClickClear(View view) {
        if (10 > mReceiveMoney) {
            mReceiveMoney = 0;
        } else {
            mReceiveMoney = (int) Math.floor(mReceiveMoney / 10);
        }
        mBinding.receiveRiver.setText(String.valueOf(mReceiveMoney));
    }

    @Override
    public void onClickPositiveButton(Dialog dialog) {
        sendToServer();
    }

    @Override
    public void onClickNegativeButton(Dialog dialog) {
        dialog.cancel();
    }

    private int getSumRiver() {
        return getArguments().getInt(EXTRA_SUM_RIVER);
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    private Item[] getSaleItems() {
        final Parcelable[] parcelables = getArguments().getParcelableArray(EXTRA_SALE_ITEMS);
        Item[] ret = new Item[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            ret[i] = (Item) parcelables[i];
        }
        return ret;
    }

    private boolean isValid() {
        return getSumRiver() <= this.mReceiveMoney;
    }

    private void finishFragment() {
        if (mDialogFragment != null) mDialogFragment.getDialog().cancel();
        getApp().postEvent(new SuccessSentSaleEvent());
        dismiss();
    }

    private void sendToServer() {
        String sum = "";
        for (final Item item : getSaleItems()) {
            sum += String.valueOf(item.getId()) + ",";
        }
        sum = sum.substring(0, sum.length() - 1);
        final Staff staff = getApp().getCurrentStaff();
        final Store store = getApp().getCurrentStore();
        final String staffBarcode = staff == null ? "" : staff.getBarcode();
        final int storeId = store == null ? 0 : store.getId();

        if (getApp().isPracticeModeEnabled()) {
            Toast.makeText(getContext(), "練習モードのためレシートは出ません", Toast.LENGTH_SHORT).show();
            finishFragment();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("送信しています");
            progressDialog.show();

            getApp().getApiService()
                    .createSale(mReceiveMoney, getSaleItems().length, getSumRiver(), sum, storeId, staffBarcode)
                    .enqueue(new Callback<Sale>() {
                        @Override
                        public void onResponse(Call<Sale> call, Response<Sale> response) {
                            progressDialog.dismiss();
                            finishFragment();
                        }

                        @Override
                        public void onFailure(Call<Sale> call, Throwable throwable) {
                            progressDialog.dismiss();
                            AlertUtil.showErrorDialog(getContext(), throwable.getLocalizedMessage(), false,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finishFragment();
                                        }
                                    });
                        }
                    });
        }
    }

    @NonNull
    private KidsPOSApplication getApp() {
        return KidsPOSApplication.get(getContext());
    }
}
