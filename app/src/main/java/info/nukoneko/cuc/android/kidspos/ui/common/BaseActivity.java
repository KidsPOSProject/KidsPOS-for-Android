package info.nukoneko.cuc.android.kidspos.ui.common;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import info.nukoneko.cuc.android.kidspos.KidPOSApplication;

public abstract class BaseActivity extends RxAppCompatActivity {
    protected KidPOSApplication getApp() {
        return KidPOSApplication.get(this);
    }
}
