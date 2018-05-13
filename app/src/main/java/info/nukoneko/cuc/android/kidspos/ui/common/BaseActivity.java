package info.nukoneko.cuc.android.kidspos.ui.common;

import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;

public abstract class BaseActivity extends AppCompatActivity {
    protected boolean shouldEventSubscribes() {
        return false;
    }

    protected KidsPOSApplication getApp() {
        return KidsPOSApplication.get(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (shouldEventSubscribes()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (shouldEventSubscribes()) {
            EventBus.getDefault().unregister(this);
        }
    }
}
