package info.nukoneko.cuc.android.kidspos.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity;

public final class SettingsActivity extends BaseActivity {
    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment()).commit();
    }
}
