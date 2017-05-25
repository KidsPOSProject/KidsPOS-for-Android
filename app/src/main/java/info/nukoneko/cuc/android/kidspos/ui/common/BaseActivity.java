package info.nukoneko.cuc.android.kidspos.ui.common;

import android.support.v7.app.AppCompatActivity;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;

public abstract class BaseActivity extends AppCompatActivity {
    protected KidsPOSApplication getApp() {
        return KidsPOSApplication.get(this);
    }
}
