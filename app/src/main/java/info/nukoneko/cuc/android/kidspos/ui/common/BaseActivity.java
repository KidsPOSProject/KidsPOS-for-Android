package info.nukoneko.cuc.android.kidspos.ui.common;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import info.nukoneko.cuc.android.kidspos.KPApplicationController;

public abstract class BaseActivity extends RxAppCompatActivity {
    protected KPApplicationController getApp() {
        return KPApplicationController.get(this);
    }
}
