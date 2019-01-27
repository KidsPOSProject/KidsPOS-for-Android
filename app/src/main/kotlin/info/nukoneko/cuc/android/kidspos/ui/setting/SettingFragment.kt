package info.nukoneko.cuc.android.kidspos.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.util.AlertUtil
import org.koin.android.viewmodel.ext.android.viewModel

class SettingFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val myViewModel: SettingViewModel by viewModel()

    private val listener = object : SettingViewModel.Listener {
        override fun onShouldShowMessage(message: String) {
            AlertUtil.showErrorDialog(context!!, message, false, null)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        myViewModel.listener = listener
        addPreferencesFromResource(R.xml.settings)
        updateSummaries()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        myViewModel.onSharedPreferenceChanged(sharedPreferences, key)
        updateSummaries()
    }

    private fun updateSummaries() {
        run {
            val pref = findPreference(GlobalConfig.KEY_SERVER_URL) as EditTextPreference
            pref.summary = pref.text
        }
        run {
            val pref = findPreference(GlobalConfig.KEY_SERVER_PORT) as EditTextPreference
            pref.summary = pref.text
        }
        run {
            val pref = findPreference(GlobalConfig.KEY_ENABLE_PRACTICE_MODE) as SwitchPreferenceCompat
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
