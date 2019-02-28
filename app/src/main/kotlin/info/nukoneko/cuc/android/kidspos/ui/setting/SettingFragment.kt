package info.nukoneko.cuc.android.kidspos.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.android.synthetic.main.fragment_setting.*
import org.koin.android.ext.android.inject

/**
 * このクラスはDataBinding + ViewModelを使っていない
 */
class SettingFragment : Fragment() {
    private val config: GlobalConfig by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettingButton.setOnClickListener {
            launchQrReader()
        }

        changeModeButton.setOnClickListener {
            val newMode = when (config.currentRunningMode) {
                Mode.PRODUCTION -> Mode.PRACTICE
                Mode.PRACTICE -> Mode.PRODUCTION
            }
            config.currentRunningMode = newMode
            Toast.makeText(requireContext(), "${newMode.modeName} モードへ切り替えました", Toast.LENGTH_SHORT).show()
            updateValues()
        }
    }

    override fun onResume() {
        super.onResume()
        updateValues()
    }

    private fun launchQrReader() {
        if (activity is SettingActivity) {
            IntentIntegrator(activity).initiateScan()
        }
    }

    private fun updateValues() {
        currentServerAddress.text = config.currentServerAddress

        val currentMode = config.currentRunningMode
        currentRunningModeText.text = "現在は ${currentMode.modeName} モードです"

        val nextMode = when (currentMode) {
            Mode.PRODUCTION -> Mode.PRACTICE
            Mode.PRACTICE -> Mode.PRODUCTION
        }
        changeModeButton.text = "${nextMode.modeName} に切り替える"
    }

    companion object {
        fun newInstance() = SettingFragment()
    }
}