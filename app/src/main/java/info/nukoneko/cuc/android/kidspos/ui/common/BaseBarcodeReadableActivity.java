package info.nukoneko.cuc.android.kidspos.ui.common;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.orhanobut.logger.Logger;

import info.nukoneko.cuc.android.kidspos.util.BarcodePrefix;
import info.nukoneko.cuc.android.kidspos.util.logger.LogFilter;

public abstract class BaseBarcodeReadableActivity extends BaseActivity {
    private String mInputValue = "";
    private boolean mFlip = false;

    public abstract void onBarcodeInput(@NonNull String barcode, BarcodePrefix prefix);

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return readBarcode(event);
    }

    boolean readBarcode(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            Logger.t(LogFilter.BARCODE.name()).d(mInputValue);
            if (!TextUtils.isEmpty(mInputValue) && mInputValue.length() >= 5) {
                if (mInputValue.length() == 10) {
                    final String prefix = mInputValue.substring(2, 4);
                    onBarcodeInput(mInputValue, BarcodePrefix.prefixOf(prefix));
                } else if (getApp().isPracticeModeEnabled() || getApp().isTestModeEnabled()) {
                    onBarcodeInput(mInputValue, BarcodePrefix.prefixOf(mInputValue));
                } else {
                    Logger.e("Illegal InputValue: " + mInputValue);
                }
            }
            mInputValue = "";
            return false;
        }

        if (mFlip) {
            mInputValue += String.valueOf(event.getKeyCode() - 7);
            mFlip = false;
        } else {
            mFlip = true;
        }
        return super.dispatchKeyEvent(event);
    }
}
