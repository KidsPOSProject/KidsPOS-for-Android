package info.nukoneko.cuc.android.kidspos.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import info.nukoneko.cuc.android.kidspos.databinding.FragmentSettingBinding
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.util.Mode
import org.koin.android.ext.android.inject

class SettingFragment : Fragment() {
    private val config: GlobalConfig by inject()
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loadSettingButton.setOnClickListener {
            launchQrReader()
        }

        binding.changeModeButton.setOnClickListener {
            val newMode = when (config.currentRunningMode) {
                Mode.PRODUCTION -> Mode.PRACTICE
                Mode.PRACTICE -> Mode.PRODUCTION
            }
            config.currentRunningMode = newMode
            Toast.makeText(requireContext(), "${newMode.modeName} モードへ切り替えました", Toast.LENGTH_SHORT)
                .show()
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
        binding.currentServerAddress.text = config.currentServerAddress

        val currentMode = config.currentRunningMode
        binding.currentRunningModeText.text = "現在は ${currentMode.modeName} モードです"

        val nextMode = when (currentMode) {
            Mode.PRODUCTION -> Mode.PRACTICE
            Mode.PRACTICE -> Mode.PRODUCTION
        }
        binding.changeModeButton.text = "${nextMode.modeName} に切り替える"
    }

    companion object {
        fun newInstance() = SettingFragment()
    }
}
