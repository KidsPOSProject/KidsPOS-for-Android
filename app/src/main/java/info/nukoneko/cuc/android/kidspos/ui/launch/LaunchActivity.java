package info.nukoneko.cuc.android.kidspos.ui.launch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity;
import info.nukoneko.cuc.android.kidspos.ui.main.MainActivity;

public final class LaunchActivity extends BaseActivity {
    private final Handler mHandler = new Handler();
    private final Runnable mTask = new Runnable() {
        @Override
        public void run() {
            startActivity();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mTask, 2000);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTask);
        super.onPause();
    }

    private void startActivity() {
        if (isFinishing()) return;
        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
