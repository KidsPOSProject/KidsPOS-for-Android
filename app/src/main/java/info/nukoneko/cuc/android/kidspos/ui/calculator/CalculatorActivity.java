package info.nukoneko.cuc.android.kidspos.ui.calculator;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityCalculatorBinding;
import info.nukoneko.cuc.android.kidspos.entity.Item;
import info.nukoneko.cuc.android.kidspos.entity.Sale;
import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.SuccessSentSaleEvent;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity;
import info.nukoneko.cuc.android.kidspos.util.rx.RxWrap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;

public class CalculatorActivity extends BaseActivity implements CalculatorLayout.Listener, AccountResultDialogFragment.Listener {
    private static final String EXTRA_SUM_PRICE = "sum_price";
    private static final String EXTRA_SALE_ITEMS = "sales_model";

    private int mReceiveMoney = 0;
    private ActivityCalculatorBinding mBinding;
    private DialogFragment mDialogFragment = null;

    public static void startActivity(@NonNull final Context context,
                                     final int sumPrice,
                                     @NonNull final List<Item> saleItems) {
        if (sumPrice == 0) return;

        final Intent intent = new Intent(context, CalculatorActivity.class) {{
            putExtra(EXTRA_SUM_PRICE, sumPrice);
            putExtra(EXTRA_SALE_ITEMS, saleItems.toArray(new Item[saleItems.size()]));
        }};

        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_calculator);
        mBinding.account.setText(String.valueOf(getSumPrice()));
    }

    private int getSumPrice() {
        return getIntent().getExtras().getInt(EXTRA_SUM_PRICE);
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    private Item[] getSaleItems() {
        final Parcelable[] parcelables = getIntent().getExtras().getParcelableArray(EXTRA_SALE_ITEMS);
        Item[] ret = new Item[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            ret[i] = (Item) parcelables[i];
        }
        return ret;
    }

    @Override
    public void onClickNumber(int number) {
        if (10000 > mReceiveMoney && mReceiveMoney > 999) return;
        if (mReceiveMoney == 0) {
            mReceiveMoney = number;
        } else {
            mReceiveMoney = mReceiveMoney * 10 + number;
        }
        mBinding.money.setText(String.valueOf(mReceiveMoney));
    }

    @Override
    public void onClickClear(View view) {
        if (10 > mReceiveMoney) {
            mReceiveMoney = 0;
        } else {
            mReceiveMoney = (int) Math.floor(mReceiveMoney / 10);
        }
        mBinding.money.setText(String.valueOf(mReceiveMoney));
    }

    @Override
    public void onClickEnd(View view) {
        if (!this.isValueCheck()) return;

        mDialogFragment = AccountResultDialogFragment.newInstance(R.string.dialog_kakunin, getSumPrice(), this.mReceiveMoney);
        mDialogFragment.setCancelable(false);
        mDialogFragment.show(getSupportFragmentManager(), "yesNoDialog");
    }

    public boolean isValueCheck() {
        return getSumPrice() <= this.mReceiveMoney;
    }

    private void send() {
        String sum = "";
        for (Item item : getSaleItems()) {
            sum += String.valueOf(item.getId()) + ",";
        }
        sum = sum.substring(0, sum.length() - 1);
        final Staff staff = getApp().getCurrentStaff();
        final Store store = getApp().getCurrentStore();
        final String staffBarcode = staff == null ? "" : staff.getBarcode();
        final int storeId = store == null ? 0 : store.getId();

        if (getApp().isPracticeModeEnabled()) {
            Toast.makeText(this, "練習モードのためレシートは出ません", Toast.LENGTH_SHORT).show();
            finishActivity();
        } else {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("送信しています");
            dialog.show();

            getApp().getApiService()
                    .createSale(mReceiveMoney, getSaleItems().length, getSumPrice(), sum, storeId, staffBarcode)
                    .enqueue(new Callback<Sale>() {
                        @Override
                        public void onResponse(Call<Sale> call, Response<Sale> response) {
                            dialog.dismiss();
                            finishActivity();
                        }

                        @Override
                        public void onFailure(Call<Sale> call, Throwable t) {
                            dialog.dismiss();
                            AlertUtil.showErrorDialog(CalculatorActivity.this, t, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finishActivity();
                                }
                            });
                        }
                    });
        }
    }

    private void finishActivity() {
        if (mDialogFragment != null) mDialogFragment.getDialog().cancel();
        KPEventBusProvider.getInstance().send(new SuccessSentSaleEvent());
        finish();
    }

    @Override
    public void onClickPositiveButton(Dialog dialog) {
        send();
    }

    @Override
    public void onClickNegativeButton(Dialog dialog) {
        dialog.cancel();
    }
}
