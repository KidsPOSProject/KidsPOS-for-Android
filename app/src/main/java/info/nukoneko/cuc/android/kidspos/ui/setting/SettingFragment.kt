package info.nukoneko.cuc.android.kidspos.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat
import android.text.TextUtils

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.api.manager.SettingsManager
import info.nukoneko.cuc.android.kidspos.event.ApplicationEvent
import info.nukoneko.cuc.android.kidspos.util.AlertUtil
import info.nukoneko.cuc.android.kidspos.util.ValidationUtil

class SettingFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        addPreferencesFromResource(R.xml.settings)
        updateSummaries()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val app = KidsPOSApplication[context] ?: return

        when (key) {
            SettingsManager.KEY_ENABLE_PRACTICE_MODE -> app.postEvent(ApplicationEvent.AppModeChange)
            SettingsManager.KEY_SERVER_IP -> {
                val ip = sharedPreferences.getString(key, "")
                if (TextUtils.isEmpty(ip) || !ValidationUtil.isValidIPv4Address(ip!!)) {
                    AlertUtil.showErrorDialog(context!!, "IPアドレスが間違っています", false, null)
                    sharedPreferences.edit().putString(key, SettingsManager.DEFAULT_IP).apply()
                    return
                }
            }
            SettingsManager.KEY_SERVER_PORT -> {
                val port = sharedPreferences.getInt(key, -1)
                if (!ValidationUtil.isUsablePort(port)) {
                    AlertUtil.showErrorDialog(context!!, "ポートが間違っています", false, null)
                    sharedPreferences.edit().putInt(key, SettingsManager.DEFAULT_PORT).apply()
                    return
                }
            }
        }
        updateSummaries()
    }

    private fun updateSummaries() {
        run {
            val pref = findPreference(SettingsManager.KEY_SERVER_IP) as EditTextPreference
            pref.summary = pref.text
        }
        run {
            val pref = findPreference(SettingsManager.KEY_SERVER_PORT) as EditTextPreference
            pref.summary = pref.text
        }
        run {
            val pref = findPreference(SettingsManager.KEY_ENABLE_PRACTICE_MODE) as SwitchPreferenceCompat
            pref.summary = if (pref.isChecked) "練習モード" else "通常モード"
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    companion object {
        fun newInstance(): SettingFragment = SettingFragment()
    }
}
