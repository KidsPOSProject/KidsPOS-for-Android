package info.nukoneko.cuc.kidspos.common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * created at 2015/06/13.
 */
public class CommonActivity extends AppCompatActivity {
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
    public String getTAG(){
        return this.TAG;
    }
}
