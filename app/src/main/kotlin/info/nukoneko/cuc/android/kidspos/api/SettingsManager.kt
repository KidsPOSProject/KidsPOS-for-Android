package info.nukoneko.cuc.android.kidspos.api

import android.content.Context
import android.support.v7.preference.PreferenceManager

class SettingsManager(context: Context) {

    private val preference = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    var serverIP: String
        get() = preference.getString(KEY_SERVER_IP, DEFAULT_IP) ?: DEFAULT_IP
        set(ip) = preference.edit().putString(KEY_SERVER_IP, ip).apply()

    var serverPort: Int
        get() = preference.getInt(KEY_SERVER_PORT, DEFAULT_PORT)
        set(port) = preference.edit().putInt(KEY_SERVER_PORT, port).apply()

    var ipPort: String = "$serverIP:$serverPort"

    var isPracticeModeEnabled: Boolean
        get() = preference.getBoolean(KEY_ENABLE_PRACTICE_MODE, true)
        set(isEnabled) = preference.edit().putBoolean(KEY_ENABLE_PRACTICE_MODE, isEnabled).apply()

    companion object {
        const val DEFAULT_IP = "192.168.0.220"
        const val DEFAULT_PORT = 9500
        const val KEY_SERVER_IP = "settings_server_ip"
        const val KEY_SERVER_PORT = "settings_server_port"
        const val KEY_ENABLE_PRACTICE_MODE = "settings_enable_practice_mode"
    }
}
