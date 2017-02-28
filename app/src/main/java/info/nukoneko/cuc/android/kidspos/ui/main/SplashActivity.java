package info.nukoneko.cuc.android.kidspos.ui.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.view.Window;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.common.CommonActivity;
import info.nukoneko.cuc.android.kidspos.databinding.ActivitySplashBinding;

public class SplashActivity extends CommonActivity {
    Handler handler;
    myRunnable task;

    ActivitySplashBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState){
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.hide();
        handler = new Handler();
        task = new myRunnable();
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(task, 2000);
    }

    @Override
    public void onPause(){
        handler.removeCallbacks(task);
        super.onPause();
    }

    class myRunnable implements Runnable{
        @Override
        public void run() {
            startActivity();
        }
    }
    void startActivity(){
        if(isFinishing()) return;
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
