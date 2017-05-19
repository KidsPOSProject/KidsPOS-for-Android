package info.nukoneko.cuc.android.kidspos.ui.calculator;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

public final class AccountResultDialogFragmentViewModel {
    private ObservableField<String> mTitle = new ObservableField<>();
    private ObservableInt mPrice = new ObservableInt();
    private ObservableInt mReceiveMoney = new ObservableInt();

    AccountResultDialogFragmentViewModel(@NonNull String title, int price, int receiveMoney) {
        mTitle.set(title);
        mPrice.set(price);
        mReceiveMoney.set(receiveMoney);
    }

    public ObservableInt getPrice() {
        return mPrice;
    }

    public ObservableInt getReceiveMoney() {
        return mReceiveMoney;
    }

    public ObservableField<String> getTitle() {
        return mTitle;
    }
}
