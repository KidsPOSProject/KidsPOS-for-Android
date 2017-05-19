package info.nukoneko.cuc.android.kidspos.ui.common;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import info.nukoneko.cuc.kidspos4j.util.config.BarcodeRule;

public abstract class BaseBarcodeReadableActivity extends BaseActivity {
    private String mInputValue = "";
    private boolean mFlip = false;

    public abstract void onInputBarcode(@NonNull String barcode, BarcodeRule.BARCODE_PREFIX type);

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (TextUtils.isEmpty(mInputValue) || 5 > mInputValue.length()) return false;
            final String typeCode = mInputValue.substring(2, 4);
            if (mInputValue.length() == 10) {
                onInputBarcode(mInputValue, BarcodeRule.BARCODE_PREFIX.typeOf(typeCode));
            } else if (getApp().isPracticeModeEnabled() || getApp().isTestModeEnabled()) {
                onInputBarcode(mInputValue, BarcodeRule.BARCODE_PREFIX.typeOf(mInputValue));
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
