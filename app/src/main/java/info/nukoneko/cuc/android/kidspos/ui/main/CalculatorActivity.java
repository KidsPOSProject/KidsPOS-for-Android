package info.nukoneko.cuc.android.kidspos.ui.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.AppController;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.common.CommonActivity;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityCalculatorBinding;
import info.nukoneko.cuc.android.kidspos.ui.view.CalcView;
import info.nukoneko.cuc.android.kidspos.ui.view.YesNoDialog;
import info.nukoneko.cuc.android.kidspos.util.KPLogger;
import info.nukoneko.kidspos4j.api.APIManager;
import info.nukoneko.kidspos4j.model.JSONConvertor;
import info.nukoneko.kidspos4j.model.ModelItem;
import info.nukoneko.kidspos4j.model.ModelStaff;
import info.nukoneko.kidspos4j.model.ModelStore;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CalculatorActivity extends CommonActivity implements CalcView.OnItemClickListener {
    private static final String EXTRA_VALUE = "EXTRA_VALUE";
    private static final String EXTRA_MODEL_SALES = "EXTRA_MODEL_SALES";

    private Integer sumPrice = 0;

    int receiveMoney = 0;

    ModelItem[] items;

    ActivityCalculatorBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calculator);

        binding.calc.setOnItemClickListener(this);

        Bundle extras = getIntent().getExtras();
        this.sumPrice = extras.getInt(EXTRA_VALUE);
        this.items = (ModelItem[]) extras.getSerializable(EXTRA_MODEL_SALES);
        binding.account.setText(String.valueOf(this.sumPrice));
    }

    public static void startActivity(Activity activity,
                                     int requestCode,
                                     @NonNull Integer price,
                                     @NonNull List<ModelItem> items) {
        if (price == 0) return;

        Intent intent = new Intent(activity, CalculatorActivity.class);
        intent.putExtra(EXTRA_VALUE, price);
        intent.putExtra(EXTRA_MODEL_SALES, items.toArray(new ModelItem[items.size()]));
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onClickNumber(int number) {
        if (10000 > this.receiveMoney && this.receiveMoney > 999){
            return;
        }
        if (this.receiveMoney == 0){
            this.receiveMoney = number;
        }else {
            this.receiveMoney = this.receiveMoney * 10 + number;
        }
        binding.money.setText(String.valueOf(this.receiveMoney));
    }

    @Override
    public void onClickClear() {
        if (10 > this.receiveMoney){
            this.receiveMoney = 0;
        }else {
            this.receiveMoney = (int)Math.floor(this.receiveMoney / 10);
        }
        binding.money.setText(String.valueOf(this.receiveMoney));
    }

    @Override
    public void onClickEnd() {
        KPLogger.d(this.receiveMoney);
        if(!this.isValueCheck()) return;

        YesNoDialog dialog = YesNoDialog.newInstance(R.string.dialog_kakunin, this.sumPrice, this.receiveMoney);
        dialog.setNegativeInterface(Dialog::cancel);

        dialog.setPositiveInterface(dialog1 -> {
            send();
            dialog1.cancel();
            setActivityResult(true);
        });
        dialog.show(getFragmentManager(), "yesno");
    }

    public boolean isValueCheck(){
        return sumPrice <= this.receiveMoney;
    }

    public void send() {
        String sum = "";
        for (ModelItem item : this.items) {
            sum += String.valueOf(item.getId()) + ",";
        }
        sum = sum.substring(0, sum.length() - 1);
        final ModelStaff staff = AppController.get(this).getStoreManager().getCurrentStaff();
        final ModelStore store = AppController.get(this).getStoreManager().getCurrentStore();
        String staffBarcode = staff == null ? "" : staff.getBarcode();
        APIManager.Sale()
                .createSale(this.receiveMoney,
                        this.items.length,
                        this.sumPrice,
                        sum,
                        store.getId(), staffBarcode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(modelSale -> {
                    KPLogger.i(JSONConvertor.toJSON(modelSale));
                }, Throwable::printStackTrace);
    }

    private void setActivityResult(Boolean result){
        finish();
    }
}
