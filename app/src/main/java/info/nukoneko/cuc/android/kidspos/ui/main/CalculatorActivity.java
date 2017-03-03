package info.nukoneko.cuc.android.kidspos.ui.main;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityCalculatorBinding;
import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventSendFinish;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity;
import info.nukoneko.cuc.android.kidspos.ui.view.CalcView;
import info.nukoneko.cuc.android.kidspos.ui.view.YesNoDialog;
import info.nukoneko.cuc.android.kidspos.util.rx.RxWrap;
import info.nukoneko.kidspos4j.api.APIManager;
import info.nukoneko.kidspos4j.model.ModelItem;
import info.nukoneko.kidspos4j.model.ModelStaff;
import info.nukoneko.kidspos4j.model.ModelStore;

public class CalculatorActivity extends BaseActivity implements CalcView.OnItemClickListener {
    private static final String EXTRA_VALUE = "EXTRA_VALUE";
    private static final String EXTRA_MODEL_SALES = "EXTRA_MODEL_SALES";

    private Integer sumPrice = 0;

    int mReceiveMoney = 0;

    ModelItem[] mItems;

    ActivityCalculatorBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calculator);

        binding.calc.setOnItemClickListener(this);

        Bundle extras = getIntent().getExtras();
        this.sumPrice = extras.getInt(EXTRA_VALUE);
        this.mItems = (ModelItem[]) extras.getSerializable(EXTRA_MODEL_SALES);
        binding.account.setText(String.valueOf(this.sumPrice));
    }

    public static void startActivity(final Context context,
                                     @NonNull Integer price,
                                     @NonNull List<ModelItem> items) {
        if (price == 0) return;

        Intent intent = new Intent(context, CalculatorActivity.class);
        intent.putExtra(EXTRA_VALUE, price);
        intent.putExtra(EXTRA_MODEL_SALES, items.toArray(new ModelItem[items.size()]));
        context.startActivity(intent);
    }

    @Override
    public void onClickNumber(int number) {
        if (10000 > this.mReceiveMoney && this.mReceiveMoney > 999){
            return;
        }
        if (this.mReceiveMoney == 0){
            this.mReceiveMoney = number;
        }else {
            this.mReceiveMoney = this.mReceiveMoney * 10 + number;
        }
        binding.money.setText(String.valueOf(this.mReceiveMoney));
    }

    @Override
    public void onClickClear() {
        if (10 > this.mReceiveMoney){
            this.mReceiveMoney = 0;
        }else {
            this.mReceiveMoney = (int)Math.floor(this.mReceiveMoney / 10);
        }
        binding.money.setText(String.valueOf(this.mReceiveMoney));
    }

    private DialogFragment mDialogFragment = null;

    @Override
    public void onClickEnd() {
        if(!this.isValueCheck()) return;

        mDialogFragment = YesNoDialog.newInstance(R.string.dialog_kakunin, this.sumPrice, this.mReceiveMoney);
        ((YesNoDialog)mDialogFragment).setNegativeInterface(Dialog::cancel);
        ((YesNoDialog)mDialogFragment).setPositiveInterface(dialog1 -> send());
        mDialogFragment.setCancelable(false);
        mDialogFragment.show(getSupportFragmentManager(), "yesNoDialog");
    }

    public boolean isValueCheck(){
        return sumPrice <= this.mReceiveMoney;
    }

    private int mErrorCount = 0;
    public void send() {
        String sum = "";
        for (ModelItem item : this.mItems) {
            sum += String.valueOf(item.getId()) + ",";
        }
        sum = sum.substring(0, sum.length() - 1);
        final ModelStaff staff = getApp().getCurrentStaff();
        final ModelStore store = getApp().getCurrentStore();
        String staffBarcode = staff == null ? "" : staff.getBarcode();

        if (getApp().isPracticeModeEnabled()) {
            Toast.makeText(this, "練習モードのためレシートは出ません", Toast.LENGTH_SHORT).show();
            finishActivity();
        } else {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("送信しています");
            RxWrap.create(APIManager.Sale().createSale(mReceiveMoney, mItems.length,
                    this.sumPrice, sum,
                    store.getId(), staffBarcode), dialog, bindToLifecycle())
                    .subscribe(modelSale -> {
                        finishActivity();
                    }, throwable -> {
                        AlertUtil.showErrorDialog(this, throwable,
                                1 > mErrorCount ? null : (DialogInterface.OnClickListener) (dialog1, which) -> finishActivity());
                    });
        }
    }

    private void finishActivity() {
        if (mDialogFragment != null) mDialogFragment.getDialog().cancel();
        KPEventBusProvider.getInstance().send(new KPEventSendFinish());
        finish();
    }
}
