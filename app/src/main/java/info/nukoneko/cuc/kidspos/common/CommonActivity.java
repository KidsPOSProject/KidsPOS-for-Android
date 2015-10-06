package info.nukoneko.cuc.kidspos.common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;

import info.nukoneko.cuc.kidspos.model.ItemObject;
import info.nukoneko.cuc.kidspos.util.KPLogger;

/**
 * created at 2015/06/13.
 */
abstract public class CommonActivity extends AppCompatActivity {
    String TAG = "CommonActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TAG = getLocalClassName();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    String inputValue = "";
    boolean flip = false;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            if (inputValue.length() == 13){
                KPLogger.d("READ-END", inputValue);
                onInputBarcode(inputValue);

            }else {
                KPLogger.d("READ-ERROR", inputValue);
            }
            inputValue = "";
            return false;
        }

        int v = event.getKeyCode() - 7;
        if (flip) {
            inputValue += String.valueOf(v);
            flip = false;
        }else {
            flip = true;
        }
        return super.dispatchKeyEvent(event);
    }

    protected void onInputBarcode(String barcode){
        // Stub
    }

    public String getTAG(){
        return this.TAG;
    }
}
