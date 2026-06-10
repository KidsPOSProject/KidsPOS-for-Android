package info.nukoneko.cuc.android.kidspos.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// GlobalConfig と同時稼働させると SharedPreferencesMigration が移行済みキーを
// SharedPreferences から削除して設定が分裂するため、旧設定機構を撤去するまで DI には接続しない。
class SettingsRepository(
    private val context: Context,
    private val json: Json
) {
    private val dataStore: DataStore<Preferences> = context.settingsDataStore

    val serverAddress: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_SERVER_ADDRESS] ?: DEFAULT_SERVER_ADDRESS
    }

    val runningMode: Flow<Mode> = dataStore.data.map { prefs ->
        Mode.nameOf(prefs[KEY_RUNNING_MODE])
    }

    val currentStore: Flow<Store?> = dataStore.data.map { prefs ->
        prefs[KEY_STORE]?.let { json.decodeFromString<Store>(it) }
    }

    val currentStaff: Flow<Staff?> = dataStore.data.map { prefs ->
        prefs[KEY_STAFF]?.let { json.decodeFromString<Staff>(it) }
    }

    suspend fun setServerAddress(value: String) {
        dataStore.edit { prefs -> prefs[KEY_SERVER_ADDRESS] = value }
    }

    suspend fun setRunningMode(value: Mode) {
        dataStore.edit { prefs -> prefs[KEY_RUNNING_MODE] = value.name }
    }

    suspend fun setCurrentStore(value: Store?) {
        dataStore.edit { prefs ->
            if (value != null) prefs[KEY_STORE] = json.encodeToString(value)
            else prefs.remove(KEY_STORE)
        }
    }

    suspend fun setCurrentStaff(value: Staff?) {
        dataStore.edit { prefs ->
            if (value != null) prefs[KEY_STAFF] = json.encodeToString(value)
            else prefs.remove(KEY_STAFF)
        }
    }

    companion object {
        private const val DATASTORE_NAME = "settings"
        const val DEFAULT_SERVER_ADDRESS = "http://192.168.0.220:8080"

        val KEY_SERVER_ADDRESS = stringPreferencesKey("setting_server_info")
        val KEY_RUNNING_MODE = stringPreferencesKey("setting_running_mode")
        val KEY_STORE = stringPreferencesKey("store")
        val KEY_STAFF = stringPreferencesKey("staff")

        private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
            name = DATASTORE_NAME,
            produceMigrations = { appContext ->
                listOf(
                    SharedPreferencesMigration(
                        context = appContext,
                        sharedPreferencesName = "${appContext.packageName}_preferences",
                        keysToMigrate = setOf(
                            "setting_server_info",
                            "setting_running_mode",
                            "store",
                            "staff"
                        )
                    )
                )
            }
        )
    }
}
