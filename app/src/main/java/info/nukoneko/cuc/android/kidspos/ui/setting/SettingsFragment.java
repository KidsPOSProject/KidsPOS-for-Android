package info.nukoneko.cuc.android.kidspos.ui.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.text.TextUtils;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.ChangeStateEvent;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.util.MiscUtil;
import info.nukoneko.cuc.android.kidspos.util.manager.SettingsManager;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        updateSummaries();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case SettingsManager.KEY_ENABLE_PRACTICE_MODE:
                KPEventBusProvider.getInstance().send(new ChangeStateEvent());
                break;
            case SettingsManager.KEY_SERVER_IP:
                final String ip = sharedPreferences.getString(key, "");
                if (TextUtils.isEmpty(ip) || !MiscUtil.isIpAddressValid(ip)) {
                    AlertUtil.showErrorDialog(getContext(), "IPアドレスが間違っています", false, null);
                    sharedPreferences.edit().putString(key, SettingsManager.DEFAULT_IP).apply();
                    return;
                }
                break;
            case SettingsManager.KEY_SERVER_PORT:
                final String port = sharedPreferences.getString(key, "");
                if (TextUtils.isEmpty(port) || !MiscUtil.isPortValid(port)) {
                    AlertUtil.showErrorDialog(getContext(), "ポートが間違っています", false, null);
                    sharedPreferences.edit().putString(key, SettingsManager.DEFAULT_PORT).apply();
                    return;
                }
                break;
        }
        updateSummaries();
    }

    private void updateSummaries() {
        {
            EditTextPreference pref = (EditTextPreference) findPreference(SettingsManager.KEY_SERVER_IP);
            pref.setSummary(pref.getText());
        }
        {
            EditTextPreference pref = (EditTextPreference) findPreference(SettingsManager.KEY_SERVER_PORT);
            pref.setSummary(pref.getText());
        }
        {
            SwitchPreferenceCompat pref = (SwitchPreferenceCompat) findPreference(SettingsManager.KEY_ENABLE_PRACTICE_MODE);
            pref.setSummary(pref.isChecked() ? "練習モード" : "通常モード");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
