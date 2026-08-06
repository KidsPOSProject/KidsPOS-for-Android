package info.nukoneko.cuc.android.kidspos.di.hilt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import kotlinx.serialization.json.Json
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings",
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

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.settingsDataStore

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>,
        json: Json
    ): SettingsRepository = SettingsRepository(dataStore, json)
}
