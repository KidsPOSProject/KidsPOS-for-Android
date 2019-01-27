package info.nukoneko.cuc.android.kidspos.di

import android.content.Context
import android.preference.PreferenceManager
import android.support.annotation.RestrictTo
import com.google.gson.GsonBuilder
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class GlobalConfig(context: Context, private val eventBus: EventBus) {
    private val preference = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private val gson = GsonBuilder().create()

    private var defaultSubscribeScheduler: Scheduler? = null
    fun getDefaultSubscribeScheduler(): Scheduler {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io()
        }
        return defaultSubscribeScheduler!!
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    fun setDefaultSubscribeScheduler(subscribeScheduler: Scheduler) {
        defaultSubscribeScheduler = subscribeScheduler
    }

    var baseUrl: String = "$DEFAULT_SERVER_URL:$DEFAULT_SERVER_PORT"
        get() = "http://$serverUrl:$serverPort"

    var serverUrl: String
        get() = preference.getString(
                KEY_SERVER_URL,
                DEFAULT_SERVER_URL
        ) ?: DEFAULT_SERVER_URL
        set(ip) {
            preference.edit().putString(KEY_SERVER_URL, ip).apply()
            eventBus.post(SystemEvent.HostChanged)
        }

    var serverPort: Int
        get() {
            return preference.getString(KEY_SERVER_PORT, null)?.let {
                return try {
                    it.toInt()
                } catch (e: ClassCastException) {
                    preference.edit().putString(KEY_SERVER_URL, DEFAULT_SERVER_PORT).apply()
                    return DEFAULT_SERVER_PORT_VALUE
                }
            } ?: kotlin.run {
                preference.edit().putString(KEY_SERVER_URL, DEFAULT_SERVER_PORT).apply()
                return DEFAULT_SERVER_PORT_VALUE
            }
        }
        set(port) {
            preference.edit().putString(KEY_SERVER_URL, port.toString()).apply()
            eventBus.post(SystemEvent.HostChanged)
        }

    var isPracticeModeEnabled: Boolean
        get() = preference.getBoolean(
                KEY_ENABLE_PRACTICE_MODE,
                true
        )
        set(value) = preference.edit().putBoolean(KEY_ENABLE_PRACTICE_MODE, value).apply()

    var currentStore: Store?
        get() {
            preference.getString(KEY_STORE, null)?.let {
                return gson.fromJson(it, Store::class.java)
            }
        }
        set(value) {
            preference.edit().putString(
                    KEY_STORE,
                    gson.toJson(value)
            ).apply()
        }

    var currentStaff: Staff?
        get() {
            preference.getString(KEY_STAFF, null)?.let {
                return gson.fromJson(it, Staff::class.java)
            }
        }
        set(value) {
            preference.edit().putString(
                    KEY_STAFF,
                    gson.toJson(value)
            ).apply()
        }

    companion object {
        const val KEY_SERVER_URL = "settings_server_url"
        const val KEY_SERVER_PORT = "settings_server_port"
        const val KEY_ENABLE_PRACTICE_MODE = "settings_enable_practice_mode"
        const val KEY_STORE = "store"
        const val KEY_STAFF = "staff"
        const val DEFAULT_SERVER_URL = "192.128.0.220"
        const val DEFAULT_SERVER_PORT = "8080"
        const val DEFAULT_SERVER_PORT_VALUE = 8080
    }
}