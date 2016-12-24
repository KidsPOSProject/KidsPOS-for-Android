package info.nukoneko.cuc.android.kidspos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.StoreManager;
import info.nukoneko.cuc.android.kidspos.common.CommonActivity;
import info.nukoneko.cuc.android.kidspos.util.KPLogger;
import info.nukoneko.cuc.android.kidspos.util.KPToast;
import info.nukoneko.cuc.android.kidspos.view.CalcView;
import info.nukoneko.cuc.android.kidspos.view.YesNoDialog;
import info.nukoneko.kidspos4j.api.APIManager;
import info.nukoneko.kidspos4j.model.JSONConvertor;
import info.nukoneko.kidspos4j.model.ModelItem;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by TEJNEK on 2015/10/12.
 */

public class CalculatorActivity extends CommonActivity implements CalcView.OnItemClickListener {
    private static final String EXTRA_VALUE = "EXTRA_VALUE";
    private static final String EXTRA_MODEL_SALES = "EXTRA_MODEL_SALES";

    public static final String RESULT_KEY = "EXTRA_BOOLEAN";

    @Bind(R.id.account)
    TextView mAccount;

    @Bind(R.id.money) TextView mMoney;

    @Bind(R.id.calc)
    CalcView mCalc;

    private Integer sumPrice = 0;

    int receiveMoney = 0;

    ModelItem[] items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_calculator);

        this.mCalc.setOnItemClickListener(this);

        Bundle extras = getIntent().getExtras();
        this.sumPrice = extras.getInt(EXTRA_VALUE);
        this.items = (ModelItem[]) extras.getSerializable(EXTRA_MODEL_SALES);
        this.mAccount.setText(String.valueOf(this.sumPrice));
    }

    public static void startActivity(Activity activity,
                                     int requestCode,
                                     @NonNull Integer price,
                                     @NonNull List<ModelItem> items) {
        if (price == 0) {
            KPToast.showToast("値段が正しく読み込まれませんでした");
            return;
        }

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
        this.mMoney.setText(String.valueOf(this.receiveMoney));
    }

    @Override
    public void onClickClear() {
        if (10 > this.receiveMoney){
            this.receiveMoney = 0;
        }else {
            this.receiveMoney = (int)Math.floor(this.receiveMoney / 10);
        }
        this.mMoney.setText(String.valueOf(this.receiveMoney));
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
        if (sumPrice > this.receiveMoney){
            KPToast.showToast("うけとったかずがおかしいよ?");
            return false;
        }
        return true;
    }

    public void send(){

        String sum = "";
        for (ModelItem item : this.items) {
            sum += String.valueOf(item.getId()) + ",";
        }
        sum = sum.substring(0, sum.length() - 1);
        String staffBarcode = StoreManager.getStoreStaff() == null ? "" : StoreManager.getStoreStaff().getBarcode();
        APIManager.Sale().createSale(this.receiveMoney, this.items.length, this.sumPrice, sum,
                StoreManager.getStore().getId(), staffBarcode
                )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    KPToast.showToast("送信に失敗しました");
                    return null;
                })
                .subscribe(modelSale -> {
                    KPLogger.i(JSONConvertor.toJSON(modelSale));
                });
    }

    private void setActivityResult(Boolean result){
        finish();
    }
}
