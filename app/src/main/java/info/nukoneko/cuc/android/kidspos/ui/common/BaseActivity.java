package info.nukoneko.cuc.android.kidspos.ui.common;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

public abstract class BaseActivity extends AppCompatActivity {
    private String mInputValue = "";
    private boolean mFlip = false;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (mInputValue.length() == 10) {
                onInputBarcode(mInputValue);
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

    protected void onInputBarcode(@NonNull String barcode){
        // Stub
    }
}
