package info.nukoneko.cuc.android.kidspos.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }
}
