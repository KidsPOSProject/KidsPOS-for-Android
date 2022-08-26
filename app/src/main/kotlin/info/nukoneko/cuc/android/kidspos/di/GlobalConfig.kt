package info.nukoneko.cuc.android.kidspos.di

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GlobalConfig(context: Context, private val eventBus: EventBus) {
    private val preference =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    var currentServerAddress: String
        get() = preference.getString(
            KEY_SERVER_INFO,
            DEFAULT_SERVER_INFO
        ) ?: DEFAULT_SERVER_INFO
        set(value) {
            preference.edit {
                putString(KEY_SERVER_INFO, value)
            }
            eventBus.post(SystemEvent.ServerAddressChanged(value))
        }

    var currentRunningMode: Mode
        get() = Mode.nameOf(
            preference.getString(
                KEY_RUNNING_MODE,
                Mode.PRACTICE.name
            )
        )
        set(value) {
            preference.edit {
                putString(KEY_RUNNING_MODE, value.name)
            }
            eventBus.post(SystemEvent.RunningModeChanged(value))
        }

    var currentStore: Store?
        get() {
            return preference.getString(KEY_STORE, null)?.let {
                return Json.decodeFromString<Store>(it)
            }
        }
        set(value) {
            preference.edit {
                putString(KEY_STORE, Json.encodeToString(value))
            }
            eventBus.post(SystemEvent.SelectShop(value))
        }

    var currentStaff: Staff?
        get() {
            return preference.getString(KEY_STAFF, null)?.let {
                return Json.decodeFromString<Staff>(it)
            }
        }
        set(value) {
            preference.edit {
                putString(KEY_STAFF, Json.encodeToString(value))
            }
        }

    companion object {
        const val KEY_SERVER_INFO = "setting_server_info"
        const val KEY_RUNNING_MODE = "setting_running_mode"
        const val DEFAULT_SERVER_INFO = "http://192.168.0.220:8080"
        const val KEY_STORE = "store"
        const val KEY_STAFF = "staff"
    }
}
