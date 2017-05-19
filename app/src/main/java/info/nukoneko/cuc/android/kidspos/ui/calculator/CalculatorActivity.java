package info.nukoneko.cuc.android.kidspos.ui.calculator;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityCalculatorBinding;
import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventSendFinish;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity;
import info.nukoneko.cuc.android.kidspos.util.rx.RxWrap;
import info.nukoneko.cuc.kidspos4j.api.APIManager;
import info.nukoneko.cuc.kidspos4j.model.ModelItem;
import info.nukoneko.cuc.kidspos4j.model.ModelStaff;
import info.nukoneko.cuc.kidspos4j.model.ModelStore;

public class CalculatorActivity extends BaseActivity implements CalculatorLayout.Listener, AccountResultDialogFragment.Listener {
    private static final String EXTRA_SUM_PRICE = "sum_price";
    private static final String EXTRA_SALE_ITEMS = "sales_model";

    private int mReceiveMoney = 0;
    private ActivityCalculatorBinding mBinding;
    private DialogFragment mDialogFragment = null;

    public static void startActivity(@NonNull final Context context,
                                     int sumPrice,
                                     @NonNull List<ModelItem> saleItems) {
        if (sumPrice == 0) return;

        final Intent intent = new Intent(context, CalculatorActivity.class) {{
            putExtra(EXTRA_SUM_PRICE, sumPrice);
            putExtra(EXTRA_SALE_ITEMS, saleItems.toArray(new ModelItem[saleItems.size()]));
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

    @NonNull
    private ModelItem[] getSaleItems() {
        //noinspection ConstantConditions
        return (ModelItem[]) getIntent().getExtras().getSerializable(EXTRA_SALE_ITEMS);
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
        for (ModelItem item : getSaleItems()) {
            sum += String.valueOf(item.getId()) + ",";
        }
        sum = sum.substring(0, sum.length() - 1);
        final ModelStaff staff = getApp().getCurrentStaff();
        final ModelStore store = getApp().getCurrentStore();
        final String staffBarcode = staff == null ? "" : staff.getBarcode();

        if (getApp().isPracticeModeEnabled()) {
            Toast.makeText(this, "練習モードのためレシートは出ません", Toast.LENGTH_SHORT).show();
            finishActivity();
        } else {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("送信しています");
            RxWrap.create(APIManager.Sale().createSale(mReceiveMoney, getSaleItems().length,
                    getSumPrice(), sum, store.getId(), staffBarcode), dialog, bindToLifecycle())
                    .subscribe(
                            modelSale -> finishActivity(),
                            throwable -> AlertUtil.showErrorDialog(this, throwable, (dialogInterface, i) -> finishActivity()));
        }
    }

    private void finishActivity() {
        if (mDialogFragment != null) mDialogFragment.getDialog().cancel();
        KPEventBusProvider.getInstance().send(new KPEventSendFinish());
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
