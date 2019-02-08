package info.nukoneko.cuc.android.kidspos.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.ui.common.ErrorDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class SettingFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener, CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val myViewModel: SettingViewModel by viewModel()

    private val listener = object : SettingViewModel.Listener {
        override fun onShouldShowMessage(message: String) {
            launch {
                ErrorDialogFragment.showWithSuspend(requireFragmentManager(), message)
            }
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
