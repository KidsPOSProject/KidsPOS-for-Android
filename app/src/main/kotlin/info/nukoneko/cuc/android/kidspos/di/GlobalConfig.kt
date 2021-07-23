package info.nukoneko.cuc.android.kidspos.di

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.google.gson.GsonBuilder
import info.nukoneko.cuc.android.kidspos.domain.entity.Staff
import info.nukoneko.cuc.android.kidspos.domain.entity.Store
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.util.Mode

class GlobalConfig(context: Context, private val eventBus: EventBus) {
    private val preference =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private val gson = GsonBuilder().create()

    var currentServerAddress: String
        get() = preference.getString(
            KEY_SERVER_INFO,
            DEFAULT_SERVER_INFO
        ) ?: DEFAULT_SERVER_INFO
        set(value) {
            preference.edit().putString(KEY_SERVER_INFO, value).apply()
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
            preference.edit().putString(KEY_RUNNING_MODE, value.name).apply()
            eventBus.post(SystemEvent.RunningModeChanged(value))
        }

    var currentStore: Store?
        get() {
            return preference.getString(KEY_STORE, null)?.let {
                try {
                    return gson.fromJson(it, Store::class.java)
                } catch (e: Throwable) {
                    // Parse error
                    preference.edit {
                        remove(KEY_STORE)
                    }
                    null
                }
            }
        }
        set(value) {
            preference.edit().putString(
                KEY_STORE,
                gson.toJson(value)
            ).apply()
            eventBus.post(SystemEvent.SelectShop(value))
        }

    var currentStaff: Staff?
        get() {
            return preference.getString(KEY_STAFF, null)?.let {
                try {
                    return gson.fromJson(it, Staff::class.java)
                } catch (e: Throwable) {
                    // Parse error
                    preference.edit {
                        remove(KEY_STAFF)
                    }
                    null
                }
            }
        }
        set(value) {
            preference.edit().putString(
                KEY_STAFF,
                gson.toJson(value)
            ).apply()
        }

    companion object {
        const val KEY_SERVER_INFO = "setting_server_info"
        const val KEY_RUNNING_MODE = "setting_running_mode"
        const val DEFAULT_SERVER_INFO = "http://192.168.0.220:8080"
        const val KEY_STORE = "store"
        const val KEY_STAFF = "staff"
    }
}
