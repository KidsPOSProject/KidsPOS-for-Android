package info.nukoneko.cuc.android.kidspos.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingFragment()).commit();
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }
}
