package info.nukoneko.cuc.android.kidspos.ui.launch;

import android.os.Bundle;
import android.os.Handler;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity;
import info.nukoneko.cuc.android.kidspos.ui.main.MainActivity;

public final class LaunchActivity extends BaseActivity {
    private final Handler mHandler = new Handler();
    private final Runnable mTask = new Runnable() {
        @Override
        public void run() {
            if (isFinishing()) return;
            MainActivity.createIntentWithClearTask(LaunchActivity.this);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
}
