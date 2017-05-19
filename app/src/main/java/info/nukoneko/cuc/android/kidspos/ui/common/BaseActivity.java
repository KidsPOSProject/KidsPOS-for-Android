package info.nukoneko.cuc.android.kidspos.ui.common;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;

public abstract class BaseActivity extends RxAppCompatActivity {
    protected KidsPOSApplication getApp() {
        return KidsPOSApplication.get(this);
    }
}
