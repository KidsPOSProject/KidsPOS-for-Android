package info.nukoneko.cuc.android.kidspos.ui.setting

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import android.text.TextUtils
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.ApplicationEvent
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.util.ValidationUtil

class SettingViewModel(private val config: GlobalConfig, private val eventBus: EventBus) : ViewModel() {

    var listener: Listener? = null

    fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            GlobalConfig.KEY_ENABLE_PRACTICE_MODE -> {
                eventBus.post(ApplicationEvent.AppModeChange)
            }
            GlobalConfig.KEY_SERVER_URL -> {
                sharedPreferences?.let {
                    val ip = it.getString(key, "")
                    if (TextUtils.isEmpty(ip) || !ValidationUtil.isValidIPv4Address(ip!!)) {
                        listener?.onShouldShowMessage("IPアドレスが間違っています")
                        config.serverUrl = GlobalConfig.DEFAULT_SERVER_URL
                        return
                    }
                    eventBus.post(SystemEvent.HostChanged)
                }
            }
            GlobalConfig.KEY_SERVER_PORT -> {
                sharedPreferences?.let {
                    val port = it.getString(key, "")
                    val portValue = port?.toInt()
                    if (TextUtils.isEmpty(port) || portValue == null || !ValidationUtil.isUsablePort(portValue)) {
                        listener?.onShouldShowMessage("ポートが間違っています")
                        config.serverPort = GlobalConfig.DEFAULT_SERVER_PORT_VALUE
                        return
                    }
                    eventBus.post(SystemEvent.HostChanged)
                }
            }
        }
    }

    interface Listener {
        fun onShouldShowMessage(message: String)
    }
}